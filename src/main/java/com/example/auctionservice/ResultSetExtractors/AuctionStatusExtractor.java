package com.example.auctionservice.ResultSetExtractors;

import com.example.auctionservice.Models.AuctionStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuctionStatusExtractor implements ResultSetExtractor<List<AuctionStatus>> {
    @Override
    public List<AuctionStatus> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<AuctionStatus> auctionStatuses = new ArrayList<>();
        if (!rs.isBeforeFirst()) return auctionStatuses;
        while (rs.next()){
            AuctionStatus auctionStatus = new AuctionStatus(rs.getLong("status_id"), rs.getString("status_name"));
            auctionStatuses.add(auctionStatus);
        }
        return auctionStatuses;
    }
}