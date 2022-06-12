package com.example.auctionservice.Repositories;

import com.example.auctionservice.DTOs.Bid.CreateBidDTO;
import com.example.auctionservice.DTOs.Bid.UpdateBidDTO;
import com.example.auctionservice.Models.Bid;
import com.example.auctionservice.ResultSetExtractors.BidExtractor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class BidRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String GET_ALL_QUERY = "SELECT * FROM bids b, bid_statuses s WHERE b.status_id = s.status_id GROUP BY b.bid_id, s.status_id";
    private static final String GET_ALL_BY_USER_ID_QUERY = "SELECT * FROM bids b, bid_statuses s WHERE b.status_id = s.status_id AND b.user_id = ? GROUP BY b.bid_id, s.status_id ORDER BY b.created_time DESC";
    private static final String GET_ALL_BY_AUCTION_ID_QUERY = "SELECT * FROM bids b, bid_statuses s WHERE b.status_id = s.status_id AND b.auction_id = ? GROUP BY b.bid_id, s.status_id ORDER BY b.bid_amount DESC";
    private static final String INSERT_BID_QUERY = "INSERT INTO bids(bid_amount, status_id, user_id, auction_id) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_BID_QUERY = "UPDATE bids SET status_id=? WHERE bid_id=?";
    private static final String GET_MAX_AMOUNT_BY_AUCTION_ID_QUERY = "SELECT MAX(bid_amount) FROM bids WHERE auction_id = ?";
    public BidRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Bid> getAll(){
        return jdbcTemplate.query(GET_ALL_QUERY, new BidExtractor());
    }
    public List<Bid> getAllByUserId(long userId){
        return jdbcTemplate.query(GET_ALL_BY_USER_ID_QUERY, new BidExtractor(), userId);
    }
    public List<Bid> getAllByAuctionId(long auctionId){
        return jdbcTemplate.query(GET_ALL_BY_AUCTION_ID_QUERY, new BidExtractor(), auctionId);
    }

    public Optional<BigDecimal> getMaxAmountByAuctionId(long auctionId){
        BigDecimal result = jdbcTemplate.queryForObject(GET_MAX_AMOUNT_BY_AUCTION_ID_QUERY, BigDecimal.class, auctionId);
        if (result == null) return Optional.empty();
        else return Optional.of(result);
    }

    public void createBid(CreateBidDTO createBidDTO){
        jdbcTemplate.update(INSERT_BID_QUERY, createBidDTO.amount(), createBidDTO.bidStatus().getId(), createBidDTO.userId(), createBidDTO.auctionId());
    }

    public void updateBid(UpdateBidDTO updateBidDTO){
        jdbcTemplate.update(UPDATE_BID_QUERY, updateBidDTO.bidStatus().getId(), updateBidDTO.id());
    }
}
