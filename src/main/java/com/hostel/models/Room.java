package com.hostel.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hostel.enums.RoomStatus;
import com.hostel.enums.SharingType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(
        name = "rooms",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_hostel_room_number",
                        columnNames = {"hostel_id", "room_number"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomNumber;

    private Integer floorNumber;

    @Enumerated(EnumType.STRING)
    private SharingType sharingType;

    private Integer totalBeds;

    private Integer occupiedBeds = 0;

    private BigDecimal rentPerBed;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id")
    @JsonIgnoreProperties("rooms")
    private Hostel hostel;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("room")
    private List<Bed> beds;
}