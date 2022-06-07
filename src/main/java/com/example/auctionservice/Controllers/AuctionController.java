package com.example.auctionservice.Controllers;

import com.example.auctionservice.Models.Auction;
import com.example.auctionservice.Repositories.AuctionRepository;
import com.example.auctionservice.Repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auctions")
public class AuctionController {

    final AuctionRepository auctionRepository;
    final UserRepository userRepository;
    public AuctionController(AuctionRepository auctionRepository, UserRepository userRepository) {
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Auction> getAll(){
        return auctionRepository.getAll();
    }
}
