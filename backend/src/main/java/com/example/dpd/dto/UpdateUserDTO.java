package com.example.dpd.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class UpdateUserDTO {
    private String name;

    @Email(message = "Email should be valid")
    private String email;

    private LocalDate dateOfBirth;
    private String placeOfBirth;
    private String motherMaidenName;
    private String taj;
    private String taxId;

    private Set<AddressDTO> addresses;
    private Set<PhoneNumberDTO> phoneNumbers;
}