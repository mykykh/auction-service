package com.example.auctionservice.Controllers;

import com.example.auctionservice.Configs.SecurityUserDetailsService;
import com.example.auctionservice.Models.User;
import com.example.auctionservice.Repositories.RoleRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Collections;

@Controller
public class AuthController {
    final private String DEFAULT_ROLE = "ROLE_USER";
    final SecurityUserDetailsService userDetailsManager;
    final PasswordEncoder passwordEncoder;
    final RoleRepository roleRepository;

    public AuthController(RoleRepository roleRepository, SecurityUserDetailsService userDetailsManager, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public String postSingUp(@RequestParam String username, @RequestParam String password) {
        User user = new User();

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singleton(roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new UsernameNotFoundException("Role not found"))));

        try {
            userDetailsManager.createUser(user);
        }catch (DuplicateKeyException e){

        }
        return "signUp";
    }

    @GetMapping("/signup")
    public String getSignUp(){
        return "signUpView";
    }
}
