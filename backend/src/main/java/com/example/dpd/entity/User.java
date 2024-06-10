package com.example.dpd.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "users_email_key", columnNames = {"email"})
})
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Size(max = 255)
    @Column(name = "place_of_birth")
    private String placeOfBirth;

    @Size(max = 255)
    @Column(name = "mother_maiden_name")
    private String motherMaidenName;

    @Size(max = 9)
    @Column(name = "taj", length = 9)
    private String taj;

    @Size(max = 10)
    @Column(name = "tax_id", length = 10)
    private String taxId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Address> addresses = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<PhoneNumber> phoneNumbers = new LinkedHashSet<>();

}