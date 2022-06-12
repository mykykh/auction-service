package com.example.auctionservice.Repositories;

import com.example.auctionservice.DTOs.Categories.CreateCategoryDTO;
import com.example.auctionservice.Models.Category;
import com.example.auctionservice.ResultSetExtractors.CategoryExtractor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CategoryRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String GET_ALL_QUERY = "SELECT * FROM categories c";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM categories WHERE category_name = ?";
    private static final String INSERT_CATEGORY_QUERY = "INSERT INTO categories(category_name) VALUES (?)";
    private static final String DELETE_CATEGORY_QUERY = "CALL delete_category(?)";

    public CategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isCategoryWithNameExists(String categoryName){
        return findByName(categoryName).isPresent();
    }

    public List<Category> getAll(){
        return jdbcTemplate.query(GET_ALL_QUERY, new CategoryExtractor());
    }

    public Optional<Category> findByName(String categoryName){
        List<Category> categories = jdbcTemplate.query(FIND_BY_NAME_QUERY, new CategoryExtractor(), categoryName);
        if(categories == null) return Optional.empty();
        else if (categories.isEmpty()) return Optional.empty();
        else return Optional.of(categories.get(0));
    }

    public void create(CreateCategoryDTO createCategoryDTO){
        jdbcTemplate.update(INSERT_CATEGORY_QUERY, createCategoryDTO.name());
    }

    public void delete(long categoryId){
        if (categoryId == 1) return;
        jdbcTemplate.update(DELETE_CATEGORY_QUERY, categoryId);
    }
}
