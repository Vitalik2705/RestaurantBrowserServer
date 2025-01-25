package com.coursework.project.dto;

import com.coursework.project.entity.MessageType;
import lombok.Data;

@Data
public class MessageDTO {
  private Long id;
  private Long reservationId;
  private Long senderId;
  private Long receiverId;
  private String content;
  private String createdAt;
  private MessageType messageType;
  private boolean isRead;
  private ReservationDTO reservation;
}
