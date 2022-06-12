package com.example.auctionservice.Controllers;

import com.example.auctionservice.DTOs.Privilege.CreatePrivilegeDTO;
import com.example.auctionservice.DTOs.Role.CreateRoleDTO;
import com.example.auctionservice.DTOs.Role.UpdateRoleDTO;
import com.example.auctionservice.DTOs.User.PrivateUserDTO;
import com.example.auctionservice.DTOs.User.UpdateUserAndUserRolesDTO;
import com.example.auctionservice.DTOs.User.UpdateUserDTO;
import com.example.auctionservice.Exceptions.NotFoundException;
import com.example.auctionservice.Models.Privilege;
import com.example.auctionservice.Models.Role;
import com.example.auctionservice.Services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ModelAndView getAdminPanel(ModelMap model){
        return new ModelAndView("admin/index", model);
    }

    @GetMapping("/users")
    public ModelAndView getAllUsers(ModelMap model){
        List<PrivateUserDTO> userDTOList = userService.getAllUserPrivateData();
        model.addAttribute("users", userDTOList);
        return new ModelAndView("admin/usersList", model);
    }

    @GetMapping("/user/{id}")
    public ModelAndView editUser(@PathVariable long id, ModelMap model){
        Optional<PrivateUserDTO> userDTOOptional = userService.findUserPrivateDataById(id);
        if (userDTOOptional.isEmpty()) throw new NotFoundException();

        List<Role> roles = userService.getAllUserRoles();

        model.addAttribute("user", userDTOOptional.get());
        model.addAttribute("existingRoles", roles);

        return new ModelAndView("admin/editUser", model);
    }

    @PostMapping("/user/edit-user")
    public ModelAndView editUser(UpdateUserDTO updateUserDTO, @RequestParam Optional<List<Long>> roles){
        Optional<PrivateUserDTO> userDTOOptional = userService.findUserPrivateDataById(updateUserDTO.id());
        if (userDTOOptional.isEmpty()) return new ModelAndView("redirect:/admin/users");

        if (!updateUserDTO.username().equals(userDTOOptional.get().username())){
            if (userService.isUserWithNameExists(updateUserDTO.username())) return new ModelAndView("redirect:/admin/user/" + updateUserDTO.id());
        }

        List<Role> newRoles = new ArrayList<>();
        if (roles.isPresent()){
            for (long roleId : roles.get()){
                Optional<Role> newRoleOptional = userService.findRoleById(roleId);
                newRoleOptional.ifPresent(newRole -> newRoles.add(newRole));
            }
        }

        userService.updateUserData(new UpdateUserAndUserRolesDTO(updateUserDTO.id(), updateUserDTO.username(), updateUserDTO.balance(), newRoles));
        return new ModelAndView("redirect:/admin/user/" + updateUserDTO.id());
    }

    @GetMapping("/roles")
    public ModelAndView getAllRoles(ModelMap model){
        List<Role> roles = userService.getAllUserRoles();
        List<Privilege> privileges = userService.getAllPrivileges();

        model.addAttribute("roles", roles);
        model.addAttribute("existingPrivileges", privileges);

        return new ModelAndView("admin/rolesList", model);
    }

    @GetMapping("/role/{id}")
    public ModelAndView editRole(@PathVariable long id, ModelMap model){
        Optional<Role> roleOptional = userService.findRoleById(id);
        if (roleOptional.isEmpty()) throw new NotFoundException();

        List<Privilege> privileges = userService.getAllPrivileges();

        model.addAttribute("role", roleOptional.get());
        model.addAttribute("existingPrivileges", privileges);

        return new ModelAndView("admin/editRole", model);
    }

    @PostMapping("/role/create-role")
    public ModelAndView createRole(@RequestParam String name, @RequestParam Optional<List<Long>> privileges){
        if (userService.isRoleWithNameExists(name)) return new ModelAndView("redirect:/admin/roles");

        List<Privilege> newPrivileges = new ArrayList<>();
        if (privileges.isPresent()){
            for (long privilegeId : privileges.get()){
                Optional<Privilege> newPrivilegeOptional = userService.findPrivilegeById(privilegeId);
                newPrivilegeOptional.ifPresent(newPrivilege -> newPrivileges.add(newPrivilege));
            }
        }

        userService.createRole(new CreateRoleDTO(name, newPrivileges));
        return new ModelAndView("redirect:/admin/roles");
    }

    @PostMapping("/role/edit-role")
    public ModelAndView editRole(@RequestParam long id, @RequestParam String name, @RequestParam Optional<List<Long>> privileges){
        Optional<Role> roleOptional = userService.findRoleById(id);
        if (roleOptional.isEmpty()) return new ModelAndView("redirect:/admin/roles/");

        if (!name.equals(roleOptional.get().getName())){
            if (userService.isRoleWithNameExists(name)) return new ModelAndView("redirect:/admin/role/" + id);
        }

        List<Privilege> newPrivileges = new ArrayList<>();
        if (privileges.isPresent()){
            for (long privilegeId : privileges.get()){
                Optional<Privilege> newPrivilegeOptional = userService.findPrivilegeById(privilegeId);
                newPrivilegeOptional.ifPresent(newPrivilege -> newPrivileges.add(newPrivilege));
            }
        }

        userService.updateRole(new UpdateRoleDTO(id, name, newPrivileges));
        return new ModelAndView("redirect:/admin/role/" + id);
    }

    @PostMapping("/role/delete-role")
    public ModelAndView deleteRole(@RequestParam long id){
        userService.deleteRole(id);
        return new ModelAndView("redirect:/admin/roles");
    }

    @GetMapping("/privileges")
    public ModelAndView getAllPrivileges(ModelMap model){
        List<Privilege> privileges = userService.getAllPrivileges();

        model.addAttribute("privileges", privileges);

        return new ModelAndView("admin/privilegesList", model);
    }

    @PostMapping("/privilege/create-privilege")
    public ModelAndView createPrivilege(@RequestParam String name){
        if (userService.isPrivilegeWithNameExists(name)) return new ModelAndView("redirect:/admin/privileges");

        userService.createPrivilege(new CreatePrivilegeDTO(name));
        return new ModelAndView("redirect:/admin/privileges");
    }

    @PostMapping("/privilege/delete-privilege")
    public ModelAndView deletePrivilege(@RequestParam long id){
        userService.deletePrivilege(id);
        return new ModelAndView("redirect:/admin/privileges");
    }
}
