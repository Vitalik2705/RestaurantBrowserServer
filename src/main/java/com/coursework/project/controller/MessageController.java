package com.coursework.project.controller;


import com.coursework.project.dto.MessageDTO;
import com.coursework.project.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
  private final MessageService messageService;

  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<MessageDTO>> getUserMessages(@PathVariable Long userId) {
    return ResponseEntity.ok(messageService.getUserMessages(userId));
  }

  @PostMapping("/reservations/{reservationId}/confirm")
  public ResponseEntity<MessageDTO> confirmReservation(
          @PathVariable Long reservationId,
          @RequestParam Long userId) {
    return ResponseEntity.ok(messageService.confirmReservation(reservationId, userId));
  }

  @PostMapping("/reservations/{reservationId}/cancel")
  public ResponseEntity<MessageDTO> cancelReservation(
          @PathVariable Long reservationId,
          @RequestParam Long userId) {
    return ResponseEntity.ok(messageService.cancelReservation(reservationId, userId));
  }

  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
    messageService.deleteMessage(messageId);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{messageId}/read")
  public ResponseEntity<MessageDTO> markMessageAsRead(@PathVariable Long messageId) {
    return ResponseEntity.ok(messageService.markMessageAsRead(messageId));
  }

  @PutMapping("/user/{userId}/read-all")
  public ResponseEntity<List<MessageDTO>> markAllMessagesAsRead(@PathVariable Long userId) {
    return ResponseEntity.ok(messageService.markAllMessagesAsRead(userId));
  }
}
