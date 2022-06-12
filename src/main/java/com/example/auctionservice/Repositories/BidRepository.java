package com.example.auctionservice.Repositories;

import com.example.auctionservice.Models.Bid;
import com.example.auctionservice.ResultSetExtractors.BidExtractor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BidRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String GET_ALL_QUERY = "SELECT * FROM bids b, bid_statuses s WHERE b.status_id = s.status_id GROUP BY b.bid_id, s.status_id";
    private static final String GET_ALL_BY_USER_ID_QUERY = "SELECT * FROM bids b, bid_statuses s WHERE b.status_id = s.status_id AND b.user_id = ? GROUP BY b.bid_id, s.status_id";

    public BidRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Bid> getAll(){
        return jdbcTemplate.query(GET_ALL_QUERY, new BidExtractor());
    }
    public List<Bid> getAllByUserId(long userId){
        return jdbcTemplate.query(GET_ALL_BY_USER_ID_QUERY, new BidExtractor(), userId);
    }
}
