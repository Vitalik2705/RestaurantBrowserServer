package com.coursework.project.controller;

import com.coursework.project.dto.AuthResponseDTO;
import com.coursework.project.dto.LoginDTO;
import com.coursework.project.dto.RegisterDTO;
import com.coursework.project.entity.Restaurant;
import com.coursework.project.entity.User;
import com.coursework.project.logging.CustomLogger;
import com.coursework.project.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterDTO registerDTO) {
        try {
            CustomLogger.logInfo("AuthController Registering user with email: " + registerDTO.getEmail());
            AuthResponseDTO response = authService.register(registerDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CustomLogger.logError("AuthController Error registering user: " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDto){
        try {
            CustomLogger.logInfo("AuthController Logging in user with email: " + loginDto.getEmail());
            AuthResponseDTO response = authService.login(loginDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CustomLogger.logError("AuthController Error logging in user: " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            CustomLogger.logInfo("AuthController Retrieving user by ID: " + id);
            User user = authService.getUserById(id);
            return user != null ? new ResponseEntity<>(user, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            CustomLogger.logError("AuthController Error getting user by ID " + id + ": " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }

    @PostMapping("/{userId}/add-favorite-restaurant/{restaurantId}")
    public ResponseEntity<?> addFavoriteRestaurant(
            @PathVariable Long userId,
            @PathVariable Long restaurantId
    ) {
        try {
            CustomLogger.logInfo("Adding restaurant with ID " + restaurantId + " to favorites for user with ID " + userId);
            authService.addFavoriteRestaurant(userId, restaurantId);
            return ResponseEntity.status(HttpStatus.OK).body("Restaurant added to favorites successfully.");
        } catch (Exception e) {
            CustomLogger.logError("AuthController Error adding restaurant to favorites: " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }

    @GetMapping("/{userId}/favorite-restaurants")
    public ResponseEntity<Page<Restaurant>> getFavoriteRestaurants(
            @PathVariable Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            CustomLogger.logInfo("AuthController Retrieving favorite restaurants for user with ID " + userId);
            User user = authService.getUserById(userId);

            if (user != null) {
                List<Restaurant> favoriteRestaurants = user.getRestaurants();

                int start = page * size;
                int end = Math.min((page + 1) * size, favoriteRestaurants.size());

                List<Restaurant> pageContent = favoriteRestaurants.subList(start, end);

                Pageable pageable = PageRequest.of(page, size);
                Page<Restaurant> favoriteRestaurantsPage = new PageImpl<>(pageContent, pageable, favoriteRestaurants.size());
                CustomLogger.logInfo("AuthController Retrieved favorite restaurants successfully");
                return new ResponseEntity<>(favoriteRestaurantsPage, HttpStatus.OK);
            } else {
                CustomLogger.logError("AuthController User with ID " + userId + " not found");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            CustomLogger.logError("AuthController Error getting favorite restaurants: " + e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{userId}/remove-favorite-restaurant/{restaurantId}")
    public ResponseEntity<?> removeFavoriteRestaurant(
            @PathVariable Long userId,
            @PathVariable Long restaurantId
    ) {
        try {
            CustomLogger.logInfo("AuthController Removing restaurant with ID " + restaurantId + " from favorites for user with ID " + userId);
            authService.removeFavoriteRestaurant(userId, restaurantId);
            return ResponseEntity.status(HttpStatus.OK).body("Restaurant removed from favorites successfully.");
        } catch (Exception e) {
            CustomLogger.logError("AuthController Error removing restaurant from favorites: " + e.getMessage());
            throw e;
        }
    }
}