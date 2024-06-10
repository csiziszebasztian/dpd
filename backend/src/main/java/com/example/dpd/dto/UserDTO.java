package com.example.dpd.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private LocalDate dateOfBirth;
    private String placeOfBirth;
    private String motherMaidenName;
    private String taj;
    private String taxId;
    private Set<AddressDTO> addresses;
    private Set<PhoneNumberDTO> phoneNumbers;
}
