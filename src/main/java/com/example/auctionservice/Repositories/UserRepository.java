package com.example.auctionservice.Repositories;

import com.example.auctionservice.DTOs.User.*;
import com.example.auctionservice.Models.Role;
import com.example.auctionservice.Models.User;
import com.example.auctionservice.ResultSetExtractors.UserExtractor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository{
    private final JdbcTemplate jdbcTemplate;

    private static final String GET_ALL_QUERY = "SELECT u.*, r.*, p.* FROM users_roles ur RIGHT JOIN users u ON ur.user_id = u.user_id LEFT JOIN (roles_privileges rp RIGHT JOIN roles r ON r.role_id = rp.role_id LEFT JOIN privileges p ON p.privilege_id = rp.privilege_id) ON ur.role_id = r.role_id GROUP BY u.user_id, r.role_id, p.privilege_id ORDER BY u.user_id, r.role_id, p.privilege_id";
    private static final String FIND_BY_ID_QUERY = "SELECT u.*, r.*, p.* FROM users_roles ur RIGHT JOIN users u ON ur.user_id = u.user_id LEFT JOIN (roles_privileges rp RIGHT JOIN roles r ON r.role_id = rp.role_id LEFT JOIN privileges p ON p.privilege_id = rp.privilege_id) ON ur.role_id = r.role_id WHERE u.user_id=? GROUP BY u.user_id, r.role_id, p.privilege_id  ORDER BY u.user_id, r.role_id, p.privilege_id";
    private static final String FIND_BY_NAME_QUERY = "SELECT u.*, r.*, p.* FROM users_roles ur RIGHT JOIN users u ON ur.user_id = u.user_id LEFT JOIN (roles_privileges rp RIGHT JOIN roles r ON r.role_id = rp.role_id LEFT JOIN privileges p ON p.privilege_id = rp.privilege_id) ON ur.role_id = r.role_id WHERE u.user_name=? GROUP BY u.user_id, r.role_id, p.privilege_id ORDER BY u.user_id, r.role_id, p.privilege_id";
    private static final String INSERT_USER_QUERY = "INSERT INTO users(user_name, password, balance, enabled) VALUES (?, ?, 0, ?)";
    private static final String INSERT_USER_ROLE_QUERY = "INSERT INTO users_roles SELECT user_id, role_id FROM users, roles WHERE user_name=? AND role_name=?";
    private static final String INSERT_USER_ROLE_BY_ID_QUERY = "INSERT INTO users_roles(user_id, role_id) VALUES (?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET user_name = ?, balance = ?, updated_time=current_timestamp WHERE user_id = ?";
    private static final String UPDATE_USER_PASSWORD_QUERY = "UPDATE users SET password = ?, updated_time=current_timestamp WHERE user_id = ?";
    private static final String DELETE_USER_ROLE_QUERY = "DELETE FROM users_roles WHERE user_id = ? AND role_id = ?";

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> getAll(){
        return jdbcTemplate.query(GET_ALL_QUERY, new UserExtractor());
    }


    public boolean isUserWithNameExists(String username){
        List<User> users = jdbcTemplate.query(FIND_BY_NAME_QUERY, new UserExtractor(), username);
        if (users == null) return false;
        return !users.isEmpty();
    }

    public Optional<User> findById(long id){
        List<User> users = jdbcTemplate.query(FIND_BY_ID_QUERY, new UserExtractor(), id);
        if (users == null) return Optional.empty();
        if (users.isEmpty()) return Optional.empty();
        else return Optional.of(users.get(0));
    }

    public Optional<User> findByName(String username){
        List<User> users = jdbcTemplate.query(FIND_BY_NAME_QUERY, new UserExtractor(), username);
        if (users == null) return Optional.empty();
        if (users.isEmpty()) return Optional.empty();
        else return Optional.of(users.get(0));
    }

    public void update(UpdateUserDTO updateUserDTO){
        jdbcTemplate.update(UPDATE_USER_QUERY, updateUserDTO.username(), updateUserDTO.balance(), updateUserDTO.id());
    }

    @Transactional
    public void update(UpdateUserAndUserRolesDTO updateUserDTO){
        Optional<User> user = findById(updateUserDTO.id());
        if (user.isEmpty()) return;
        jdbcTemplate.update(UPDATE_USER_QUERY, updateUserDTO.username(), updateUserDTO.balance(), updateUserDTO.id());
        for (Role newRole: updateUserDTO.roles()){
            if (user.get().getRoles().stream().noneMatch(oldRole -> oldRole.getId().equals(newRole.getId()))){
                jdbcTemplate.update(INSERT_USER_ROLE_BY_ID_QUERY, updateUserDTO.id(), newRole.getId());
            }
        }
        for (Role oldRole: user.get().getRoles()){
            if (updateUserDTO.roles().stream().noneMatch(newRole -> newRole.getId().equals(oldRole.getId()))){
                jdbcTemplate.update(DELETE_USER_ROLE_QUERY, updateUserDTO.id(), oldRole.getId());
            }
        }
    }

    public void updatePassword(UpdateUserPasswordDTO userPasswordDTO){
        jdbcTemplate.update(UPDATE_USER_PASSWORD_QUERY, userPasswordDTO.password(), userPasswordDTO.id());
    }

    @Transactional
    public void create(CreateUserDTO createUserDTO) throws DuplicateKeyException{
        jdbcTemplate.update(INSERT_USER_QUERY, createUserDTO.username(), createUserDTO.password(), createUserDTO.enabled());
        for (Role role : createUserDTO.roles()){
            jdbcTemplate.update(INSERT_USER_ROLE_QUERY, createUserDTO.username(), role.getName());
        }
    }
}
