package com.example.auctionservice.Repositories;

import com.example.auctionservice.DTOs.Auction.CreateAuctionDTO;
import com.example.auctionservice.DTOs.Auction.UpdateAuctionDTO;
import com.example.auctionservice.Models.Auction;
import com.example.auctionservice.ResultSetExtractors.AuctionExtractor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class AuctionRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String GET_ALL_QUERY = "SELECT * FROM auctions a, auction_statuses s, categories c WHERE a.status_id = s.status_id AND c.category_id = a.category_id GROUP BY s.status_id, a.auction_id, c.category_id ORDER BY s.status_id, a.auction_id, c.category_id";
    private static final String GET_ALL_BY_USER_ID_QUERY = "SELECT * FROM auctions a, auction_statuses s, categories c WHERE a.status_id = s.status_id AND c.category_id = a.category_id AND a.user_id=? GROUP BY s.status_id, a.auction_id, c.category_id ORDER BY s.status_id, a.auction_id, c.category_id";
    private static final String GET_ALL_BY_CATEGORY_ID_QUERY = "SELECT * FROM auctions a, auction_statuses s, categories c WHERE a.status_id = s.status_id AND c.category_id = a.category_id AND a.category_id=? GROUP BY s.status_id, a.auction_id, c.category_id ORDER BY s.status_id, a.auction_id, c.category_id";
    private static final String GET_ALL_EXPIRED_QUERY = "SELECT * FROM auctions a, auction_statuses s, categories c WHERE a.status_id = s.status_id AND c.category_id = a.category_id AND a.expiration_time <= current_timestamp AND s.status_name= 'ACTIVE' GROUP BY s.status_id, a.auction_id, c.category_id ORDER BY a.auction_id, s.status_id, c.category_id";
    private static final String FIND_ACTIVE_BY_ID_QUERY = "SELECT * FROM auctions a, auction_statuses s, categories c WHERE a.status_id = s.status_id AND c.category_id = a.category_id AND s.status_name='ACTIVE' AND a.auction_id = ? GROUP BY s.status_id, a.auction_id, c.category_id ORDER BY a.auction_id, s.status_id, c.category_id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM auctions a, auction_statuses s, categories c WHERE a.status_id = s.status_id AND c.category_id = a.category_id AND a.auction_id = ? GROUP BY s.status_id, a.auction_id, c.category_id ORDER BY a.auction_id, s.status_id, c.category_id";
    private static final String UPDATE_QUERY = "UPDATE auctions SET auction_title=?, auction_description=?, starting_price=?, expiration_time=?, status_id=?, category_id=?, updated_time=current_timestamp WHERE auction_id=?";

    private static final String INSERT_QUERY = "INSERT INTO auctions(user_id, auction_title, auction_description, starting_price, expiration_time, status_id, category_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
    public AuctionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Auction> getAll(){
        return jdbcTemplate.query(GET_ALL_QUERY, new AuctionExtractor());
    }

    public List<Auction> getAllByUserId(long userId){
        return jdbcTemplate.query(GET_ALL_BY_USER_ID_QUERY, new AuctionExtractor(), userId);
    }
    public List<Auction> getAllByCategoryId(long categoryId){
        return jdbcTemplate.query(GET_ALL_BY_CATEGORY_ID_QUERY, new AuctionExtractor(), categoryId);
    }

    public List<Auction> getAllExpired(){
        return jdbcTemplate.query(GET_ALL_EXPIRED_QUERY, new AuctionExtractor());
    }

    public Optional<Auction> findActiveById(long id){
        List<Auction> auctions = jdbcTemplate.query(FIND_ACTIVE_BY_ID_QUERY, new AuctionExtractor(), id);
        if (auctions == null) return Optional.empty();
        if (auctions.isEmpty()) return Optional.empty();
        else return Optional.of(auctions.get(0));
    }

    public Optional<Auction> findById(long id){
        List<Auction> auctions = jdbcTemplate.query(FIND_BY_ID_QUERY, new AuctionExtractor(), id);
        if (auctions == null) return Optional.empty();
        if (auctions.isEmpty()) return Optional.empty();
        else return Optional.of(auctions.get(0));
    }

    public void updateAuction(UpdateAuctionDTO updateAuctionDTO){
        Optional<Auction> auction = findById(updateAuctionDTO.id());
        if (auction.isEmpty()) return;
        if (auction.get().getAuctionStatus().getName().equals("CLOSED"))return;
        jdbcTemplate.update(UPDATE_QUERY, updateAuctionDTO.title(), updateAuctionDTO.description(), updateAuctionDTO.startingPrice(),
                Timestamp.from(updateAuctionDTO.expirationTime()), updateAuctionDTO.auctionStatus().getId(),
                updateAuctionDTO.category().getId(), updateAuctionDTO.id());
    }

    public void createAuction(CreateAuctionDTO auctionDTO){
        jdbcTemplate.update(INSERT_QUERY, auctionDTO.user_id(), auctionDTO.title(), auctionDTO.description(), auctionDTO.startingPrice(),
                Timestamp.from(auctionDTO.expirationTime()), auctionDTO.auctionStatus().getId(), auctionDTO.category().getId());
    }
}
