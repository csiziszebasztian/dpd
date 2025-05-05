package com.example.dpd;

import com.example.dpd.controller.UserController;
import com.example.dpd.dto.AddressDTO;
import com.example.dpd.dto.CreateUserDTO;
import com.example.dpd.dto.PhoneNumberDTO;
import com.example.dpd.dto.UpdateUserDTO;
import com.example.dpd.dto.UserDTO;
import com.example.dpd.exception.ResourceNotFoundException;
import com.example.dpd.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class) // Test only the UserController layer
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Creates a Mockito mock and registers it in the ApplicationContext
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON

    @Test
    void createUser_ShouldReturnCreatedUserAndStatus201() throws Exception {
        // Arrange
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setPostalCode("1234"); // Added
        addressDTO.setCity("Test City");
        addressDTO.setStreet("Test Street"); // Added
        addressDTO.setHouseNumber("1A"); // Added

        PhoneNumberDTO phoneDTO = new PhoneNumberDTO();
        phoneDTO.setPhoneNumber("123456789"); // Use a more realistic number

        CreateUserDTO createUserDTO = CreateUserDTO.builder()
                .name("John Doe")
                .email("john@test.com")
                .dateOfBirth(LocalDate.now())
                .placeOfBirth("Test Place") // Added
                .motherMaidenName("Test Maiden") // Added
                .taj("123456789") // Added
                .taxId("1234567890") // Added
                .addresses(Set.of(addressDTO))
                .phoneNumbers(Set.of(phoneDTO))
                .build();

        UserDTO returnedUserDTO = new UserDTO();
        returnedUserDTO.setId(UUID.randomUUID()); // Use String ID as in DTO
        returnedUserDTO.setName(createUserDTO.getName());
        returnedUserDTO.setEmail(createUserDTO.getEmail());
        returnedUserDTO.setDateOfBirth(createUserDTO.getDateOfBirth());
        returnedUserDTO.setPlaceOfBirth(createUserDTO.getPlaceOfBirth());
        returnedUserDTO.setMotherMaidenName(createUserDTO.getMotherMaidenName());
        returnedUserDTO.setTaj(createUserDTO.getTaj());
        returnedUserDTO.setTaxId(createUserDTO.getTaxId());
        // Note: We don't need to mock addresses/phones in returned DTO for this test's purpose

        when(userService.createUser(any(CreateUserDTO.class))).thenReturn(returnedUserDTO);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isCreated()) // 201
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(returnedUserDTO.getId())))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@test.com")));

        verify(userService, times(1)).createUser(any(CreateUserDTO.class));
    }

     @Test
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange: Create DTO missing required fields (e.g., name) based on @Valid
        CreateUserDTO invalidCreateUserDTO = CreateUserDTO.builder()
                .email("john@test.com") // Missing name
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCreateUserDTO)))
                .andExpect(status().isBadRequest()); // Expect 400 due to validation failure

        verify(userService, never()).createUser(any(CreateUserDTO.class)); // Service method should not be called
    }


    @Test
    void updateUser_ShouldReturnUpdatedUserAndStatus200() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        UpdateUserDTO updateUserDTO = UpdateUserDTO.builder().name("Jane Doe").build();

        UserDTO returnedUserDTO = new UserDTO();
        returnedUserDTO.setId(userId); // Use String ID
        returnedUserDTO.setName(updateUserDTO.getName());
        returnedUserDTO.setEmail("original@test.com"); // Assume email wasn't updated

        when(userService.updateUser(eq(userId), any(UpdateUserDTO.class))).thenReturn(returnedUserDTO);

        // Act & Assert
        mockMvc.perform(patch("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk()) // 200
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userId.toString())))
                .andExpect(jsonPath("$.name", is("Jane Doe")))
                .andExpect(jsonPath("$.email", is("original@test.com")));

        verify(userService, times(1)).updateUser(eq(userId), any(UpdateUserDTO.class));
    }

    @Test
    void updateUser_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        UpdateUserDTO updateUserDTO = UpdateUserDTO.builder().name("Jane Doe").build();

        when(userService.updateUser(eq(userId), any(UpdateUserDTO.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        // Act & Assert
        mockMvc.perform(patch("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isNotFound()); // Expect 404

        verify(userService, times(1)).updateUser(eq(userId), any(UpdateUserDTO.class));
    }


    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        doNothing().when(userService).deleteUser(userId); // Mock the void method

        // Act & Assert
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent()); // 204

        verify(userService, times(1)).deleteUser(userId);
    }

     @Test
    void deleteUser_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        // Simulate service throwing exception when user not found during delete/depersonalization
        doThrow(new ResourceNotFoundException("User not found"))
            .when(userService).deleteUser(userId);

        // Act & Assert
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNotFound()); // Expect 404

        verify(userService, times(1)).deleteUser(userId);
    }


    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        // Arrange
        UserDTO user1 = new UserDTO(); user1.setId(UUID.randomUUID()); user1.setName("User One"); // Use String ID
        UserDTO user2 = new UserDTO(); user2.setId(UUID.randomUUID()); user2.setName("User Two"); // Use String ID
        List<UserDTO> userList = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(userList);

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk()) // 200
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("User One")))
                .andExpect(jsonPath("$[1].name", is("User Two")));

        verify(userService, times(1)).getAllUsers();
    }

     @Test
    void getAllUsers_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk()) // 200
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService, times(1)).getAllUsers();
    }


    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId); // Use String ID
        userDTO.setName("Specific User");
        userDTO.setEmail("specific@test.com");

        when(userService.getUserById(userId)).thenReturn(Optional.of(userDTO));

        // Act & Assert
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk()) // 200
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userId.toString())))
                .andExpect(jsonPath("$.name", is("Specific User")))
                .andExpect(jsonPath("$.email", is("specific@test.com")));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        // The controller throws ResourceNotFoundException when Optional is empty
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound()); // Expect 404

        verify(userService, times(1)).getUserById(userId);
    }
}
