package com.coursework.project.service.impl;

import com.coursework.project.dto.FeedbackDTO;
import com.coursework.project.entity.Feedback;
import com.coursework.project.entity.Restaurant;
import com.coursework.project.entity.User;
import com.coursework.project.logging.CustomLogger;
import com.coursework.project.repository.FeedbackRepository;
import com.coursework.project.repository.RestaurantRepository;
import com.coursework.project.repository.UserRepository;
import com.coursework.project.service.FeedbackService;
import com.coursework.project.exception.RestaurantNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Feedback createFeedback(FeedbackDTO feedbackDTO, Long restaurantId, Long userId) {
        try {
            Feedback feedback = new Feedback();
            feedback.setRating(feedbackDTO.getRating());
            feedback.setDescription(feedbackDTO.getDescription());
            feedback.setAdvantages(feedbackDTO.getAdvantages());
            feedback.setDisadvantages(feedbackDTO.getDisadvantages());
            feedback.setDate(feedbackDTO.getDate());

            Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);
            Optional<User> optionalUser = userRepository.findById(userId);

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                feedback.setUser(user);
            } else {
                CustomLogger.logError("FeedbackServiceImpl User with ID " + userId + " not found");
                throw new RuntimeException("User with ID " + userId + " not found");
            }

            if (optionalRestaurant.isPresent()) {
                Restaurant restaurant = optionalRestaurant.get();
                feedback.setRestaurant(restaurant);
                feedbackRepository.save(feedback);

                calculateAndSaveRestaurantRating(restaurant);

                CustomLogger.logInfo("FeedbackServiceImpl Feedback created successfully");
                return feedback;
            } else {
                CustomLogger.logError("FeedbackServiceImpl Restaurant with ID " + restaurantId + " not found");
                throw new RestaurantNotFoundException("Restaurant with ID " + restaurantId + " not found");
            }
        } catch (Exception e) {
            CustomLogger.logError("Error creating feedback: " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        try {
            List<Feedback> feedbacks = feedbackRepository.findAll();
            CustomLogger.logInfo("FeedbackServiceImpl Retrieved all feedbacks successfully");
            return feedbacks;
        } catch (Exception e) {
            CustomLogger.logError("FeedbackServiceImpl Error getting all feedbacks: " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }

    @Override
    public boolean deleteFeedback(Long id) {
        try {
            Optional<Feedback> optionalFeedback = feedbackRepository.findById(id);

            if (optionalFeedback.isPresent()) {
                Feedback feedback = optionalFeedback.get();
                feedbackRepository.deleteById(id);
                Restaurant restaurant = feedback.getRestaurant();

                calculateAndSaveRestaurantRating(restaurant);

                CustomLogger.logInfo("FeedbackServiceImpl Feedback deleted successfully");
                return true;
            }
            return false;
        } catch (Exception e) {
            CustomLogger.logError("FeedbackServiceImpl Error deleting feedback with ID " + id + ": " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }

    @Override
    public Feedback getFeedbackById(Long id) {
        try {
            CustomLogger.logInfo("FeedbackServiceImpl Retrieving feedback by ID: " + id);
            Optional<Feedback> optionalFeedback = feedbackRepository.findById(id);
            return optionalFeedback.orElse(null);
        } catch (Exception e) {
            CustomLogger.logError("FeedbackServiceImpl Error getting feedback with ID " + id + ": " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }

    private void calculateAndSaveRestaurantRating(Restaurant restaurant) {
        try {
            List<Feedback> feedbackList = restaurant.getFeedbackList();
            if (feedbackList != null && !feedbackList.isEmpty()) {
                double averageRating = feedbackList.stream()
                        .mapToDouble(Feedback::getRating)
                        .average()
                        .orElse(0.0);

                restaurant.setRating(averageRating);
                restaurantRepository.save(restaurant);
            }
            CustomLogger.logInfo("FeedbackServiceImpl Restaurant rating calculated and saved successfully");
        } catch (Exception e) {
            CustomLogger.logError("FeedbackServiceImpl Error calculating and saving restaurant rating: " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }
}

