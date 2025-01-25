package com.coursework.project.service.impl;

import com.coursework.project.dto.ReservationDTO;
import com.coursework.project.dto.TableReservationDTO;
import com.coursework.project.dto.UserReservationDTO;
import com.coursework.project.entity.*;
import com.coursework.project.repository.*;
import com.coursework.project.service.ReservationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
  private final ReservationRepository reservationRepository;
  private final DiningTableRepository diningTableRepository;
  private final RestaurantRepository restaurantRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;

  public Reservation createReservation(Long tableId, Long userId, ReservationDTO reservationDTO) {
    DiningTable table = diningTableRepository.findById(tableId).orElseThrow(() -> new RuntimeException("Table not found"));

    if (!isTableAvailable(table)) {
      throw new RuntimeException("Table is already reserved for this time");
    }

    if (reservationDTO.getGuestCount() > table.getCapacity()) {
      throw new RuntimeException("Guest count exceeds table capacity");
    }

    Reservation reservation = new Reservation();
    reservation.setDiningTable(table);
    reservation.setReservationTime(reservationDTO.getReservationTime());
    reservation.setCustomerName(reservationDTO.getCustomerName());
    reservation.setCustomerPhone(reservationDTO.getCustomerPhone());
    reservation.setGuestCount(reservationDTO.getGuestCount());
    reservation.setStatus(ReservationStatus.PENDING);

    Optional<User> userOptional = userRepository.findById(userId);
    reservation.setUser(userOptional.get());

    reservationRepository.save(reservation);

    User restaurantOwner = table.getRestaurant().getCreator();

    Message reservationMessage = new Message();
    reservationMessage.setReservation(reservation);
    reservationMessage.setSender(userOptional.get());
    reservationMessage.setReceiver(restaurantOwner);
    reservationMessage.setContent(String.format("Новий запит на бронювання від %s на %s у ресторані %s",
            reservationDTO.getCustomerName(),
            reservation.getReservationTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            reservation.getDiningTable().getRestaurant().getName()));
    reservationMessage.setMessageType(MessageType.RESERVATION_REQUEST);

    messageRepository.save(reservationMessage);

    return reservation;
  }

  private boolean isTableAvailable(DiningTable table) {
    return table.getReservations().stream().noneMatch(r -> r.getStatus() == ReservationStatus.CONFIRMED);
  }

  @Transactional
  public void deleteReservation(Long reservationId) {
    Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Reservation not found"));

    messageRepository.deleteByReservationId(reservationId);

    reservationRepository.delete(reservation);
  }

  public List<TableReservationDTO> getReservationsByRestaurant(Long restaurantId) {
    Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RuntimeException("Restaurant not found"));

    return restaurant.getDiningTables().stream().map(table -> {
      List<Reservation> tableReservations = table.getReservations().stream()
              .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED ||
                      r.getStatus() == ReservationStatus.PENDING)
              .toList();

      return new TableReservationDTO(table.getTableId(), table.getCapacity(), tableReservations);
    }).toList();
  }

  public List<UserReservationDTO> getReservationsByUserId(Long userId) {
    List<Reservation> reservations = reservationRepository.findByUserId(userId);

    return reservations.stream()
            .map(this::convertToUserReservationDTO)
            .collect(Collectors.toList());
  }

  @Override
  public Reservation cancelReservation(Long id) {
    Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reservation not found"));

    reservation.setStatus(ReservationStatus.CANCELLED);
    return reservationRepository.save(reservation);
  }

  public List<UserReservationDTO> getRestaurantReservationsForAdmin(Long restaurantId) {
    Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RuntimeException("Restaurant not found"));

    return restaurant.getDiningTables().stream()
            .flatMap(table -> table.getReservations().stream())
            .map(this::convertToUserReservationDTO)
            .collect(Collectors.toList());
  }

  private UserReservationDTO convertToUserReservationDTO(Reservation reservation) {
    UserReservationDTO dto = new UserReservationDTO();
    dto.setId(reservation.getId());
    dto.setReservationTime(reservation.getReservationTime());
    dto.setCustomerName(reservation.getCustomerName());
    dto.setCustomerPhone(reservation.getCustomerPhone());
    dto.setGuestCount(reservation.getGuestCount());
    dto.setStatus(reservation.getStatus());
    dto.setTableId(reservation.getDiningTable().getTableId());
    dto.setRestaurantId(reservation.getDiningTable().getRestaurant().getRestaurantId());
    dto.setRestaurantName(reservation.getDiningTable().getRestaurant().getName());

    return dto;
  }
}