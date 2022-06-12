package com.example.auctionservice.Controllers;

import com.example.auctionservice.Models.Auction;
import com.example.auctionservice.Models.Bid;
import com.example.auctionservice.Repositories.AuctionRepository;
import com.example.auctionservice.Repositories.BidRepository;
import com.example.auctionservice.Repositories.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auctions")
public class AuctionController {

    final AuctionRepository auctionRepository;
    final UserRepository userRepository;
    final BidRepository bidRepository;
    public AuctionController(AuctionRepository auctionRepository, UserRepository userRepository, BidRepository bidRepository) {
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
    }

    @GetMapping
    public List<Auction> getAll(){
        return auctionRepository.getAll();
    }

    @GetMapping("/bets")
    public List<Bid> getAllBids(){
        return bidRepository.getAll();
    }
}
