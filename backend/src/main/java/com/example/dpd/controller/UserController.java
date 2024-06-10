package com.example.dpd.controller;

import com.example.dpd.dto.CreateUserDTO;
import com.example.dpd.dto.UpdateUserDTO;
import com.example.dpd.dto.UserDTO;
import com.example.dpd.exception.ResourceNotFoundException;
import com.example.dpd.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        UserDTO createdUser = userService.createUser(createUserDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        UserDTO updatedUser = userService.updateUser(id, updateUserDTO);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        Optional<UserDTO> userDTO = userService.getUserById(id);
        return userDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @PostMapping("/gtpr/{id}")
    public ResponseEntity<Void> gtpr(@PathVariable UUID id) {
        userService.gtpr(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
