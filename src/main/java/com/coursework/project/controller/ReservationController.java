package com.coursework.project.controller;

import com.coursework.project.dto.ReservationDTO;
import com.coursework.project.dto.TableReservationDTO;
import com.coursework.project.dto.UserReservationDTO;
import com.coursework.project.entity.Reservation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.coursework.project.service.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
  private final ReservationService reservationService;

  public ReservationController(ReservationService reservationService) {
    this.reservationService = reservationService;
  }

  @PostMapping("/tables/{tableId}/{userId}")
  public ResponseEntity<Reservation> createReservation(
          @PathVariable Long tableId,
          @PathVariable Long userId,
          @RequestBody @Valid ReservationDTO reservationDTO) {
    return ResponseEntity.ok(reservationService.createReservation(tableId, userId, reservationDTO));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
    reservationService.deleteReservation(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/restaurants/{restaurantId}")
  public ResponseEntity<List<TableReservationDTO>> getRestaurantReservations(
          @PathVariable Long restaurantId) {
    return ResponseEntity.ok(reservationService.getReservationsByRestaurant(restaurantId));
  }

  @GetMapping("/users/{userId}")
  public ResponseEntity<List<UserReservationDTO>> getUserReservations(@PathVariable Long userId) {
    return ResponseEntity.ok(reservationService.getReservationsByUserId(userId));
  }

  @PostMapping("/{id}/cancel")
  public ResponseEntity<Reservation> cancelReservation(@PathVariable Long id) {
    return ResponseEntity.ok(reservationService.cancelReservation(id));
  }

  @GetMapping("/restaurant/{restaurantId}/all")
  public ResponseEntity<List<UserReservationDTO>> getRestaurantReservationsForAdmin (
          @PathVariable Long restaurantId) {
    return ResponseEntity.ok(reservationService.getRestaurantReservationsForAdmin(restaurantId));
  }
}
