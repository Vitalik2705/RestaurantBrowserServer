package com.coursework.project.service;

import com.coursework.project.dto.FeedbackDTO;
import com.coursework.project.entity.Feedback;
import java.util.List;

public interface FeedbackService {
    Feedback createFeedback(FeedbackDTO feedbackDTO, Long restaurantId, Long userId);

    List<Feedback> getAllFeedbacks();

    boolean deleteFeedback(Long id);

    Feedback getFeedbackById(Long id);
}
