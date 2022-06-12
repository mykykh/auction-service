package com.example.auctionservice.DTOs.Auction;

import com.example.auctionservice.Models.AuctionStatus;
import com.example.auctionservice.Models.Category;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record CreateAuctionDTO (long user_id, String title, String description, BigDecimal startingPrice,
                                AuctionStatus auctionStatus, Category category, Instant expirationTime) {
}
