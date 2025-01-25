package com.coursework.project.service.impl;

import com.coursework.project.dto.MessageDTO;
import com.coursework.project.dto.ReservationDTO;
import com.coursework.project.entity.*;
import com.coursework.project.repository.MessageRepository;
import com.coursework.project.repository.ReservationRepository;
import com.coursework.project.repository.UserRepository;
import com.coursework.project.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {
  private final MessageRepository messageRepository;
  private final ReservationRepository reservationRepository;
  private final UserRepository userRepository;

  public MessageServiceImpl(MessageRepository messageRepository,
          ReservationRepository reservationRepository,
          UserRepository userRepository) {
    this.messageRepository = messageRepository;
    this.reservationRepository = reservationRepository;
    this.userRepository = userRepository;
  }

  public List<MessageDTO> getUserMessages(Long userId) {
    return messageRepository.findByReceiverIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
  }

  public MessageDTO confirmReservation(Long reservationId, Long userId) {
    Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Reservation not found"));

    User restaurantOwner = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (!reservation.getDiningTable().getRestaurant().getCreator().getId().equals(userId)) {
      throw new RuntimeException("Unauthorized to confirm this reservation");
    }

    reservation.setStatus(ReservationStatus.CONFIRMED);
    reservationRepository.save(reservation);

    Message confirmationMessage = new Message();
    confirmationMessage.setReservation(reservation);
    confirmationMessage.setSender(restaurantOwner);
    confirmationMessage.setReceiver(reservation.getUser());
    Restaurant restaurant = reservation.getDiningTable().getRestaurant();
    confirmationMessage.setContent(String.format(
            "Ваше бронювання підтверджено! Чекаємо на вас %s у ресторані %s, %s",
            reservation.getReservationTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            restaurant.getName(),
            restaurant.getAddress().getFormattedAddress()
    ));
    confirmationMessage.setMessageType(MessageType.RESERVATION_CONFIRMATION);

    return convertToDTO(messageRepository.save(confirmationMessage));
  }

  public MessageDTO cancelReservation(Long reservationId, Long userId) {
    Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Reservation not found"));

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    boolean isAuthorized = reservation.getDiningTable().getRestaurant().getCreator().getId().equals(userId) ||
            reservation.getUser().getId().equals(userId);

    if (!isAuthorized) {
      throw new RuntimeException("Unauthorized to cancel this reservation");
    }

    reservation.setStatus(ReservationStatus.CANCELLED);
    reservationRepository.save(reservation);

    Message cancellationMessage = new Message();
    cancellationMessage.setReservation(reservation);
    cancellationMessage.setSender(user);
    cancellationMessage.setReceiver(reservation.getUser().getId().equals(userId) ?
            reservation.getDiningTable().getRestaurant().getCreator() : reservation.getUser());
    cancellationMessage.setContent("The reservation has been cancelled.");
    cancellationMessage.setMessageType(MessageType.RESERVATION_CANCELLATION);

    return convertToDTO(messageRepository.save(cancellationMessage));
  }

  @Override
  public void deleteMessage(Long messageId) {
    Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message not found"));
    messageRepository.delete(message);
  }

  @Override
  public MessageDTO markMessageAsRead(Long messageId) {
    Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message not found"));

    message.setRead(true);
    message = messageRepository.save(message);

    return convertToDTO(message);
  }

  @Override
  public List<MessageDTO> markAllMessagesAsRead(Long userId) {
    List<Message> unreadMessages = messageRepository.findByReceiverIdAndIsReadFalse(userId);

    unreadMessages.forEach(message -> message.setRead(true));
    List<Message> updatedMessages = messageRepository.saveAll(unreadMessages);

    return updatedMessages.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
  }

  private MessageDTO convertToDTO(Message message) {
    MessageDTO dto = new MessageDTO();
    dto.setId(message.getId());
    dto.setReservationId(message.getReservation().getId());
    dto.setSenderId(message.getSender().getId());
    dto.setReceiverId(message.getReceiver().getId());
    dto.setContent(message.getContent());
    dto.setCreatedAt(message.getCreatedAt().toString());
    dto.setMessageType(message.getMessageType());
    dto.setRead(message.isRead());
    dto.setReservation(new ReservationDTO(message.getReservation()));
    return dto;
  }
}