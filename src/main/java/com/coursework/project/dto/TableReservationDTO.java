package com.coursework.project.dto;

import com.coursework.project.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TableReservationDTO {
  private Long tableId;
  private int capacity;
  private List<Reservation> reservations;
}
