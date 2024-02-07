package com.coursework.project.controller;

import com.coursework.project.dto.FeedbackDTO;
import com.coursework.project.entity.Feedback;
import com.coursework.project.logging.CustomLogger;
import com.coursework.project.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/{restaurantId}/{userId}")
    public ResponseEntity<Feedback> createFeedback(@RequestBody @Valid FeedbackDTO feedbackDTO,
                                                   @PathVariable Long restaurantId,
                                                   @PathVariable Long userId) {
        try {
            CustomLogger.logInfo("FeedbackController Creating feedback for restaurant ID " + restaurantId + " and user ID " + userId);
            Feedback createdFeedback = feedbackService.createFeedback(feedbackDTO, restaurantId, userId);
            return new ResponseEntity<>(createdFeedback, HttpStatus.CREATED);
        } catch (Exception e) {
            CustomLogger.logError("FeedbackController Error creating feedback: " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedbacks() {
        try {
            CustomLogger.logInfo("FeedbackController Retrieving all feedbacks");
            List<Feedback> feedbacks = feedbackService.getAllFeedbacks();
            return new ResponseEntity<>(feedbacks, HttpStatus.OK);
        } catch (Exception e) {
            CustomLogger.logError("FeedbackController Error getting all feedbacks: " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        try {
            CustomLogger.logInfo("FeedbackController Deleting feedback with ID: " + id);
            boolean deleted = feedbackService.deleteFeedback(id);
            return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            CustomLogger.logError("FeedbackController Error deleting feedback with ID " + id + ": " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable Long id) {
        try {
            CustomLogger.logInfo("FeedbackController Retrieving feedback by ID: " + id);
            Feedback feedback = feedbackService.getFeedbackById(id);
            return feedback != null ? new ResponseEntity<>(feedback, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            CustomLogger.logError("FeedbackController Error getting feedback with ID " + id + ": " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }
}
