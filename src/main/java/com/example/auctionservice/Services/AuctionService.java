package com.example.auctionservice.Services;

import com.example.auctionservice.DTOs.Auction.CreateAuctionDTO;
import com.example.auctionservice.DTOs.Auction.PrivateAuctionDTO;
import com.example.auctionservice.DTOs.Auction.PublicAuctionDTO;
import com.example.auctionservice.DTOs.Auction.UpdateAuctionDTO;
import com.example.auctionservice.DTOs.Bid.PublicBidDTO;
import com.example.auctionservice.DTOs.Categories.CreateCategoryDTO;
import com.example.auctionservice.Models.Auction;
import com.example.auctionservice.Models.AuctionStatus;
import com.example.auctionservice.Models.Category;
import com.example.auctionservice.Repositories.AuctionRepository;
import com.example.auctionservice.Repositories.AuctionStatusRepository;
import com.example.auctionservice.Repositories.BidRepository;
import com.example.auctionservice.Repositories.CategoryRepository;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;
    private final AuctionStatusRepository auctionStatusRepository;
    private final BidRepository bidRepository;
    public AuctionService(AuctionRepository auctionRepository, CategoryRepository categoryRepository, AuctionStatusRepository auctionStatusRepository, BidRepository bidRepository) {
        this.auctionRepository = auctionRepository;
        this.categoryRepository = categoryRepository;
        this.auctionStatusRepository = auctionStatusRepository;
        this.bidRepository = bidRepository;
    }

    public boolean isCategoryWithNameExists(String categoryName){
        return categoryRepository.isCategoryWithNameExists(categoryName);
    }

    public List<Category> getAllCategories(){
        return categoryRepository.getAll();
    }
    @PostFilter("filterObject.auctionStatus().name == 'ACTIVE'")
    public List<PublicAuctionDTO> getAllAuctionPublicData(){
        return auctionRepository.getAll().stream().map(this::convertToPublicDto).collect(Collectors.toList());
    }
    @PreAuthorize("hasAuthority('SELECT_AUCTIONS')")
    @PostFilter("filterObject.auctionStatus().name == 'ACTIVE'")
    public List<PrivateAuctionDTO> getAllAuctionPrivateData(){
        return auctionRepository.getAll().stream().map(this::convertToPrivateDto).collect(Collectors.toList());
    }
    @PostFilter("filterObject.auctionStatus().name == 'ACTIVE'")
    public List<PublicAuctionDTO> getAllAuctionPublicDataByCategoryId(long categoryId){
        return auctionRepository.getAllByCategoryId(categoryId).stream().map(this::convertToPublicDto).collect(Collectors.toList());
    }
    @PostFilter("filterObject.auctionStatus().name == 'ACTIVE'")
    public List<PublicAuctionDTO> getAllAuctionPublicDataByUserId(long userId){
        return auctionRepository.getAllByUserId(userId).stream().map(this::convertToPublicDto).collect(Collectors.toList());
    }
    @PreAuthorize("hasAuthority('SELECT_AUCTIONS') or #userId == authentication.principal.id")
    public List<PrivateAuctionDTO> getAllAuctionPrivateDataByUserId(long userId){
        return auctionRepository.getAllByUserId(userId).stream().map(this::convertToPrivateDto).collect(Collectors.toList());
    }

    public Optional<PublicAuctionDTO> findAuctionPublicDataById(long id){
        return auctionRepository.findActiveById(id).map(this::convertToPublicDto);
    }
    public Optional<PrivateAuctionDTO> findAuctionPrivateDataById(long id){
        return auctionRepository.findById(id).map(this::convertToPrivateDto);
    }

    public Optional<Category> findCategoryByName(String categoryName){
        return categoryRepository.findByName(categoryName);
    }
    public Optional<AuctionStatus> findAuctionStatusByName(String auctionStatusName){
        return auctionStatusRepository.findByName(auctionStatusName);
    }

    @PreAuthorize("#auctionDTO.user_id() == authentication.principal.id")
    public void createAuction(CreateAuctionDTO auctionDTO){
        auctionRepository.createAuction(auctionDTO);
    }
    @PreAuthorize("hasAuthority('UPDATE_AUCTIONS')")
    public void createCategory(CreateCategoryDTO createCategoryDTO){
        categoryRepository.create(createCategoryDTO);
    }

    @PreAuthorize("hasAuthority('UPDATE_AUCTIONS') or #auctionDTO.user_id() == authentication.principal.id")
    public void updateAuction(UpdateAuctionDTO auctionDTO){
        auctionRepository.updateAuction(auctionDTO);
    }

    @PreAuthorize("hasAuthority('UPDATE_AUCTIONS')")
    public void deleteCategory(long categoryId){
        categoryRepository.delete(categoryId);
    }

    public PublicAuctionDTO convertToPublicDto(Auction auction){
        return new PublicAuctionDTO(auction.getId(), auction.getTitle(),
                auction.getDescription(), auction.getStartingPrice(), auction.getAuctionStatus(),
                auction.getCategory(), auction.getUserId(), auction.getExpirationTime());
    }

    public PrivateAuctionDTO convertToPrivateDto(Auction auction){
        return new PrivateAuctionDTO(auction.getId(), auction.getTitle(),
                auction.getDescription(), auction.getStartingPrice(), auction.getAuctionStatus(),
                auction.getCategory(), auction.getUserId(),
                auction.getExpirationTime(), auction.getUpdatedTime(), auction.getCreatedTime());
    }

    public BigDecimal getBidMinimalAmountByAuctionId(long auctionId, BigDecimal startingPrice){

        Optional<BigDecimal> minimalAmountOfNewBidOptional = bidRepository.getMaxAmountByAuctionId(auctionId);

        BigDecimal minimalAmountOfNewBid;

        if (minimalAmountOfNewBidOptional.isPresent()){
            if (minimalAmountOfNewBidOptional.get().compareTo(startingPrice) < 0) minimalAmountOfNewBid = startingPrice;
            else {
                minimalAmountOfNewBid = minimalAmountOfNewBidOptional.get();
                minimalAmountOfNewBid = minimalAmountOfNewBid.add(BigDecimal.valueOf(1));
            }
        }else minimalAmountOfNewBid = startingPrice;

        return minimalAmountOfNewBid;
    }

    public static Instant getExpirationTimeInstant(String expirationTime){
        LocalDateTime localDateTime = LocalDateTime.parse(expirationTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        Instant expirationTimeInstant = localDateTime.toInstant(ZoneOffset.UTC);
        if (expirationTimeInstant.compareTo(Instant.now()) < 0) {
            expirationTimeInstant = Instant.now();
        }
        return expirationTimeInstant;
    }
}
