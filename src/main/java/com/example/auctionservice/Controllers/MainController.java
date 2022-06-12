package com.example.auctionservice.Controllers;

import com.example.auctionservice.DTOs.Auction.PublicAuctionDTO;
import com.example.auctionservice.DTOs.Auction.UpdateAuctionDTO;
import com.example.auctionservice.DTOs.Bid.CreateBidDTO;
import com.example.auctionservice.DTOs.Bid.PublicBidDTO;
import com.example.auctionservice.DTOs.User.PrivateUserDTO;
import com.example.auctionservice.DTOs.User.PublicUserDTO;
import com.example.auctionservice.DTOs.User.UpdateUserDTO;
import com.example.auctionservice.Exceptions.NotFoundException;
import com.example.auctionservice.Models.BidStatus;
import com.example.auctionservice.Services.AuctionService;
import com.example.auctionservice.Services.BidService;
import com.example.auctionservice.Services.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
public class MainController {

    private final UserService userService;
    private final AuctionService auctionService;
    private final BidService bidService;

    public MainController(UserService userService, AuctionService auctionService, BidService bidService) {
        this.userService = userService;
        this.auctionService = auctionService;
        this.bidService = bidService;
    }

    @GetMapping("/")
    public ModelAndView getAllAuctions(ModelMap model){
        List<PublicAuctionDTO> auctions = auctionService.getAllAuctionPublicData();
        model.addAttribute("auctions", auctions);
        return new ModelAndView("index", model);
    }

    @GetMapping("/user/{id}")
    public ModelAndView getAllAuctionByUser(@PathVariable Long id, ModelMap model){
        List<PublicAuctionDTO> auctions = auctionService.getAllAuctionPublicDataByUserId(id);
        model.addAttribute("auctions", auctions);
        return new ModelAndView("index", model);
    }

    @GetMapping("/category/{id}")
    public ModelAndView getAllAuctionByCategory(@PathVariable Long id, ModelMap model){
        List<PublicAuctionDTO> auctions = auctionService.getAllAuctionPublicDataByCategoryId(id);
        model.addAttribute("auctions", auctions);
        return new ModelAndView("index", model);
    }

    @GetMapping("/auction/{id}")
    public ModelAndView getAuctionWithId(@PathVariable long id, ModelMap model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<PublicAuctionDTO> auction = auctionService.findAuctionPublicDataById(id);
        if (auction.isEmpty()) throw new NotFoundException();

        Optional<PublicUserDTO> auctionOwner = userService.findUserPublicDataById(auction.get().userId());
        if (auctionOwner.isEmpty()) throw new NotFoundException();

        List<PublicBidDTO> bids = bidService.getAllPublicDataByAuctionId(id);
        BigDecimal minimalAmountOfNewBid = auctionService.getBidMinimalAmountByAuctionId(auction.get().id(), auction.get().startingPrice());

        model.addAttribute("auctionOwner", auctionOwner.get());
        model.addAttribute("auction", auction.get());
        model.addAttribute("bids", bids);
        model.addAttribute("minimalBid", minimalAmountOfNewBid);
        if (!(authentication instanceof AnonymousAuthenticationToken)){
            Optional<PrivateUserDTO> userDTO = userService.findUserPrivateDataByUsername(authentication.getName());
            userDTO.ifPresent(privateUserDTO -> model.addAttribute("currentUser", privateUserDTO));
        }

        return new ModelAndView("auction", model);
    }

    @PostMapping("/auction/{id}/makebid")
    public ModelAndView makeBid(@PathVariable long id, @RequestParam BigDecimal amount, Principal principal, ModelMap model){
        amount = amount.setScale(2, RoundingMode.HALF_UP);
        Optional<PrivateUserDTO> userDTO = userService.findUserPrivateDataByUsername(principal.getName());
        if (userDTO.isEmpty()) return new ModelAndView("redirect:/auction/" + id, model);
        if (userDTO.get().balance().compareTo(amount) < 0) return new ModelAndView("redirect:/auction/" + id, model);

        Optional<PublicAuctionDTO> auction = auctionService.findAuctionPublicDataById(id);
        if (auction.isEmpty()) return new ModelAndView("redirect:/auction/" + id, model);
        if (auction.get().userId() == userDTO.get().id()) return new ModelAndView("redirect:/auction/" + id, model);

        List<PublicBidDTO> bids = bidService.getAllPublicDataByAuctionId(id);
        Optional<BidStatus> bidStatus = bidService.findByName("ACTIVE");
        if (amount.compareTo(auctionService.getBidMinimalAmountByAuctionId(auction.get().id(), auction.get().startingPrice())) < 0) return new ModelAndView("redirect:/auction/" + id, model);
        if (bidStatus.isEmpty()) return new ModelAndView("redirect:/auction/" + id, model);

        bidService.creatBid(new CreateBidDTO(id, amount, bidStatus.get(), userDTO.get().id(), auction.get().id()));
        userService.updateUserData(new UpdateUserDTO(userDTO.get().id(), userDTO.get().username(),
                userDTO.get().balance().subtract(amount)));

        return new ModelAndView("redirect:/auction/" + id, model);
    }
}
