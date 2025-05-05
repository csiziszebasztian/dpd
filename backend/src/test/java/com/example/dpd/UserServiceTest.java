package com.example.dpd;

import com.example.dpd.dto.*;
import com.example.dpd.entity.Address;
import com.example.dpd.entity.PhoneNumber;
import com.example.dpd.entity.User;
import com.example.dpd.exception.ResourceNotFoundException;
import com.example.dpd.repository.UserRepository;
import com.example.dpd.service.UserServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void createUser_ShouldReturnCreatedUser() {
        // Arrange
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setPostalCode("1111");
        addressDTO.setCity("Test City");
        addressDTO.setStreet("Test Street");
        addressDTO.setHouseNumber("1");

        PhoneNumberDTO phoneDTO = new PhoneNumberDTO();
        phoneDTO.setPhoneNumber("123456789");

        CreateUserDTO createUserDTO = CreateUserDTO.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .placeOfBirth("Test Place")
                .motherMaidenName("Test Maiden")
                .taj("123456789")
                .taxId("1234567890")
                .addresses(Set.of(addressDTO))
                .phoneNumbers(Set.of(phoneDTO))
                .build();

        // Mock the save operation to return the user passed to it, adding an ID
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            userToSave.setId(UUID.randomUUID()); // Simulate DB generating ID
            // Simulate cascade setting IDs for children if needed, though not strictly necessary for this test
            userToSave.getAddresses().forEach(a -> a.setId(UUID.randomUUID()));
            userToSave.getPhoneNumbers().forEach(p -> p.setId(UUID.randomUUID()));
            return userToSave;
        });

        // Act
        UserDTO userDTO = userService.createUser(createUserDTO);

        // Assert
        assertNotNull(userDTO);
        assertNotNull(userDTO.getId());
        assertEquals("John Doe", userDTO.getName());
        assertEquals("john.doe@example.com", userDTO.getEmail());
        assertEquals("123456789", userDTO.getTaj());
        assertEquals("1234567890", userDTO.getTaxId());
        assertEquals(LocalDate.of(1990, 1, 1), userDTO.getDateOfBirth());
        assertEquals("Test Place", userDTO.getPlaceOfBirth());
        assertEquals("Test Maiden", userDTO.getMotherMaidenName());

        assertThat(userDTO.getAddresses()).hasSize(1);
        AddressDTO savedAddress = userDTO.getAddresses().iterator().next();
        assertEquals("1111", savedAddress.getPostalCode());
        assertEquals("Test City", savedAddress.getCity());
        assertEquals("Test Street", savedAddress.getStreet());
        assertEquals("1", savedAddress.getHouseNumber());

        assertThat(userDTO.getPhoneNumbers()).hasSize(1);
        assertEquals("123456789", userDTO.getPhoneNumbers().iterator().next().getPhoneNumber());

        verify(userRepository, times(1)).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertNotNull(capturedUser.getId()); // ID should be null before save
        assertEquals("John Doe", capturedUser.getName());
        assertThat(capturedUser.getAddresses()).hasSize(1);
        assertThat(capturedUser.getPhoneNumbers()).hasSize(1);
        // Check if user is set correctly in children before save
        capturedUser.getAddresses().forEach(a -> assertSame(capturedUser, a.getUser()));
        capturedUser.getPhoneNumbers().forEach(p -> assertSame(capturedUser, p.getUser()));
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
        // Arrange
        UUID userId = UUID.randomUUID();
        AddressDTO updatedAddressDTO = new AddressDTO();
        updatedAddressDTO.setId(UUID.randomUUID()); // Simulate existing address ID
        updatedAddressDTO.setPostalCode("2222");
        updatedAddressDTO.setCity("New City");
        updatedAddressDTO.setStreet("New Street");
        updatedAddressDTO.setHouseNumber("2");

        PhoneNumberDTO updatedPhoneDTO = new PhoneNumberDTO();
        updatedPhoneDTO.setId(UUID.randomUUID()); // Simulate existing phone ID
        updatedPhoneDTO.setPhoneNumber("987654321");


        UpdateUserDTO updateUserDTO = UpdateUserDTO.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .addresses(Set.of(updatedAddressDTO)) // Update with new address list
                .phoneNumbers(Set.of(updatedPhoneDTO)) // Update with new phone list
                .build();

        User existingUser = User.builder()
                .id(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .taj("123456789")
                .addresses(new HashSet<>()) // Start with empty sets
                .phoneNumbers(new HashSet<>())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        // Mock save to return the user passed to it
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserDTO userDTO = userService.updateUser(userId, updateUserDTO);

        // Assert
        assertNotNull(userDTO);
        assertEquals(userId, userDTO.getId());
        assertEquals("Jane Doe", userDTO.getName());
        assertEquals("jane.doe@example.com", userDTO.getEmail()); // Email updated
        assertEquals("123456789", userDTO.getTaj()); // TAJ not in DTO, should remain unchanged

        assertThat(userDTO.getAddresses()).hasSize(1);
        AddressDTO savedAddress = userDTO.getAddresses().iterator().next();
        assertEquals("2222", savedAddress.getPostalCode());
        assertEquals("New City", savedAddress.getCity());

        assertThat(userDTO.getPhoneNumbers()).hasSize(1);
        assertEquals("987654321", userDTO.getPhoneNumbers().iterator().next().getPhoneNumber());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();
        assertEquals("Jane Doe", capturedUser.getName());
        assertEquals("jane.doe@example.com", capturedUser.getEmail());
        assertThat(capturedUser.getAddresses()).hasSize(1);
        assertThat(capturedUser.getPhoneNumbers()).hasSize(1);
        // Check user link in children
        capturedUser.getAddresses().forEach(a -> assertSame(capturedUser, a.getUser()));
        capturedUser.getPhoneNumbers().forEach(p -> assertSame(capturedUser, p.getUser()));
    }


    @Test
    void deleteUser_ShouldDepersonalizeAndSaveUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User existingUser = User.builder()
                .id(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .taj("123456789")
                .addresses(new HashSet<>(Set.of(Address.builder().id(UUID.randomUUID()).city("Test").build()))) // Add dummy address
                .phoneNumbers(new HashSet<>(Set.of(PhoneNumber.builder().id(UUID.randomUUID()).phoneNumber("123").build()))) // Add dummy phone
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser); // Mock save

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(userArgumentCaptor.capture());
        verify(userRepository, never()).deleteById(any(UUID.class)); // Ensure hard delete is NOT called

        User capturedUser = userArgumentCaptor.getValue();
        assertEquals("[DELETED]", capturedUser.getName());
        assertEquals("[DELETED@" + userId + "]", capturedUser.getEmail()); // Check unique deleted email
        assertNull(capturedUser.getDateOfBirth());
        assertEquals("[DELETED]", capturedUser.getPlaceOfBirth());
        assertEquals("[DELETED]", capturedUser.getMotherMaidenName());
        assertNull(capturedUser.getTaj());
        assertNull(capturedUser.getTaxId());
        assertThat(capturedUser.getAddresses()).isEmpty(); // Check collections are cleared
        assertThat(capturedUser.getPhoneNumbers()).isEmpty();
    }

     @Test
    void deleteUser_ShouldThrowNotFoundWhenUserDoesNotExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllUsers_ShouldReturnListOfUserDTOs() {
        // Arrange
        User user1 = User.builder().id(UUID.randomUUID()).name("User One").email("one@test.com").addresses(Set.of()).phoneNumbers(Set.of()).build();
        User user2 = User.builder().id(UUID.randomUUID()).name("User Two").email("two@test.com").addresses(Set.of()).phoneNumbers(Set.of()).build();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<UserDTO> userDTOs = userService.getAllUsers();

        // Assert
        assertThat(userDTOs).hasSize(2);
        assertThat(userDTOs).extracting(UserDTO::getName).containsExactlyInAnyOrder("User One", "User Two");
        assertThat(userDTOs).extracting(UserDTO::getEmail).containsExactlyInAnyOrder("one@test.com", "two@test.com");
        verify(userRepository, times(1)).findAll();
    }

     @Test
    void getAllUsers_ShouldReturnEmptyListWhenNoUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<UserDTO> userDTOs = userService.getAllUsers();

        // Assert
        assertThat(userDTOs).isEmpty();
        verify(userRepository, times(1)).findAll();
    }


    @Test
    void getUserById_ShouldReturnUserDTOWhenFound() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User existingUser = User.builder()
                .id(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .taj("123456789")
                .addresses(Set.of())
                .phoneNumbers(Set.of())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act
        Optional<UserDTO> userDTOOptional = userService.getUserById(userId);

        // Assert
        assertTrue(userDTOOptional.isPresent());
        UserDTO userDTO = userDTOOptional.get();
        assertEquals(userId, userDTO.getId());
        assertEquals("John Doe", userDTO.getName());
        assertEquals("john.doe@example.com", userDTO.getEmail());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_ShouldReturnEmptyOptionalWhenNotFound() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<UserDTO> userDTOOptional = userService.getUserById(userId);

        // Assert
        assertTrue(userDTOOptional.isEmpty()); // Check if Optional is empty
        verify(userRepository, times(1)).findById(userId);
    }

    // Removed gtpr tests as the method was removed from the service

}
