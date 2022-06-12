package com.example.auctionservice.ResultSetExtractors;

import com.example.auctionservice.Models.Privilege;
import com.example.auctionservice.Models.Role;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleExtractor implements ResultSetExtractor<List<Role>> {
    @Override
    public List<Role> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Role> roles = new ArrayList<>();
        if (!rs.isBeforeFirst()) return roles;
        rs.next();
        do{
            long current_role_id = rs.getLong("role_id");
            Role role = new Role();
            role.setId(rs.getLong("role_id"));
            role.setName(rs.getString("role_name"));

            ArrayList<Privilege> privileges = new ArrayList<>();
            do {
                if (rs.getString("privilege_name") != null){
                    privileges.add(new Privilege(rs.getLong("privilege_id"),
                            rs.getString("privilege_name")));
                }
                rs.next();
            } while (!rs.isAfterLast() && rs.getLong("role_id") == current_role_id);
            role.setPrivileges(privileges);
            roles.add(role);
        }while(!rs.isAfterLast());
        return roles;
    }

    public List<Role> extractDataByUserId(ResultSet rs, long current_user_id) throws SQLException, DataAccessException {
        List<Role> roles = new ArrayList<>();
        do {
            long current_role_id = rs.getLong("role_id");
            Role role = new Role();
            role.setId(rs.getLong("role_id"));
            role.setName(rs.getString("role_name"));

            ArrayList<Privilege> privileges = new ArrayList<>();
            do {
                if (rs.getString("privilege_name") != null){
                    privileges.add(new Privilege(rs.getLong("privilege_id"),
                            rs.getString("privilege_name")));
                }
                rs.next();
            } while (!rs.isAfterLast() && rs.getLong("role_id") == current_role_id && rs.getLong("user_id") == current_user_id);
            role.setPrivileges(privileges);
            roles.add(role);
        } while (!rs.isAfterLast() && rs.getLong("user_id") == current_user_id);
        return roles;
    }
}
