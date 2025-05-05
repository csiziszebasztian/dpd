package com.example.dpd.entity;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private User user;

    @Size(max = 10) // Example size, adjust if needed
    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Size(max = 255)
    @Column(name = "city")
    private String city;

    @Size(max = 255)
    @Column(name = "street")
    private String street;

    @Size(max = 50) // Example size, adjust if needed
    @Column(name = "house_number", length = 50)
    private String houseNumber;

    @Column(name = "other_info")
    private String otherInfo; // No size limit, defaults usually suffice for text/varchar

}
