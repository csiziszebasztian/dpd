package com.example.dpd.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class AddressDTO {
    private UUID id;
    private String address;
}
