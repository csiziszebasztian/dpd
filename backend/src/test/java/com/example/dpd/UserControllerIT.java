package com.example.dpd;

import com.example.dpd.dto.CreateUserDTO;
import com.example.dpd.dto.UpdateUserDTO;
import com.example.dpd.entity.User;
import com.example.dpd.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        CreateUserDTO createUserDTO = CreateUserDTO.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .taj("123456789")
                .addresses(Set.of())
                .phoneNumbers(Set.of())
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setTaj("123456789");
        userRepository.save(user);

        UpdateUserDTO updateUserDTO = UpdateUserDTO.builder()
                .name("Jane Doe")
                .build();

        mockMvc.perform(patch("/api/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setTaj("123456789");
        userRepository.save(user);

        mockMvc.perform(delete("/api/users/{id}", user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllUsers_ShouldReturnUsers() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setTaj("123456789");
        userRepository.save(user);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setTaj("123456789");
        userRepository.save(user);

        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void getUserById_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void gtpr_ShouldReturnNoContent() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setTaj("123456789");
        userRepository.save(user);

        mockMvc.perform(post("/api/users/gtpr/{id}", user.getId()))
                .andExpect(status().isNoContent());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertNull(updatedUser.getName());
        assertNull(updatedUser.getDateOfBirth());
        assertNull(updatedUser.getPlaceOfBirth());
        assertNull(updatedUser.getMotherMaidenName());
        assertNull(updatedUser.getTaj());
        assertNull(updatedUser.getTaxId());
        assertTrue(updatedUser.getAddresses().isEmpty());
        assertTrue(updatedUser.getPhoneNumbers().isEmpty());
    }

}
