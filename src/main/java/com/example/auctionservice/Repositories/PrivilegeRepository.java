package com.example.auctionservice.Repositories;

import com.example.auctionservice.DTOs.Privilege.CreatePrivilegeDTO;
import com.example.auctionservice.Models.Privilege;
import com.example.auctionservice.ResultSetExtractors.PrivilegeExtractor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PrivilegeRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String GET_ALL_QUERY = "SELECT * FROM privileges";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM privileges WHERE privilege_name = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM privileges WHERE privilege_id = ?";
    private static final String INSERT_PRIVILEGE_QUERY = "INSERT INTO privileges(privilege_name) VALUES (?)";
    private static final String DELETE_PRIVILEGE_QUERY = "DELETE FROM privileges WHERE privilege_id = ?";

    public PrivilegeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isPrivilegeWithNameExists(String privilegeName){
        return findByName(privilegeName).isPresent();
    }
    public List<Privilege> getAll(){
        return jdbcTemplate.query(GET_ALL_QUERY, new PrivilegeExtractor());
    }

    public Optional<Privilege> findByName(String privilegeName){
        List<Privilege> privileges = jdbcTemplate.query(FIND_BY_NAME_QUERY, new PrivilegeExtractor(), privilegeName);
        if(privileges == null) return Optional.empty();
        else if (privileges.isEmpty()) return Optional.empty();
        else return Optional.of(privileges.get(0));
    }

    public Optional<Privilege> findById(long privilegeId){
        List<Privilege> privileges = jdbcTemplate.query(FIND_BY_ID_QUERY, new PrivilegeExtractor(), privilegeId);
        if(privileges == null) return Optional.empty();
        else if (privileges.isEmpty()) return Optional.empty();
        else return Optional.of(privileges.get(0));
    }

    public void create(CreatePrivilegeDTO createPrivilegeDTO){
        jdbcTemplate.update(INSERT_PRIVILEGE_QUERY, createPrivilegeDTO.name());
    }

    public void delete(long privilegeId){
        jdbcTemplate.update(DELETE_PRIVILEGE_QUERY, privilegeId);
    }
}
