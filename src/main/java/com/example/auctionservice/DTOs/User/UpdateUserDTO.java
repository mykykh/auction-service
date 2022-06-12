package com.example.auctionservice.DTOs.User;

import java.math.BigDecimal;

public record UpdateUserDTO(long id, String username, BigDecimal balance) { }
