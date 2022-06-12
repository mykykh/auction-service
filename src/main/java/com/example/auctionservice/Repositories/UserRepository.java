package com.example.auctionservice.Repositories;

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
    private static final String GET_ALL_QUERY = "SELECT u.*, r.*, p.* FROM users u, roles r, privileges p, users_roles ur, roles_privileges rp WHERE u.user_id = ur.user_id AND r.role_id = ur.role_id AND r.role_id=rp.role_id AND p.privilege_id = rp.privilege_id GROUP BY u.user_id, r.role_id, p.privilege_id";
    private static final String FIND_BY_NAME_QUERY =    "SELECT u.*, r.*, p.* FROM " +
                                                        "users u, roles r, privileges p, users_roles ur, roles_privileges rp " +
                                                        "WHERE u.user_id = ur.user_id AND r.role_id = ur.role_id " +
                                                        "AND r.role_id=rp.role_id AND p.privilege_id = rp.privilege_id " +
                                                        "AND u.user_name = ?" +
                                                        "GROUP BY u.user_id, r.role_id, p.privilege_id";
    private static final String INSERT_USER_QUERY = "INSERT INTO users(user_name, password, enabled) VALUES (?, ?, ?)";
    private static final String INSERT_USER_ROLE_QUERY = "INSERT INTO users_roles SELECT user_id, role_id FROM users, roles WHERE user_name=? AND role_name=?";

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> getAll(){
        return jdbcTemplate.query(GET_ALL_QUERY, new UserExtractor());
    }

    public Optional<User> findByName(String username){
        List<User> users = jdbcTemplate.query(FIND_BY_NAME_QUERY, new UserExtractor(), username);
        if (users == null) return Optional.empty();
        if (users.isEmpty()) return Optional.empty();
        else return Optional.of(users.get(0));
    }

    @Transactional
    public void save(User user) throws DuplicateKeyException{
        jdbcTemplate.update(INSERT_USER_QUERY, user.getUsername(), user.getPassword(), user.isEnabled());
        for (Role role : user.getRoles()){
            jdbcTemplate.update(INSERT_USER_ROLE_QUERY, user.getUsername(), role.getName());
        }
    }
}
