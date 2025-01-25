package com.coursework.project.service;

import com.coursework.project.dto.ReservationDTO;
import com.coursework.project.dto.TableReservationDTO;
import com.coursework.project.dto.UserReservationDTO;
import com.coursework.project.entity.Reservation;

import java.util.List;

public interface ReservationService {

  Reservation createReservation(Long tableId, Long userId, ReservationDTO reservationDTO);
  void deleteReservation(Long reservationId);
  List<TableReservationDTO> getReservationsByRestaurant(Long restaurantId);
  List<UserReservationDTO> getReservationsByUserId(Long userId);
  Reservation cancelReservation(Long id);
  List<UserReservationDTO> getRestaurantReservationsForAdmin(Long restaurantId);
  }