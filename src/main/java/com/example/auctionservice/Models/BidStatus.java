package com.example.auctionservice.Models;

public class BidStatus {
    private long id;
    private String name;

    public BidStatus(long id, String name){
        this.id = id;
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
