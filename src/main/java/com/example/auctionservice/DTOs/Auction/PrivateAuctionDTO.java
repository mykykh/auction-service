package com.example.auctionservice.DTOs.Auction;

import com.example.auctionservice.Models.AuctionStatus;
import com.example.auctionservice.Models.Category;

import java.math.BigDecimal;
import java.time.Instant;

public record PrivateAuctionDTO (long id, String title, String description, BigDecimal startingPrice,
                                 AuctionStatus auctionStatus, Category category, long userId,
                                 Instant expirationTime, Instant updatedTime, Instant createdTime) {
}
