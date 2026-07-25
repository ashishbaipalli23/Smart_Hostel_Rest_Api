package com.hostel.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hostel.enums.BedStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "beds",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_room_bed_number",
                        columnNames = {"room_id", "bed_number"}
                )
        },
        indexes = {
                @Index(name = "idx_bed_status", columnList = "status")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Bed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bedNumber;

    @Enumerated(EnumType.STRING)
    private BedStatus status;

    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @JsonIgnoreProperties("beds")
    private Room room;
}