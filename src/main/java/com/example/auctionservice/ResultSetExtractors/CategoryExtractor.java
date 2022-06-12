package com.example.auctionservice.ResultSetExtractors;

import com.example.auctionservice.Models.AuctionStatus;
import com.example.auctionservice.Models.Category;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryExtractor implements ResultSetExtractor<List<Category>> {
    @Override
    public List<Category> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Category> categories = new ArrayList<>();
        if (!rs.isBeforeFirst()) return categories;
        while (rs.next()){
            Category category = new Category(rs.getLong("category_id"), rs.getString("category_name"));
            categories.add(category);
        }
        return categories;
    }
}
