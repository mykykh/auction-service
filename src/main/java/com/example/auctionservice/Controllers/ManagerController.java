package com.example.auctionservice.Controllers;

import com.example.auctionservice.DTOs.Auction.PrivateAuctionDTO;
import com.example.auctionservice.DTOs.Auction.UpdateAuctionDTO;
import com.example.auctionservice.DTOs.Bid.PublicBidDTO;
import com.example.auctionservice.DTOs.Categories.CreateCategoryDTO;
import com.example.auctionservice.DTOs.User.PrivateUserDTO;
import com.example.auctionservice.DTOs.User.UpdateUserAndUserRolesDTO;
import com.example.auctionservice.DTOs.User.UpdateUserDTO;
import com.example.auctionservice.Exceptions.NotFoundException;
import com.example.auctionservice.Models.Auction;
import com.example.auctionservice.Models.AuctionStatus;
import com.example.auctionservice.Models.Category;
import com.example.auctionservice.Models.Role;
import com.example.auctionservice.Services.AuctionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    private final AuctionService auctionService;

    public ManagerController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @GetMapping
    public ModelAndView getManagerPanel(ModelMap model){
        return new ModelAndView("manager/index");
    }

    @GetMapping("/categories")
    public ModelAndView getAllCategories(ModelMap model){
        List<Category> categories = auctionService.getAllCategories();

        model.addAttribute("categories", categories);

        return new ModelAndView("manager/categoriesList", model);
    }

    @PostMapping("/category/create-category")
    public ModelAndView createCategory(@RequestParam String name){
        if (auctionService.isCategoryWithNameExists(name)) return new ModelAndView("redirect:/manager/categories");

        auctionService.createCategory(new CreateCategoryDTO(name));
        return new ModelAndView("redirect:/manager/categories");
    }

    @PostMapping("/category/delete-category")
    public ModelAndView deleteCategory(@RequestParam long id){
        auctionService.deleteCategory(id);
        return new ModelAndView("redirect:/manager/categories");
    }

    @GetMapping("/auctions")
    public ModelAndView getAllAuctions(ModelMap model){
        List<PrivateAuctionDTO> auctions = auctionService.getAllAuctionPrivateData();

        model.addAttribute("auctions", auctions);

        return new ModelAndView("manager/auctionsList", model);
    }

    @GetMapping("/auction/{id}")
    public ModelAndView editAuction(@PathVariable long id, ModelMap model){
        Optional<PrivateAuctionDTO> auction = auctionService.findAuctionPrivateDataById(id);

        if (auction.isEmpty()) throw new NotFoundException();
        if (auction.get().auctionStatus().getName().equals("CLOSED")) return new ModelAndView("redirect:/account/auction/" + id);

        List<Category> categories = auctionService.getAllCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("auction", auction.get());
        model.addAttribute("expirationTime",  LocalDateTime.ofInstant(auction.get().expirationTime(), ZoneOffset.UTC));

        return new ModelAndView("manager/editAuction", model);
    }

    @PostMapping("/auction/edit-auction")
    public ModelAndView editAuction(@RequestParam long id, @RequestParam String title, @RequestParam Optional<String> description,
                                    @RequestParam BigDecimal startingPrice, @RequestParam String categoryName,
                                    @RequestParam String expirationTime){
        Optional<PrivateAuctionDTO> auction = auctionService.findAuctionPrivateDataById(id);

        if (auction.isEmpty()) throw new NotFoundException();
        if (auction.get().auctionStatus().getName().equals("CLOSED")) return new ModelAndView("redirect:/manager/auctions");

        Optional<Category> category = auctionService.findCategoryByName(categoryName);
        Optional<AuctionStatus> auctionStatus = auctionService.findAuctionStatusByName("ACTIVE");

        startingPrice = startingPrice.setScale(2, RoundingMode.HALF_UP);
        Instant expirationTimeInstant = AuctionService.getExpirationTimeInstant(expirationTime);

        if (category.isEmpty() || auctionStatus.isEmpty()) return new ModelAndView("redirect:/manager/auctions/" + id);

        auctionService.updateAuction(new UpdateAuctionDTO(auction.get().userId(), id, title, description.orElse(null), startingPrice,
                auctionStatus.get(), category.get(), expirationTimeInstant));

        return new ModelAndView("redirect:/manager/auctions");
    }
}
