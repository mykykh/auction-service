package com.example.auctionservice.DTOs.User;

import com.example.auctionservice.Models.Role;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;

public record PrivateUserDTO (long id, String username, BigDecimal balance, Collection<Role> roles, Instant updatedTime, Instant createdTime){}
