package com.hemanth.slmsuite.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "warranty_claims")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WarrantyClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(nullable = false, length = 1000)
    private String issueDescription;

    @Column(nullable = false)
    private String status; // SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, PARTS_ISSUED, CLOSED

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    private String rejectionReason;

    @Column(nullable = false)
    private String serialNumber;

    @Column(nullable = false)
    private java.time.LocalDate purchaseDate;
}