package com.example.auctionservice.DTOs.Bid;

import com.example.auctionservice.Models.BidStatus;

import java.math.BigDecimal;

public record CreateBidDTO (long id, BigDecimal amount, BidStatus bidStatus, long userId, long auctionId){
}
