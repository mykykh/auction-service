package com.example.auctionservice.DTOs.User;

import com.example.auctionservice.Models.Role;

import java.util.Collection;

public record CreateUserDTO(String username, String password, Collection<Role> roles, boolean enabled) { }
