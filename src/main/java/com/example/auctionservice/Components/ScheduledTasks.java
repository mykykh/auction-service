package com.example.auctionservice.Components;

import com.example.auctionservice.DTOs.Auction.UpdateAuctionDTO;
import com.example.auctionservice.DTOs.Bid.UpdateBidDTO;
import com.example.auctionservice.DTOs.User.UpdateUserDTO;
import com.example.auctionservice.Models.*;
import com.example.auctionservice.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduledTasks {

    private final AuctionRepository auctionRepository;
    private final AuctionStatusRepository auctionStatusRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final BidStatusRepository bidStatusRepository;

    public ScheduledTasks(AuctionRepository auctionRepository,
                          AuctionStatusRepository auctionStatusRepository,
                          BidRepository bidRepository,
                          UserRepository userRepository, BidStatusRepository bidStatusRepository) {
        this.auctionRepository = auctionRepository;
        this.auctionStatusRepository = auctionStatusRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.bidStatusRepository = bidStatusRepository;
    }

    @Scheduled(fixedRate = 10000)
    public void checkAuctionsExpireTime(){
        List<Auction> expiredAuctions= auctionRepository.getAllExpired();

        Optional<AuctionStatus> auctionStatusClosedOptional = auctionStatusRepository.findByName("CLOSED");
        Optional<BidStatus> bidStatusLostOptional = bidStatusRepository.findByName("LOST");
        Optional<BidStatus> bidStatusWonOptional = bidStatusRepository.findByName("WON");
        if (auctionStatusClosedOptional.isEmpty() || bidStatusLostOptional.isEmpty() || bidStatusWonOptional.isEmpty()) return;

        AuctionStatus auctionStatusClosed = auctionStatusClosedOptional.get();
        BidStatus bidStatusLost = bidStatusLostOptional.get();
        BidStatus bidStatusWon = bidStatusWonOptional.get();

        for (Auction expiredAuction : expiredAuctions){
            expiredAuction.setAuctionStatus(auctionStatusClosed);

            Optional<User> auctionOwner = userRepository.findById(expiredAuction.getUserId());
            if (auctionOwner.isEmpty()) continue;

            List<Bid> bids = bidRepository.getAllByAuctionId(expiredAuction.getId());

            bids.sort((bid1, bid2) -> bid1.getAmount().compareTo(bid2.getAmount()));

            if (!bids.isEmpty()){
                Bid largestBid = bids.remove(bids.size() - 1);

                bidRepository.updateBid(new UpdateBidDTO(largestBid.getId(), bidStatusWon));
                userRepository.update(new UpdateUserDTO(auctionOwner.get().getId(),
                        auctionOwner.get().getUsername(),
                        auctionOwner.get().getBalance().add(largestBid.getAmount())));

                for (Bid bid: bids){
                    Optional<User> bidOwner = userRepository.findById(bid.getUserId());
                    if (bidOwner.isEmpty()) continue;
                    bidRepository.updateBid(new UpdateBidDTO(bid.getId(), bidStatusLost));
                    userRepository.update(new UpdateUserDTO(bidOwner.get().getId(),
                            bidOwner.get().getUsername(),
                            bidOwner.get().getBalance().add(bid.getAmount())));
                }
            }

            auctionRepository.updateAuction(new UpdateAuctionDTO(expiredAuction.getUserId(),
                    expiredAuction.getId(),
                    expiredAuction.getTitle(),
                    expiredAuction.getDescription(),
                    expiredAuction.getStartingPrice(),
                    expiredAuction.getAuctionStatus(),
                    expiredAuction.getCategory(),
                    expiredAuction.getExpirationTime()));
        }
    }
}
