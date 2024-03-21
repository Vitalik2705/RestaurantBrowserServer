package com.coursework.project.service;

import com.coursework.project.dto.AuthResponseDTO;
import com.coursework.project.dto.LoginDTO;
import com.coursework.project.dto.RegisterDTO;
import com.coursework.project.entity.Role;
import com.coursework.project.entity.User;

public interface AuthService {
    AuthResponseDTO login(LoginDTO loginDto);
    AuthResponseDTO register(RegisterDTO registerDto);
    User getUserById(Long id);
    void addFavoriteRestaurant(Long userId, Long restaurantId);
    void removeFavoriteRestaurant(Long userId, Long restaurantId);
    void addRoleToUser(Long userId, Role role);
}

