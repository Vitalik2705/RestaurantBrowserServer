package com.coursework.project.service.impl;

import com.coursework.project.dto.AuthResponseDTO;
import com.coursework.project.dto.LoginDTO;
import com.coursework.project.dto.RegisterDTO;
import com.coursework.project.entity.Restaurant;
import com.coursework.project.entity.Role;
import com.coursework.project.entity.User;
import com.coursework.project.repository.RestaurantRepository;
import com.coursework.project.repository.UserRepository;
import com.coursework.project.security.JwtTokenProvider;
import com.coursework.project.logging.CustomLogger;
import com.coursework.project.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getEmail(),
                    loginDto.getPassword()
            ));

            var user = userRepository.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenProvider.generateToken(user);

            CustomLogger.logInfo("AuthServiceImpl User logged in successfully");
            // Return AuthResponseDTO with the token
            return AuthResponseDTO.builder()
                    .token(token)
                    .userId(user.getId())
                    .build();
        } catch (Exception e) {
            CustomLogger.logError("AuthServiceImpl Error during login: " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }

    @Override
    public AuthResponseDTO register(RegisterDTO registerDto) {
        try {
            if (userRepository.existsByEmail(registerDto.getEmail())) {
                CustomLogger.logError("AuthServiceImpl Email already in use");
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

            CustomLogger.logInfo("AuthServiceImpl User registered successfully");
            // Return AuthResponseDTO with the token
            return AuthResponseDTO.builder()
                    .token(token)
                    .userId(user.getId())
                    .build();
        } catch (Exception e) {
            CustomLogger.logError("AuthServiceImpl Error during registration: " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }

    @Override
    public User getUserById(Long id) {
        try {
            CustomLogger.logInfo("AuthServiceImpl Retrieved user by ID: " + id);
            Optional<User> optionalUser = userRepository.findById(id);
            return optionalUser.orElse(null);
        } catch (Exception e) {
            CustomLogger.logError("AuthServiceImpl Error getting user by ID " + id + ": " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }

    @Override
    public void addFavoriteRestaurant(Long userId, Long restaurantId) {
        try {
            User user = getUserById(userId);
            Restaurant restaurant = restaurantRepository.findById(restaurantId)
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));

            List<Restaurant> favoriteRestaurants = user.getRestaurants();
            favoriteRestaurants.add(restaurant);
            user.setRestaurants(favoriteRestaurants);

            userRepository.save(user);
            CustomLogger.logInfo("AuthServiceImpl Restaurant added to favorites successfully");
        } catch (Exception e) {
            CustomLogger.logError("AuthServiceImpl Error adding restaurant to favorites: " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }

    @Override
    public void removeFavoriteRestaurant(Long userId, Long restaurantId) {
        try {
            User user = getUserById(userId);
            if (user != null) {
                List<Restaurant> favoriteRestaurants = user.getRestaurants();
                favoriteRestaurants.removeIf(restaurant -> restaurant.getRestaurantId().equals(restaurantId));
                user.setRestaurants(favoriteRestaurants);
                userRepository.save(user);
                CustomLogger.logInfo("AuthServiceImpl Restaurant removed from favorites successfully");
            } else {
                CustomLogger.logError("AuthServiceImpl User not found");
                throw new RuntimeException("User not found");
            }
        } catch (Exception e) {
            CustomLogger.logError("AuthServiceImpl Error removing restaurant from favorites: " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }

    @Override
    public void addRoleToUser(Long userId, Role role) {
        try {
            User user = getUserById(userId);
            if (user != null) {
                Set<Role> roles = user.getRoles();
                if (!roles.contains(role)) {
                    roles.add(role);
                    user.setRoles(roles);
                    userRepository.save(user);
                    CustomLogger.logInfo("AuthServiceImpl Role added to user successfully");
                } else {
                    CustomLogger.logError("AuthServiceImpl User already has the role");
                    throw new RuntimeException("User already has the role");
                }
            } else {
                CustomLogger.logError("AuthServiceImpl User not found");
                throw new RuntimeException("User not found");
            }
        } catch (Exception e) {
            CustomLogger.logError("AuthServiceImpl Error adding role to user: " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
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
