package com.example.auctionservice.Repositories;

import com.example.auctionservice.DTOs.Role.CreateRoleDTO;
import com.example.auctionservice.DTOs.Role.UpdateRoleDTO;
import com.example.auctionservice.Models.Privilege;
import com.example.auctionservice.Models.Role;
import com.example.auctionservice.Models.User;
import com.example.auctionservice.ResultSetExtractors.RoleExtractor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String GET_ALL_QUERY = "SELECT r.*, p.* FROM roles_privileges rp RIGHT JOIN roles r ON r.role_id = rp.role_id LEFT JOIN privileges p ON p.privilege_id = rp.privilege_id ORDER BY r.role_id, p.privilege_id";
    private static final String FIND_BY_NAME_QUERY = "SELECT r.*, p.* FROM roles_privileges rp RIGHT JOIN roles r ON r.role_id = rp.role_id LEFT JOIN privileges p ON p.privilege_id = rp.privilege_id WHERE r.role_name=? ORDER BY r.role_id, p.privilege_id";
    private static final String FIND_BY_ID_QUERY = "SELECT r.*, p.* FROM roles_privileges rp RIGHT JOIN roles r ON r.role_id = rp.role_id LEFT JOIN privileges p ON p.privilege_id = rp.privilege_id WHERE r.role_id=? ORDER BY r.role_id, p.privilege_id";
    private static final String INSERT_ROLE_QUERY = "INSERT INTO roles(role_name) VALUES (?)";
    private static final String INSERT_ROLE_PRIVILEGE_QUERY = "INSERT INTO roles_privileges(role_id, privilege_id) SELECT role_id, privilege_id FROM roles, privileges WHERE role_name=? AND privilege_name=?";
    private static final String INSERT_ROLE_PRIVILEGE_BY_ID_QUERY = "INSERT INTO roles_privileges(role_id, privilege_id) VALUES (?, ?)";

    private static final String UPDATE_ROLE_QUERY = "UPDATE roles SET role_name = ? WHERE role_id = ?";
    private static final String DELETE_ROLE_QUERY = "DELETE FROM roles WHERE role_id = ?";
    private static final String DELETE_ROLE_PRIVILEGE_QUERY = "DELETE FROM roles_privileges WHERE role_id = ? AND privilege_id = ?";

    public RoleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isRoleWithNameExists(String roleName){
        return findByName(roleName).isPresent();
    }

    public List<Role> getAll(){
        return jdbcTemplate.query(GET_ALL_QUERY, new RoleExtractor());
    }

    public Optional<Role> findByName(String roleName){
        List<Role> roles = jdbcTemplate.query(FIND_BY_NAME_QUERY, new RoleExtractor(), roleName);
        if(roles == null) return Optional.empty();
        else if (roles.isEmpty()) return Optional.empty();
        else return Optional.of(roles.get(0));
    }

    public Optional<Role> findById(long roleId){
        List<Role> roles = jdbcTemplate.query(FIND_BY_ID_QUERY, new RoleExtractor(), roleId);
        if(roles == null) return Optional.empty();
        else if (roles.isEmpty()) return Optional.empty();
        else return Optional.of(roles.get(0));
    }

    @Transactional
    public void create(CreateRoleDTO createRoleDTO){
        jdbcTemplate.update(INSERT_ROLE_QUERY, createRoleDTO.name());
        for (Privilege privilege: createRoleDTO.privileges()){
            jdbcTemplate.update(INSERT_ROLE_PRIVILEGE_QUERY, createRoleDTO.name(), privilege.getName());
        }
    }

    @Transactional
    public void update(UpdateRoleDTO updateRoleDTO){
        Optional<Role> roleOptional = findById(updateRoleDTO.id());
        if (roleOptional.isEmpty()) return;
        jdbcTemplate.update(UPDATE_ROLE_QUERY, updateRoleDTO.name(), updateRoleDTO.id());
        for (Privilege newPrivilege: updateRoleDTO.privileges()){
            if (roleOptional.get().getPrivileges().stream().noneMatch(oldRole -> oldRole.getId().equals(newPrivilege.getId()))){
                jdbcTemplate.update(INSERT_ROLE_PRIVILEGE_BY_ID_QUERY, updateRoleDTO.id(), newPrivilege.getId());
            }
        }
        for (Privilege oldPrivilege: roleOptional.get().getPrivileges()){
            if (updateRoleDTO.privileges().stream().noneMatch(newPrivilege -> newPrivilege.getId().equals(oldPrivilege.getId()))){
                jdbcTemplate.update(DELETE_ROLE_PRIVILEGE_QUERY, updateRoleDTO.id(), oldPrivilege.getId());
            }
        }
    }

    public void delete(long roleId){
        if (roleId == 1 || roleId == 2) return;
        jdbcTemplate.update(DELETE_ROLE_QUERY, roleId);
    }
}
