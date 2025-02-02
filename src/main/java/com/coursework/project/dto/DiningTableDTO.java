package com.coursework.project.dto;

import com.coursework.project.entity.Reservation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DiningTableDTO {
  @Min(value = 1, message = "Capacity must be at least 1")
  private int capacity;

  private List<Reservation> reservations;
}
