package com.example.auctionservice.DTOs.Role;

import com.example.auctionservice.Models.Privilege;

import java.util.List;

public record CreateRoleDTO (String name, List<Privilege> privileges){
}
