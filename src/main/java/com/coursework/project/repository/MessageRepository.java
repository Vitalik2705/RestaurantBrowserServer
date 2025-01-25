package com.coursework.project.repository;

import com.coursework.project.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
  List<Message> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
  void deleteByReservationId(Long reservationId);
  List<Message> findByReceiverIdAndIsReadFalse(Long receiverId);
}
