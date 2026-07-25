package com.hostel.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hostel.enums.AllocationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "tenant_allocations",
        indexes = {
                @Index(name = "idx_allocation_tenant", columnList = "tenant_id"),
                @Index(name = "idx_allocation_bed", columnList = "bed_id"),
                @Index(name = "idx_allocation_status", columnList = "allocation_status")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TenantAllocation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    @JsonIgnoreProperties({"password", "authorities", "hibernateLazyInitializer", "handler"})
    private UserEntity tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bed_id")
    @JsonIgnoreProperties({"room", "hibernateLazyInitializer", "handler"})
    private Bed bed;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private BigDecimal monthlyRent;

    private BigDecimal depositAmount;

    @Enumerated(EnumType.STRING)
    private AllocationStatus allocationStatus;

    private Boolean active = true;
}