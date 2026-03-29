    package com.hostel.models;

    import com.hostel.utilties.Roles;
    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;
    import lombok.ToString;
    import org.hibernate.annotations.CreationTimestamp;
    import org.hibernate.annotations.UpdateTimestamp;

    import java.time.LocalDate;
    import java.time.LocalDateTime;

    @Table(name = "users_tab")
    @Entity
    @Getter
    @Setter
    @ToString
    public class UserEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String name;

        @Column(nullable = false, unique = true)
        private String username; //email_ID

        @Column(nullable = false)
        private String password;


        @Column(name = "phone_number", nullable = false)
        private String phoneNumber;


        @Column(name = "aadhaar_number", unique = true)
        private String aadhaarNumber;


        private String address;

        private String city;

        private String state;

        private String pincode;




        private LocalDate joiningDate;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Roles role;

        @CreationTimestamp
        @Column(nullable = false,updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        @Column
        private LocalDateTime updatedAt;



       @Column(name = "active_switch")
        private Boolean isEnabled = true;





    }
