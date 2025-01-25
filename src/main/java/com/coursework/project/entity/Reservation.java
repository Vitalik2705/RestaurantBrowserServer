package com.coursework.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Data
public class Reservation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDateTime reservationTime;

  @Column(nullable = false)
  private String customerName;

  @Column(nullable = false)
  private String customerPhone;

  @Column(nullable = false)
  private int guestCount;

  @Enumerated(EnumType.STRING)
  private ReservationStatus status = ReservationStatus.FREE;

  @ManyToOne
  @JoinColumn(name = "table_id", nullable = false)
  @JsonIgnore
  private DiningTable diningTable;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}

