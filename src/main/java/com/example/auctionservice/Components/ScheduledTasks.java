package com.example.auctionservice.Components;

import com.example.auctionservice.Models.Auction;
import com.example.auctionservice.Models.AuctionStatus;
import com.example.auctionservice.Repositories.AuctionRepository;
import com.example.auctionservice.Repositories.AuctionStatusRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduledTasks {

    private final AuctionRepository auctionRepository;
    private final AuctionStatusRepository auctionStatusRepository;

    public ScheduledTasks(AuctionRepository auctionRepository, AuctionStatusRepository auctionStatusRepository) {
        this.auctionRepository = auctionRepository;
        this.auctionStatusRepository = auctionStatusRepository;
    }

    @Scheduled(fixedRate = 1000)
    public void checkAuctionsExpireTime(){
        List<Auction> expiredAuctions= auctionRepository.getAllExpired();
        AuctionStatus StatusClosed = auctionStatusRepository.findByName("CLOSED")
                .orElseThrow(() -> new UsernameNotFoundException("Status not found"));
        for (Auction expiredAuction : expiredAuctions){
            expiredAuction.setAuctionStatus(StatusClosed);
            auctionRepository.updateAuction(expiredAuction);
        }
    }
}
