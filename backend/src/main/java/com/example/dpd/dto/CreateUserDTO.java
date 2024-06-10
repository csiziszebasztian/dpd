package com.example.dpd.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class CreateUserDTO {

    @NotBlank(message = "Name is mandatory")
    private String name;
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;
    @NotNull(message = "Date of birth is mandatory")
    private LocalDate dateOfBirth;
    @NotBlank(message = "Place of birth is mandatory")
    private String placeOfBirth;
    @NotBlank(message = "Mother maiden name is mandatory")
    private String motherMaidenName;
    @NotBlank(message = "TAJ is mandatory")
    private String taj;
    @NotBlank(message = "Tax id is mandatory")
    private String taxId;

    private Set<AddressDTO> addresses;
    private Set<PhoneNumberDTO> phoneNumbers;

}
