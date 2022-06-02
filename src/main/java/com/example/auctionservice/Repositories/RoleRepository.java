package com.example.auctionservice.Repositories;

import com.example.auctionservice.Models.Privilege;
import com.example.auctionservice.Models.Role;
import com.example.auctionservice.ResultSetExtractors.RoleExtractor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RoleRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String FIND_BY_NAME_QUERY =    "SELECT r.*, p.* " +
                                                        "FROM privileges p, roles r, roles_privileges rp " +
                                                        "WHERE r.role_id = rp.role_id AND p.privilege_id = rp.privilege_id " +
                                                        "AND r.role_name=? GROUP BY r.role_id, p.privilege_id";

    public RoleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Role> findByName(String roleName){
        List<Role> roles = jdbcTemplate.query(FIND_BY_NAME_QUERY, new RoleExtractor(), roleName);
        if(roles == null) return Optional.empty();
        else if (roles.isEmpty()) return Optional.empty();
        else return Optional.of(roles.get(0));
    }
}
