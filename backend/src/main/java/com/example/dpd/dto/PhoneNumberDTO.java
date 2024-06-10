package com.example.dpd.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PhoneNumberDTO {
    private UUID id;
    private String phoneNumber;
}
