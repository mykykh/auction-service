package com.example.auctionservice.DTOs.User;

import com.example.auctionservice.Models.Role;

import java.math.BigDecimal;
import java.util.List;

public record UpdateUserAndUserRolesDTO (long id, String username, BigDecimal balance, List<Role> roles) { }
