package com.coursework.project.dto;

import com.coursework.project.entity.ReservationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserReservationDTO {
  private Long id;
  private LocalDateTime reservationTime;
  private String customerName;
  private String customerPhone;
  private int guestCount;
  private ReservationStatus status;
  private Long tableId;
  private Long restaurantId;
  private String restaurantName;
}
