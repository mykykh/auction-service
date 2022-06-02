package com.example.auctionservice.ResultSetExtractors;

import com.example.auctionservice.Models.Privilege;
import com.example.auctionservice.Models.Role;
import com.example.auctionservice.Models.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
            user.setEnabled(rs.getBoolean("enabled"));

            user.setRoles(roleExtractor.extractDataByUserId(rs, current_user_id));
            users.add(user);
        }while (!rs.isAfterLast());
        return users;
    }
}
