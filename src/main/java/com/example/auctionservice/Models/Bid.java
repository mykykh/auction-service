package com.example.auctionservice.Models;

import java.math.BigDecimal;

public class Bid {
    private long id = 0;
    private BigDecimal amount = BigDecimal.valueOf(0);
    private BidStatus bidStatus;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BidStatus getBetStatus() {
        return bidStatus;
    }

    public void setBetStatus(BidStatus bidStatus) {
        this.bidStatus = bidStatus;
    }
}
