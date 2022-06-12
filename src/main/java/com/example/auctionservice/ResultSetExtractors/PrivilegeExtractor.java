package com.example.auctionservice.ResultSetExtractors;

import com.example.auctionservice.Models.Privilege;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrivilegeExtractor  implements ResultSetExtractor<List<Privilege>> {
    @Override
    public List<Privilege> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Privilege> privileges = new ArrayList<>();
        if (!rs.isBeforeFirst()) return privileges;
        while (rs.next()){
            Privilege privilege = new Privilege(rs.getLong("privilege_id"), rs.getString("privilege_name"));
            privileges.add(privilege);
        }
        return privileges;
    }
}