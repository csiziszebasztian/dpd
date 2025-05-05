package com.example.dpd.service;

import com.example.dpd.dto.*;
import com.example.dpd.entity.Address;
import com.example.dpd.entity.PhoneNumber;
import com.example.dpd.entity.User;
import com.example.dpd.exception.ResourceNotFoundException;
import com.example.dpd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    public UserDTO createUser(CreateUserDTO createUserDTO) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName(createUserDTO.getName());
        user.setEmail(createUserDTO.getEmail());
        user.setDateOfBirth(createUserDTO.getDateOfBirth());
        user.setPlaceOfBirth(createUserDTO.getPlaceOfBirth());
        user.setMotherMaidenName(createUserDTO.getMotherMaidenName());
        user.setTaj(createUserDTO.getTaj());
        user.setTaxId(createUserDTO.getTaxId());

        Set<Address> addresses = createUserDTO.getAddresses().stream()
                .map(this::convertToAddress)
                .collect(Collectors.toSet());
        addresses.forEach(address -> address.setUser(user));
        user.setAddresses(addresses);

        Set<PhoneNumber> phoneNumbers = createUserDTO.getPhoneNumbers().stream()
                .map(this::convertToPhoneNumber)
                .collect(Collectors.toSet());
        phoneNumbers.forEach(phoneNumber -> phoneNumber.setUser(user));
        user.setPhoneNumbers(phoneNumbers);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO updateUser(UUID id, UpdateUserDTO updateUserDTO) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (updateUserDTO.getName() != null) {
                user.setName(updateUserDTO.getName());
            }
            if (updateUserDTO.getEmail() != null) {
                user.setEmail(updateUserDTO.getEmail());
            }
            if(updateUserDTO.getDateOfBirth() != null) {
                user.setDateOfBirth(updateUserDTO.getDateOfBirth());
            }
            if(updateUserDTO.getPlaceOfBirth() != null) {
                user.setPlaceOfBirth(updateUserDTO.getPlaceOfBirth());
            }
            if(updateUserDTO.getMotherMaidenName() != null) {
                user.setMotherMaidenName(updateUserDTO.getMotherMaidenName());
            }
            if(updateUserDTO.getTaj() != null) {
                user.setTaj(updateUserDTO.getTaj());
            }
            if(updateUserDTO.getTaxId() != null) {
                user.setTaxId(updateUserDTO.getTaxId());
            }
            if (updateUserDTO.getAddresses() != null) {
                user.getAddresses().clear();
                Set<Address> addresses = updateUserDTO.getAddresses().stream()
                        .map(this::convertToAddress)
                        .collect(Collectors.toSet());
                addresses.forEach(address -> address.setUser(user));
                user.setAddresses(addresses);
            }

            if (updateUserDTO.getPhoneNumbers() != null) {
                user.getPhoneNumbers().clear();
                Set<PhoneNumber> phoneNumbers = updateUserDTO.getPhoneNumbers().stream()
                        .map(this::convertToPhoneNumber)
                        .collect(Collectors.toSet());
                phoneNumbers.forEach(phoneNumber -> phoneNumber.setUser(user));
                user.setPhoneNumbers(phoneNumbers);
            }

            User updatedUser = userRepository.save(user);
            return convertToDTO(updatedUser);
        } else {
            throw new ResourceNotFoundException("User not found");
        }
    }

    public void deleteUser(UUID id) {
        // GDPR-compliant deletion (Depersonalization)
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName("[DELETED]"); // Or null
            user.setEmail("[DELETED@" + user.getId().toString() + "]"); // Ensure uniqueness if email has constraint
            user.setDateOfBirth(null);
            user.setPlaceOfBirth("[DELETED]"); // Or null
            user.setMotherMaidenName("[DELETED]"); // Or null
            user.setTaj(null); // Assuming TAJ can be nullable
            user.setTaxId(null); // Assuming Tax ID can be nullable

            // Remove associated personal data collections
            user.getAddresses().clear();
            user.getPhoneNumbers().clear();
            // Note: CascadeType.ALL on User entity should handle deletion of orphaned Address/PhoneNumber rows

            userRepository.save(user);
        } else {
            // Optionally log or ignore if user not found, or re-throw
             throw new ResourceNotFoundException("User not found for depersonalization with id: " + id);
        }
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserById(UUID id) {
        return userRepository.findById(id)
                .map(this::convertToDTO);
    }

    // gtpr method removed, logic moved to deleteUser

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setDateOfBirth(user.getDateOfBirth());
        userDTO.setPlaceOfBirth(user.getPlaceOfBirth());
        userDTO.setMotherMaidenName(user.getMotherMaidenName());
        userDTO.setTaj(user.getTaj());
        userDTO.setTaxId(user.getTaxId());

        Set<AddressDTO> addressDTOs = user.getAddresses().stream()
                .map(this::convertToAddressDTO)
                .collect(Collectors.toSet());
        userDTO.setAddresses(addressDTOs);

        Set<PhoneNumberDTO> phoneNumberDTOs = user.getPhoneNumbers().stream()
                .map(this::convertToPhoneNumberDTO)
                .collect(Collectors.toSet());
        userDTO.setPhoneNumbers(phoneNumberDTOs);

        return userDTO;
    }

    private Address convertToAddress(AddressDTO addressDTO) {
        Address address = new Address();
        // If addressDTO.getId() is null, it's a new address, generate ID.
        // If addressDTO.getId() is not null, we should ideally fetch the existing Address
        // and update it, or rely on JPA merge behavior. For simplicity in create/update
        // where we clear the collection, generating a new ID might be acceptable for now,
        // but this assumes addresses are fully replaced on update.
        address.setId(addressDTO.getId() != null ? addressDTO.getId() : UUID.randomUUID());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setCity(addressDTO.getCity());
        address.setStreet(addressDTO.getStreet());
        address.setHouseNumber(addressDTO.getHouseNumber());
        address.setOtherInfo(addressDTO.getOtherInfo());
        // The user link is set in the calling method (createUser/updateUser)
        return address;
    }

    private AddressDTO convertToAddressDTO(Address address) {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(address.getId());
        addressDTO.setPostalCode(address.getPostalCode());
        addressDTO.setCity(address.getCity());
        addressDTO.setStreet(address.getStreet());
        addressDTO.setHouseNumber(address.getHouseNumber());
        addressDTO.setOtherInfo(address.getOtherInfo());
        return addressDTO;
    }

    private PhoneNumber convertToPhoneNumber(PhoneNumberDTO phoneNumberDTO) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setId(UUID.randomUUID());
        phoneNumber.setPhoneNumber(phoneNumberDTO.getPhoneNumber());
        return phoneNumber;
    }

    private PhoneNumberDTO convertToPhoneNumberDTO(PhoneNumber phoneNumber) {
        PhoneNumberDTO phoneNumberDTO = new PhoneNumberDTO();
        phoneNumberDTO.setId(phoneNumber.getId());
        phoneNumberDTO.setPhoneNumber(phoneNumber.getPhoneNumber());
        return phoneNumberDTO;
    }

}
