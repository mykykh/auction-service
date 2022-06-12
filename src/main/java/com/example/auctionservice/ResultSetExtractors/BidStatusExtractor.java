package com.example.auctionservice.ResultSetExtractors;

import com.example.auctionservice.Models.BidStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BidStatusExtractor  implements ResultSetExtractor<List<BidStatus>> {
    @Override
    public List<BidStatus> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<BidStatus> bidStatuses = new ArrayList<>();
        if (!rs.isBeforeFirst()) return bidStatuses;
        while (rs.next()){
            BidStatus bidStatus = new BidStatus(rs.getLong("status_id"), rs.getString("status_name"));
            bidStatuses.add(bidStatus);
        }
        return bidStatuses;
    }
}
