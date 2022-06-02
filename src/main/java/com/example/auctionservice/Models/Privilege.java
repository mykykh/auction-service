package com.example.auctionservice.Models;

public class Privilege {
    private Long id;
    private String name;

    public Privilege(Long id, String name){
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
