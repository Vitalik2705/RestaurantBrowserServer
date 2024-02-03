package com.coursework.project.controller;

import com.coursework.project.dto.AuthResponseDTO;
import com.coursework.project.dto.JwtAuthResponse;
import com.coursework.project.dto.LoginDTO;
import com.coursework.project.dto.RegisterDTO;
import com.coursework.project.entity.Restaurant;
import com.coursework.project.entity.User;
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
        return ResponseEntity.ok(authService.register(registerDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDto){
        return ResponseEntity.ok(authService.login(loginDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = authService.getUserById(id);
        return user != null ? new ResponseEntity<>(user, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{userId}/add-favorite-restaurant/{restaurantId}")
    public ResponseEntity<?> addFavoriteRestaurant(
            @PathVariable Long userId,
            @PathVariable Long restaurantId
    ) {
        authService.addFavoriteRestaurant(userId, restaurantId);
        return ResponseEntity.status(HttpStatus.OK).body("Restaurant added to favorites successfully.");
    }

    @GetMapping("/{userId}/favorite-restaurants")
    public ResponseEntity<Page<Restaurant>> getFavoriteRestaurants(
            @PathVariable Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        User user = authService.getUserById(userId);

        if (user != null) {
            List<Restaurant> favoriteRestaurants = user.getRestaurants();

            int start = page * size;
            int end = Math.min((page + 1) * size, favoriteRestaurants.size());

            List<Restaurant> pageContent = favoriteRestaurants.subList(start, end);

            Pageable pageable = PageRequest.of(page, size);
            Page<Restaurant> favoriteRestaurantsPage = new PageImpl<>(pageContent, pageable, favoriteRestaurants.size());
            return new ResponseEntity<>(favoriteRestaurantsPage, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{userId}/remove-favorite-restaurant/{restaurantId}")
    public ResponseEntity<?> removeFavoriteRestaurant(
            @PathVariable Long userId,
            @PathVariable Long restaurantId
    ) {
        authService.removeFavoriteRestaurant(userId, restaurantId);
        return ResponseEntity.status(HttpStatus.OK).body("Restaurant removed from favorites successfully.");
    }
}
