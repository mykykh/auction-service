package com.example.auctionservice.Repositories;

import com.example.auctionservice.Models.Auction;
import com.example.auctionservice.ResultSetExtractors.AuctionExtractor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class AuctionRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String GET_ALL_QUERY = "SELECT * FROM auctions a, auction_statuses s, categories c WHERE a.status_id = s.status_id AND c.category_id = a.category_id GROUP BY a.auction_id, s.status_id, c.category_id";
    private static final String GET_ALL_BY_USER_ID_QUERY = "SELECT * FROM auctions a, auction_statuses s, categories c WHERE a.status_id = s.status_id AND c.category_id = a.category_id AND a.user_id = ? GROUP BY a.auction_id, s.status_id, c.category_id";
    private static final String GET_ALL_EXPIRED_QUERY = "SELECT * FROM auctions a, auction_statuses s, categories c WHERE a.status_id = s.status_id AND c.category_id = a.category_id AND a.expiration_time <= current_timestamp AND a.status_id=1 GROUP BY a.auction_id, s.status_id, c.category_id";
    private static final String UPDATE_QUERY = "UPDATE auctions SET auction_title=?, auction_description=?, expiration_time=?, status_id=?, category_id=?, updated_time=current_timestamp WHERE auction_id=?";
    public AuctionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Auction> getAll(){
        return jdbcTemplate.query(GET_ALL_QUERY, new AuctionExtractor());
    }
    public List<Auction> getAllByUserId(long userId){
        return jdbcTemplate.query(GET_ALL_BY_USER_ID_QUERY, new AuctionExtractor(), userId);
    }
    public List<Auction> getAllExpired(){
        return jdbcTemplate.query(GET_ALL_EXPIRED_QUERY, new AuctionExtractor());
    }

    public void updateAuction(Auction auction){
        jdbcTemplate.update(UPDATE_QUERY, auction.getTitle(), auction.getDescription(), Timestamp.from(auction.getExpirationTime()), auction.getAuctionStatus().getId(), auction.getCategory().getId(), auction.getId());
    }
}
