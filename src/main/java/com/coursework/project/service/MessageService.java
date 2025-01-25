package com.coursework.project.service;

import com.coursework.project.dto.MessageDTO;

import java.util.List;

public interface MessageService {
  List<MessageDTO> getUserMessages(Long userId);

  MessageDTO confirmReservation(Long reservationId, Long userId);

  MessageDTO cancelReservation(Long reservationId, Long userId);

  void deleteMessage(Long messageId);

  MessageDTO markMessageAsRead(Long messageId);

  List<MessageDTO> markAllMessagesAsRead(Long userId);
}
