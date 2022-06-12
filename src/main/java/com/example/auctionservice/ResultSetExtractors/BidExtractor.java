package com.example.auctionservice.ResultSetExtractors;

import com.example.auctionservice.Models.Bid;
import com.example.auctionservice.Models.BidStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class BidExtractor implements ResultSetExtractor<List<Bid>> {
    @Override
    public List<Bid> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Bid> bids = new ArrayList<>();
        if (!rs.isBeforeFirst()) return bids;
        while (rs.next()){
            Bid bid = new Bid();
            bid.setId(rs.getLong("bid_id"));
            bid.setAmount(rs.getBigDecimal("bid_amount"));
            bid.setBidStatus(new BidStatus(rs.getLong("status_id"), rs.getString("status_name")));
            bid.setUserId(rs.getLong("user_id"));
            bid.setAuctionId(rs.getLong("auction_id"));
            bid.setUpdatedTime(rs.getTimestamp("updated_time", Calendar.getInstance(TimeZone.getTimeZone("UTC"))).toInstant());
            bid.setCreatedTime(rs.getTimestamp("created_time", Calendar.getInstance(TimeZone.getTimeZone("UTC"))).toInstant());
            bids.add(bid);
        }
        return bids;
    }
}
