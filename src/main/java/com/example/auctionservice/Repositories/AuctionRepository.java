package com.example.auctionservice.Repositories;

import com.example.auctionservice.Models.Auction;
import com.example.auctionservice.ResultSetExtractors.AuctionExtractor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuctionRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String GET_ALL_QUERY = "SELECT * FROM auctions a, auction_statuses s, categories c WHERE a.status_id = s.status_id AND c.category_id = a.category_id GROUP BY a.auction_id, s.status_id, c.category_id";
    private static final String GET_ALL_BY_USER_ID_QUERY = "SELECT * FROM auctions a, auction_statuses s WHERE a.status_id = s.status_id AND a.user_id = ? GROUP BY a.auction_id, s.status_id";

    public AuctionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Auction> getAll(){
        return jdbcTemplate.query(GET_ALL_QUERY, new AuctionExtractor());
    }
    public List<Auction> getAllByUserId(long userId){
        return jdbcTemplate.query(GET_ALL_BY_USER_ID_QUERY, new AuctionExtractor(), userId);
    }
}
