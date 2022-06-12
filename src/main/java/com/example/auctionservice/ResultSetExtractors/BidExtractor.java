package com.example.auctionservice.ResultSetExtractors;

import com.example.auctionservice.Models.Bid;
import com.example.auctionservice.Models.BidStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BidExtractor implements ResultSetExtractor<List<Bid>> {
    @Override
    public List<Bid> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Bid> bids = new ArrayList<>();
        if (!rs.isBeforeFirst()) return bids;
        while (rs.next()){
            Bid bid = new Bid();
            bid.setId(rs.getLong("bid_id"));
            bid.setAmount(rs.getBigDecimal("bid_amount"));
            bid.setBetStatus(new BidStatus(rs.getLong("status_id"), rs.getString("status_name")));
            bids.add(bid);
        }
        return bids;
    }
}
