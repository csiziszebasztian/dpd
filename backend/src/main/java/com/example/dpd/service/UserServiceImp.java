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
        userRepository.deleteById(id);
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

    public void gtpr(UUID id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {

            User user = userOptional.get();
            user.setName(null);
            user.setDateOfBirth(null);
            user.setPlaceOfBirth(null);
            user.setMotherMaidenName(null);
            user.setTaj(null);
            user.setTaxId(null);
            user.setAddresses(new HashSet<>());
            user.setPhoneNumbers(new HashSet<>());

            userRepository.save(user);
        } else {
            throw new ResourceNotFoundException("User not found");
        }
    }

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
        address.setId(UUID.randomUUID());
        address.setAddress(addressDTO.getAddress());
        return address;
    }

    private AddressDTO convertToAddressDTO(Address address) {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(address.getId());
        addressDTO.setAddress(address.getAddress());
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
