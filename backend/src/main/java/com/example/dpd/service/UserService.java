package com.example.dpd.service;

import com.example.dpd.dto.CreateUserDTO;
import com.example.dpd.dto.UpdateUserDTO;
import com.example.dpd.dto.UserDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserDTO createUser(CreateUserDTO createUserDTO);
    UserDTO updateUser(UUID id, UpdateUserDTO updateUserDTO);
    void deleteUser(UUID id);
    List<UserDTO> getAllUsers();
    Optional<UserDTO> getUserById(UUID id);
    // gtpr method removed
}
