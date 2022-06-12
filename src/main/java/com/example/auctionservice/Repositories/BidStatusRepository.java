package com.example.auctionservice.Repositories;

import com.example.auctionservice.Models.BidStatus;
import com.example.auctionservice.ResultSetExtractors.BidStatusExtractor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BidStatusRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String FIND_BY_NAME_QUERY =    "SELECT * FROM bid_statuses WHERE status_name=?";

    public BidStatusRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<BidStatus> findByName(String statusName){
        List<BidStatus> bidStatuses = jdbcTemplate.query(FIND_BY_NAME_QUERY, new BidStatusExtractor(), statusName);
        if(bidStatuses == null) return Optional.empty();
        else if (bidStatuses.isEmpty()) return Optional.empty();
        else return Optional.of(bidStatuses.get(0));
    }
}
