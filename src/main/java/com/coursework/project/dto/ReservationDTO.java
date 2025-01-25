package com.coursework.project.dto;

import com.coursework.project.entity.Reservation;
import com.coursework.project.entity.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ReservationDTO {
  @NotNull
  private LocalDateTime reservationTime;

  @NotNull
  private String customerName;

  @NotNull
  private String customerPhone;

  @NotNull
  private int guestCount;

  private ReservationStatus status = ReservationStatus.FREE;

  public ReservationDTO(Reservation reservation) {
    this.reservationTime = reservation.getReservationTime();
    this.customerName = reservation.getCustomerName();
    this.customerPhone = reservation.getCustomerPhone();
    this.guestCount = reservation.getGuestCount();
    this.status = reservation.getStatus();
  }
}
