package com.coursework.project.service.impl;

import com.coursework.project.dto.FeedbackDTO;
import com.coursework.project.entity.Feedback;
import com.coursework.project.entity.Restaurant;
import com.coursework.project.entity.User;
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
            throw new RuntimeException("User with ID " + userId + " not found");
        }

        if (optionalRestaurant.isPresent()) {
            Restaurant restaurant = optionalRestaurant.get();
            feedback.setRestaurant(restaurant);
            feedbackRepository.save(feedback);

            calculateAndSaveRestaurantRating(restaurant);

            return feedback;
        } else {
            throw new RestaurantNotFoundException("Restaurant with ID " + restaurantId + " not found");
        }
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    @Override
    public boolean deleteFeedback(Long id) {
        if (feedbackRepository.existsById(id)) {
            feedbackRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Feedback getFeedbackById(Long id) {
        Optional<Feedback> optionalFeedback = feedbackRepository.findById(id);
        return optionalFeedback.orElse(null);
    }

    private void calculateAndSaveRestaurantRating(Restaurant restaurant) {
        List<Feedback> feedbackList = restaurant.getFeedbackList();
        if (feedbackList != null && !feedbackList.isEmpty()) {
            double averageRating = feedbackList.stream()
                    .mapToDouble(Feedback::getRating)
                    .average()
                    .orElse(0.0);

            restaurant.setRating(averageRating);
            restaurantRepository.save(restaurant);
        }
    }
}

