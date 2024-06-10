package com.example.dpd;

import com.example.dpd.dto.CreateUserDTO;
import com.example.dpd.dto.UpdateUserDTO;
import com.example.dpd.dto.UserDTO;
import com.example.dpd.entity.User;
import com.example.dpd.exception.ResourceNotFoundException;
import com.example.dpd.repository.UserRepository;
import com.example.dpd.service.UserServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImp userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        CreateUserDTO createUserDTO = CreateUserDTO.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .taj("123456789")
                .addresses(Set.of())
                .phoneNumbers(Set.of())
                .build();

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setName("John Doe");
        savedUser.setEmail("john.doe@example.com");
        savedUser.setTaj("123456789");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDTO userDTO = userService.createUser(createUserDTO);

        assertNotNull(userDTO);
        assertEquals("John Doe", userDTO.getName());
        assertEquals("john.doe@example.com", userDTO.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowUserNotFoundException() {
        UUID userId = UUID.randomUUID();
        UpdateUserDTO updateUserDTO = UpdateUserDTO.builder()
                .name("Jane Doe")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(userId, updateUserDTO));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        UUID userId = UUID.randomUUID();
        UpdateUserDTO updateUserDTO = UpdateUserDTO.builder()
                .name("Jane Doe")
                .build();

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John Doe");
        existingUser.setEmail("john.doe@example.com");
        existingUser.setTaj("123456789");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserDTO userDTO = userService.updateUser(userId, updateUserDTO);

        assertNotNull(userDTO);
        assertEquals("Jane Doe", userDTO.getName());
        assertEquals("john.doe@example.com", userDTO.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        UUID userId = UUID.randomUUID();
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Add tests for getAllUsers
    }

    @Test
    void getUserById_ShouldReturnUser() {
        UUID userId = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John Doe");
        existingUser.setEmail("john.doe@example.com");
        existingUser.setTaj("123456789");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        Optional<UserDTO> userDTO = userService.getUserById(userId);

        assertTrue(userDTO.isPresent());
        assertEquals("John Doe", userDTO.get().getName());
        assertEquals("john.doe@example.com", userDTO.get().getEmail());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_ShouldThrowUserNotFoundException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found")));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void gtpr_ShouldUpdateUserData() {
        UUID userId = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John Doe");
        existingUser.setEmail("john.doe@example.com");
        existingUser.setTaj("123456789");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        userService.gtpr(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);

        assertNull(existingUser.getName());
        assertNull(existingUser.getDateOfBirth());
        assertNull(existingUser.getPlaceOfBirth());
        assertNull(existingUser.getMotherMaidenName());
        assertNull(existingUser.getTaj());
        assertNull(existingUser.getTaxId());
        assertTrue(existingUser.getAddresses().isEmpty());
        assertTrue(existingUser.getPhoneNumbers().isEmpty());
    }

    @Test
    void gtpr_ShouldThrowResourceNotFoundException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.gtpr(userId));
        verify(userRepository, times(1)).findById(userId);
    }

}
