package com.example.auctionservice.DTOs.Bid;

import com.example.auctionservice.Models.BidStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PublicBidDTO (long id, BigDecimal amount, BidStatus bidStatus, long userId, long auctionId, Instant createdTime){
}
