package com.coursework.project.service.impl;

import com.coursework.project.dto.AuthResponseDTO;
import com.coursework.project.dto.LoginDTO;
import com.coursework.project.dto.RegisterDTO;
import com.coursework.project.entity.Restaurant;
import com.coursework.project.entity.User;
import com.coursework.project.repository.RestaurantRepository;
import com.coursework.project.repository.UserRepository;
import com.coursework.project.security.JwtTokenProvider;
import com.coursework.project.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RestaurantRepository restaurantRepository;

    @Override
    public AuthResponseDTO login(LoginDTO loginDto) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),
                loginDto.getPassword()
        ));

        var user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(user);

        // Return AuthResponseDTO with the token
        return AuthResponseDTO.builder()
                .token(token)
                .userId(user.getId())
                .build();
    }

    @Override
    public AuthResponseDTO register(RegisterDTO registerDto) {
        // Check if the user already exists
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        // Map RegisterDTO to User entity
        User user = mapRegisterDtoToUser(registerDto);

        // Encode and set the password
        String encodedPassword = passwordEncoder.encode(registerDto.getPassword());
        user.setPassword(encodedPassword);

        // Save the user
        userRepository.save(user);

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user);

        // Return AuthResponseDTO with the token
        return AuthResponseDTO.builder()
                .token(token)
                .userId(user.getId())
                .build();
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> optionalRestaurant = userRepository.findById(id);
        return optionalRestaurant.orElse(null);
    }

    @Override
    public void addFavoriteRestaurant(Long userId, Long restaurantId) {
        User user = getUserById(userId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        List<Restaurant> favoriteRestaurants = user.getRestaurants();
        favoriteRestaurants.add(restaurant);
        user.setRestaurants(favoriteRestaurants);

        userRepository.save(user);
    }

    @Override
    public void removeFavoriteRestaurant(Long userId, Long restaurantId) {
        User user = getUserById(userId);
        if (user != null) {
            List<Restaurant> favoriteRestaurants = user.getRestaurants();
            favoriteRestaurants.removeIf(restaurant -> restaurant.getRestaurantId().equals(restaurantId));
            user.setRestaurants(favoriteRestaurants);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private User mapRegisterDtoToUser(RegisterDTO registerDto) {
        return User.builder()
                .name(registerDto.getName())
                .surname(registerDto.getSurname())
                .email(registerDto.getEmail())
                .password(registerDto.getPassword())
                .roles(registerDto.getRoles())
                .build();
    }
}