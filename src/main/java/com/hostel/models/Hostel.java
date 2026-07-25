package com.hostel.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hostel.enums.GenderType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "hostels")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Hostel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String code;

    private String address;

    private String city;

    private String state;

    @Column(name = "pin_code")
    private String pinCode;

    @Enumerated(EnumType.STRING)
    private GenderType genderType;

    private Integer totalFloors;

    private Boolean active;

    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("hostel")
    private List<Room> rooms;
}