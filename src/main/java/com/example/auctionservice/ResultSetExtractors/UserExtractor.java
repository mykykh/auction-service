package com.example.auctionservice.ResultSetExtractors;

import com.example.auctionservice.Models.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class UserExtractor implements ResultSetExtractor<List<User>> {

    final private RoleExtractor roleExtractor = new RoleExtractor();

    @Override
    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<User> users = new ArrayList<>();
        if (!rs.isBeforeFirst()) return users;
        rs.next();
        do{
            long current_user_id = rs.getLong("user_id");
            User user = new User();

            user.setId(rs.getLong("user_id"));
            user.setUsername(rs.getString("user_name"));
            user.setPassword(rs.getString("password"));
            user.setBalance(rs.getBigDecimal("balance"));
            user.setEnabled(rs.getBoolean("enabled"));
            user.setUpdatedTime(rs.getTimestamp("updated_time", Calendar.getInstance(TimeZone.getTimeZone("UTC"))).toInstant());
            user.setCreatedTime(rs.getTimestamp("created_time", Calendar.getInstance(TimeZone.getTimeZone("UTC"))).toInstant());

            user.setRoles(roleExtractor.extractDataByUserId(rs, current_user_id));
            users.add(user);
        }while (!rs.isAfterLast());
        return users;
    }
}
