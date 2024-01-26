package com.coursework.project.service;

import com.coursework.project.dto.RestaurantDTO;
import com.coursework.project.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface RestaurantService {
    Restaurant createRestaurant(RestaurantDTO restaurantDTO);

    Page<Restaurant> getAllRestaurants(int page, int size);

    boolean deleteRestaurant(Long id);

    Restaurant getRestaurantById(Long id);

    String uploadPhoto(Long id, MultipartFile file);

    Page<Restaurant> searchByNameOrFirstLetter(String searchTerm, Pageable pageable);

    Page<Restaurant> searchByMultipleFields(List<String> searchTerms, Pageable pageable);

    void updatePopularityCount(Long restaurantId, int newPopularityCount);

    Page<Restaurant> getAllRestaurantsSortedByRating(int page, int size);

    Page<Restaurant> getAllRestaurantsSortedByPopularity(int page, int size);
}
