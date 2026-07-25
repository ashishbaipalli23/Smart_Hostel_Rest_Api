package com.hostel.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonIgnoreProperties({"password", "authorities", "hibernateLazyInitializer", "handler"})
    private UserEntity tenant;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String month;

    private LocalDate paymentDate;

    private String paymentMethod; // e.g. UPI, Net Banking, Cash

    @Column(nullable = false)
    private String status; // PENDING, PAID, FAILED

    private String transactionRef;
}
