package com.example.auctionservice.Controllers;

import com.example.auctionservice.DTOs.User.CreateUserDTO;
import com.example.auctionservice.Models.Role;
import com.example.auctionservice.Services.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import java.util.Collections;
import java.util.Optional;

@Controller
public class AuthController {
    final private String DEFAULT_ROLE = "USER";
    final private String USER_EXISTS_ERROR = "user-exists";
    final private String PASSWORD_LENGTH_ERROR = "password-length";
    final private String ROLE_NOT_FOUND_ERROR = "role-not-found";
    final private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ModelAndView postSignUp(@RequestParam String username, @RequestParam String password, ModelMap model) {
        if (username.length() < 1){
            return new ModelAndView("redirect:/account");
        }
        if (password.length() < 8){
            model.addAttribute("error", PASSWORD_LENGTH_ERROR);
            return new ModelAndView("redirect:/signup", model);
        }
        if (userService.isUserWithNameExists(username)) {
            model.addAttribute("error", USER_EXISTS_ERROR);
            return new ModelAndView("redirect:/signup", model);
        }

        Optional<Role> defaultRole = userService.findRoleByName(DEFAULT_ROLE);
        if (defaultRole.isEmpty()){
            model.addAttribute("error", ROLE_NOT_FOUND_ERROR);
            return new ModelAndView("redirect:/signup", model);
        }

        CreateUserDTO createUserDTO = new CreateUserDTO(username, password, Collections.singleton(defaultRole.get()), true);

        try {
            userService.createUser(createUserDTO);
        }catch (DuplicateKeyException e){
            model.addAttribute("error", USER_EXISTS_ERROR);
            return new ModelAndView("redirect:/signup", model);
        }

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/signup")
    public ModelAndView getSignUp(){
        return new ModelAndView("signUp");
    }

    @GetMapping("/login")
    public ModelAndView getLogin(){
        return new ModelAndView("logIn");
    }
}
