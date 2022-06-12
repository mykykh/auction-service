package com.example.auctionservice.Repositories;

import com.example.auctionservice.Models.AuctionStatus;
import com.example.auctionservice.ResultSetExtractors.AuctionStatusExtractor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AuctionStatusRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String FIND_BY_NAME_QUERY =    "SELECT * FROM auction_statuses WHERE status_name=?";

    public AuctionStatusRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<AuctionStatus> findByName(String statusName){
        List<AuctionStatus> auctionStatuses = jdbcTemplate.query(FIND_BY_NAME_QUERY, new AuctionStatusExtractor(), statusName);
        if(auctionStatuses == null) return Optional.empty();
        else if (auctionStatuses.isEmpty()) return Optional.empty();
        else return Optional.of(auctionStatuses.get(0));
    }
}
