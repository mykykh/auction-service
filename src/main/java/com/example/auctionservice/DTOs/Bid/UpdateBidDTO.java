package com.example.auctionservice.DTOs.Bid;

import com.example.auctionservice.Models.BidStatus;

public record UpdateBidDTO (long id, BidStatus bidStatus){
}
