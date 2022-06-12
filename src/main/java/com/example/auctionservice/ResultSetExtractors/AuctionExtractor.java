package com.example.auctionservice.ResultSetExtractors;

import com.example.auctionservice.Models.Auction;
import com.example.auctionservice.Models.AuctionStatus;
import com.example.auctionservice.Models.Category;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class AuctionExtractor implements ResultSetExtractor<List<Auction>> {
    @Override
    public List<Auction> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Auction> auctions = new ArrayList<>();
        if (!rs.isBeforeFirst()) return auctions;
        while (rs.next()){
            Auction auction = new Auction();
            auction.setId(rs.getLong("auction_id"));
            auction.setTitle(rs.getString("auction_title"));
            auction.setDescription(rs.getString("auction_description"));
            auction.setAuctionStatus(new AuctionStatus(rs.getLong("status_id"), rs.getString("status_name")));
            auction.setCategory(new Category(rs.getLong("category_id"), rs.getString("category_name")));
            auction.setExpirationTime(rs.getTimestamp("expiration_time", Calendar.getInstance(TimeZone.getTimeZone("UTC"))).toInstant());
            auctions.add(auction);
        }
        return auctions;
    }
}
