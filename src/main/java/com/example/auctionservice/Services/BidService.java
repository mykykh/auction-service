package com.example.auctionservice.Services;

import com.example.auctionservice.DTOs.Bid.CreateBidDTO;
import com.example.auctionservice.DTOs.Bid.PrivateBidDTO;
import com.example.auctionservice.DTOs.Bid.PublicBidDTO;
import com.example.auctionservice.Models.Bid;
import com.example.auctionservice.Models.BidStatus;
import com.example.auctionservice.Repositories.BidRepository;
import com.example.auctionservice.Repositories.BidStatusRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BidService {
    private final BidRepository bidRepository;
    private final BidStatusRepository bidStatusRepository;

    public BidService(BidRepository bidRepository, BidStatusRepository bidStatusRepository) {
        this.bidRepository = bidRepository;
        this.bidStatusRepository = bidStatusRepository;
    }

    public Optional<BidStatus> findByName(String statusName){
        return bidStatusRepository.findByName(statusName);
    }

    public List<PublicBidDTO> getAllPublicDataByAuctionId(long auctionId){
        return bidRepository.getAllByAuctionId(auctionId).stream().map(this::convertToPublicDto).collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('SELECT_USERS') or #userId == authentication.principal.id")
    public List<PrivateBidDTO> getAllBidPrivateDataByUserId(long userId){
        return bidRepository.getAllByUserId(userId).stream().map(this::convertToPrivateDto).collect(Collectors.toList());
    }

    @PreAuthorize("#createBidDTO.userId() == authentication.principal.id")
    public void creatBid(CreateBidDTO createBidDTO){
        bidRepository.createBid(createBidDTO);
    }

    public PublicBidDTO convertToPublicDto(Bid bid){
        return new PublicBidDTO(bid.getId(), bid.getAmount(), bid.getBidStatus(), bid.getUserId(), bid.getAuctionId(), bid.getCreatedTime());
    }

    public PrivateBidDTO convertToPrivateDto(Bid bid){
        return new PrivateBidDTO(bid.getId(), bid.getAmount(), bid.getBidStatus(), bid.getUserId(), bid.getAuctionId(), bid.getCreatedTime());
    }
}
