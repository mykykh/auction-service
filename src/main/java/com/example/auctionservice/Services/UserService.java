package com.example.auctionservice.Services;

import com.example.auctionservice.DTOs.Privilege.CreatePrivilegeDTO;
import com.example.auctionservice.DTOs.Role.CreateRoleDTO;
import com.example.auctionservice.DTOs.Role.UpdateRoleDTO;
import com.example.auctionservice.DTOs.User.*;
import com.example.auctionservice.Models.Privilege;
import com.example.auctionservice.Models.Role;
import com.example.auctionservice.Models.User;
import com.example.auctionservice.Repositories.PrivilegeRepository;
import com.example.auctionservice.Repositories.RoleRepository;
import com.example.auctionservice.Repositories.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    final private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PrivilegeRepository privilegeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean checkIfValidPassword(String username, String password){
        Optional<User> userOptional = userRepository.findByName(username);
        if (userOptional.isEmpty()) return false;
        else return passwordEncoder.matches(password, userOptional.get().getPassword());
    }

    @PreAuthorize("hasAuthority('SELECT_ROLES') or hasRole('ADMIN')")
    public boolean isPrivilegeWithNameExists(String privilegeName){
        return privilegeRepository.isPrivilegeWithNameExists(privilegeName);
    }
    @PreAuthorize("hasAuthority('SELECT_ROLES') or hasRole('ADMIN')")
    public boolean isRoleWithNameExists(String roleName){
        return roleRepository.isRoleWithNameExists(roleName);
    }
    public boolean isUserWithNameExists(String userName){
        return userRepository.isUserWithNameExists(userName);
    }

    @PreAuthorize("hasAuthority('SELECT_ROLES') or hasRole('ADMIN')")
    public List<Privilege> getAllPrivileges(){
        return privilegeRepository.getAll();
    }
    @PreAuthorize("hasAuthority('SELECT_ROLES') or hasRole('ADMIN')")
    public List<Role> getAllUserRoles(){
        return roleRepository.getAll();
    }
    @PreAuthorize("hasAuthority('SELECT_USERS') or hasRole('ADMIN')")
    public List<PrivateUserDTO> getAllUserPrivateData(){
        return userRepository.getAll().stream().map(this::convertToPrivateDto).collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('SELECT_ROLES') or hasRole('ADMIN')")
    public Optional<Privilege> findPrivilegeByName(String privilegeName){
        return privilegeRepository.findByName(privilegeName);
    }

    public Optional<Role> findRoleByName(String roleName){
        return roleRepository.findByName(roleName);
    }
    @PreAuthorize("hasAuthority('SELECT_USERS') or hasRole('ADMIN') or #username == authentication.principal.username")
    public Optional<PrivateUserDTO> findUserPrivateDataByUsername(String username){
        return userRepository.findByName(username).map(this::convertToPrivateDto);
    }

    @PreAuthorize("hasAuthority('SELECT_ROLES') or hasRole('ADMIN')")
    public Optional<Privilege> findPrivilegeById(long privilegeId){
        return privilegeRepository.findById(privilegeId);
    }
    @PreAuthorize("hasAuthority('SELECT_ROLES') or hasRole('ADMIN')")
    public Optional<Role> findRoleById(long roleId){
        return roleRepository.findById(roleId);
    }

    public Optional<PublicUserDTO> findUserPublicDataById(long userId){
        return userRepository.findById(userId).map(this::convertToPublicDto);
    }
    @PreAuthorize("hasAuthority('SELECT_USERS') or hasRole('ADMIN') or #userId == authentication.principal.id")
    public Optional<PrivateUserDTO> findUserPrivateDataById(long userId){
        return userRepository.findById(userId).map(this::convertToPrivateDto);
    }

    @PreAuthorize("hasAuthority('UPDATE_ROLES') or hasRole('ADMIN')")
    public void createRole(CreateRoleDTO createRoleDTO){
        roleRepository.create(createRoleDTO);
    }
    @PreAuthorize("hasAuthority('UPDATE_ROLES') or hasRole('ADMIN')")
    public void createPrivilege(CreatePrivilegeDTO createPrivilegeDTO){
        privilegeRepository.create(createPrivilegeDTO);
    }
    public void createUser(CreateUserDTO createUserDTO){
        userRepository.create(new CreateUserDTO(createUserDTO.username(), passwordEncoder.encode(createUserDTO.password()), createUserDTO.roles(), createUserDTO.enabled()));
    }

    @PreAuthorize("hasAuthority('UPDATE_ROLES') or hasRole('ADMIN')")
    public void updateRole(UpdateRoleDTO updateRoleDTO){
        roleRepository.update(updateRoleDTO);
    }
    @PreAuthorize("hasAuthority('UPDATE_USERS') or hasRole('ADMIN') or #userDTO.id() == authentication.principal.id")
    public void updateUserData(UpdateUserDTO userDTO){
        userRepository.update(userDTO);
    }
    @PreAuthorize("hasAuthority('UPDATE_USERS') or hasRole('ADMIN') or #userDTO.id() == authentication.principal.id")
    public void updateUserData(UpdateUserAndUserRolesDTO userDTO){
        userRepository.update(userDTO);
    }
    @PreAuthorize("hasAuthority('UPDATE_USERS') or hasRole('ADMIN') or #userPasswordDTO.id() == authentication.principal.id")
    public void updateUserPassword(UpdateUserPasswordDTO userPasswordDTO){
        userRepository.updatePassword(new UpdateUserPasswordDTO(userPasswordDTO.id(), passwordEncoder.encode(userPasswordDTO.password())));
    }

    @PreAuthorize("hasAuthority('UPDATE_ROLES') or hasRole('ADMIN')")
    public void deleteRole(long roleId){
        roleRepository.delete(roleId);
    }
    @PreAuthorize("hasAuthority('UPDATE_ROLES') or hasRole('ADMIN')")
    public void deletePrivilege(long privilegeId){
        privilegeRepository.delete(privilegeId);
    }

    public PublicUserDTO convertToPublicDto(User user){
        return new PublicUserDTO(user.getId(), user.getUsername());
    }

    public PrivateUserDTO convertToPrivateDto(User user){
        return new PrivateUserDTO(user.getId(), user.getUsername(), user.getBalance(), user.getRoles(), user.getUpdatedTime(), user.getCreatedTime());
    }
}
