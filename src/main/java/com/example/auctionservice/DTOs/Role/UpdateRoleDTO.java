package com.example.auctionservice.DTOs.Role;

import com.example.auctionservice.Models.Privilege;

import java.util.List;

public record UpdateRoleDTO (long id, String name, List<Privilege> privileges){
}
