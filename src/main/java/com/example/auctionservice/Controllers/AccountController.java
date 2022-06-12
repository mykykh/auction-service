package com.example.auctionservice.Controllers;


import com.example.auctionservice.DTOs.Auction.CreateAuctionDTO;
import com.example.auctionservice.DTOs.Auction.PrivateAuctionDTO;
import com.example.auctionservice.DTOs.Auction.UpdateAuctionDTO;
import com.example.auctionservice.DTOs.Bid.PrivateBidDTO;
import com.example.auctionservice.DTOs.Bid.PublicBidDTO;
import com.example.auctionservice.DTOs.User.PrivateUserDTO;
import com.example.auctionservice.DTOs.User.UpdateUserDTO;
import com.example.auctionservice.DTOs.User.UpdateUserPasswordDTO;
import com.example.auctionservice.Exceptions.NotFoundException;
import com.example.auctionservice.Models.*;
import com.example.auctionservice.Services.AuctionService;
import com.example.auctionservice.Services.BidService;
import com.example.auctionservice.Services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final UserService userService;
    private final AuctionService auctionService;
    private final BidService bidService;

    public AccountController(UserService userService, AuctionService auctionService, BidService bidService) {
        this.userService = userService;
        this.auctionService = auctionService;
        this.bidService = bidService;
    }

    @GetMapping
    public ModelAndView account(ModelMap model, Principal principal){
        Optional<PrivateUserDTO> userDTO = userService.findUserPrivateDataByUsername(principal.getName());
        if (userDTO.isEmpty()) throw new NotFoundException();

        List<PrivateAuctionDTO> auctions = auctionService.getAllAuctionPrivateDataByUserId(userDTO.get().id());
        List<PrivateBidDTO> bids = bidService.getAllBidPrivateDataByUserId(userDTO.get().id());

        model.addAttribute("user", userDTO.get());
        model.addAttribute("auctions", auctions);
        model.addAttribute("bids", bids);

        return new ModelAndView("account/account");
    }

    @GetMapping("/create-auction")
    public ModelAndView getCreateAuction(ModelMap model){
        List<Category> categories = auctionService.getAllCategories();
        model.addAttribute("categories", categories);
        return new ModelAndView("account/createAuction", model);
    }

    @PostMapping("/create-auction")
    public ModelAndView postCreateAuction(@RequestParam String title, @RequestParam Optional<String> description,
                                          @RequestParam BigDecimal startingPrice, @RequestParam String categoryName,
                                          @RequestParam String expirationTime, Principal principal){
        Optional<PrivateUserDTO> userDTO = userService.findUserPrivateDataByUsername(principal.getName());
        if (userDTO.isEmpty()) throw new NotFoundException();

        Optional<Category> category = auctionService.findCategoryByName(categoryName);
        Optional<AuctionStatus> auctionStatus = auctionService.findAuctionStatusByName("ACTIVE");

        startingPrice = startingPrice.setScale(2, RoundingMode.HALF_UP);
        Instant expirationTimeInstant = AuctionService.getExpirationTimeInstant(expirationTime);

        if (category.isEmpty() || auctionStatus.isEmpty()) return new ModelAndView("redirect:/account/create-auction");

        CreateAuctionDTO createAuctionDTO = new CreateAuctionDTO(userDTO.get().id(), title, description.orElse(null), startingPrice,
                auctionStatus.get(), category.get(), expirationTimeInstant);

        auctionService.createAuction(createAuctionDTO);
        return new ModelAndView("redirect:/account");
    }

    @GetMapping("/edit-auction/{id}")
    public ModelAndView getEditAuction(@PathVariable long id, ModelMap model, Principal principal){
        Optional<PrivateUserDTO> userDTO = userService.findUserPrivateDataByUsername(principal.getName());
        if (userDTO.isEmpty()) throw new NotFoundException();

        Optional<PrivateAuctionDTO> auction = auctionService.findAuctionPrivateDataById(id);

        if (auction.isEmpty()) throw new NotFoundException();
        if (auction.get().auctionStatus().getName().equals("CLOSED") || (auction.get().userId() != userDTO.get().id())) return new ModelAndView("redirect:/account/auction/" + id);

        List<Category> categories = auctionService.getAllCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("auction", auction.get());
        model.addAttribute("expirationTime",  LocalDateTime.ofInstant(auction.get().expirationTime(), ZoneOffset.UTC));


        return new ModelAndView("account/editAuction", model);
    }

    @GetMapping("/auction/{id}")
    public ModelAndView getAuctionWithId(@PathVariable long id, Principal principal, ModelMap model){
        Optional<PrivateUserDTO> userDTO = userService.findUserPrivateDataByUsername(principal.getName());
        if (userDTO.isEmpty()) throw new NotFoundException();

        Optional<PrivateAuctionDTO> auction = auctionService.findAuctionPrivateDataById(id);

        if (auction.isEmpty()) throw new NotFoundException();
        if ((auction.get().userId() != userDTO.get().id())) throw new NotFoundException();

        List<PublicBidDTO> bids = bidService.getAllPublicDataByAuctionId(id);
        BigDecimal minimalAmountOfNewBid = auctionService.getBidMinimalAmountByAuctionId(auction.get().id(), auction.get().startingPrice());

        model.addAttribute("auctionOwner", userDTO.get());
        model.addAttribute("auction", auction.get());
        model.addAttribute("bids", bids);
        model.addAttribute("minimalBid", minimalAmountOfNewBid);

        return new ModelAndView("account/auction", model);
    }

    @PostMapping("/edit-auction")
    public ModelAndView updateAuction(@RequestParam long id, @RequestParam String title, @RequestParam Optional<String> description,
                                      @RequestParam BigDecimal startingPrice, @RequestParam String categoryName,
                                      @RequestParam String expirationTime, Principal principal){
        Optional<PrivateUserDTO> userDTO = userService.findUserPrivateDataByUsername(principal.getName());
        if (userDTO.isEmpty()) throw new NotFoundException();

        Optional<PrivateAuctionDTO> auction = auctionService.findAuctionPrivateDataById(id);

        if (auction.isEmpty()) throw new NotFoundException();
        if (auction.get().auctionStatus().getName().equals("CLOSED") || (auction.get().userId() != userDTO.get().id())) return new ModelAndView("redirect:/auction/" + id);

        Optional<Category> category = auctionService.findCategoryByName(categoryName);
        Optional<AuctionStatus> auctionStatus = auctionService.findAuctionStatusByName("ACTIVE");

        startingPrice = startingPrice.setScale(2, RoundingMode.HALF_UP);
        Instant expirationTimeInstant = AuctionService.getExpirationTimeInstant(expirationTime);

        if (category.isEmpty() || auctionStatus.isEmpty()) return new ModelAndView("redirect:/account/auction/" + id);

        auctionService.updateAuction(new UpdateAuctionDTO(userDTO.get().id(), id, title, description.orElse(null), startingPrice,
                auctionStatus.get(), category.get(), expirationTimeInstant));

        return new ModelAndView("redirect:/account");
    }

    @PostMapping("/update-balance")
    public ModelAndView updateBalance(@RequestParam BigDecimal addToBalance, Principal principal){
        Optional<PrivateUserDTO> userDTO = userService.findUserPrivateDataByUsername(principal.getName());
        if (userDTO.isEmpty()) throw new NotFoundException();

        userService.updateUserData(new UpdateUserDTO(userDTO.get().id(),
                userDTO.get().username(),
                userDTO.get().balance().add(addToBalance.setScale(2, RoundingMode.HALF_UP))));

        return new ModelAndView("redirect:/account");
    }

    @PostMapping("/update-username")
    public ModelAndView updateUserName(@RequestParam String username, Principal principal, HttpServletRequest request){
        if (username.length() < 1){
            return new ModelAndView("redirect:/account");
        }
        Optional<PrivateUserDTO> userDTO = userService.findUserPrivateDataByUsername(principal.getName());
        if (userDTO.isEmpty()) return new ModelAndView("redirect:/account");

        if (userService.isUserWithNameExists(username)) return new ModelAndView("redirect:/account");

        userService.updateUserData(new UpdateUserDTO(userDTO.get().id(), username, userDTO.get().balance()));

        request.getSession().invalidate();
        return new ModelAndView("redirect:/login");
    }

    @PostMapping("/update-password")
    public ModelAndView updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword, Principal principal, HttpServletRequest request){
        if (newPassword.length() < 8){
            return new ModelAndView("redirect:/account");
        }
        Optional<PrivateUserDTO> userDTO = userService.findUserPrivateDataByUsername(principal.getName());
        if (userDTO.isEmpty()) return new ModelAndView("redirect:/account");

        if (!userService.checkIfValidPassword(userDTO.get().username(), oldPassword)) return new ModelAndView("redirect:/account");

        userService.updateUserPassword(new UpdateUserPasswordDTO(userDTO.get().id(), newPassword));

        request.getSession().invalidate();
        return new ModelAndView("redirect:/login");
    }
}
