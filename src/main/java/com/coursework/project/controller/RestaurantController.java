package com.coursework.project.controller;

import com.coursework.project.dto.RestaurantDTO;
import com.coursework.project.entity.Restaurant;
import com.coursework.project.logging.CustomLogger;
import com.coursework.project.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.coursework.project.constant.Constant.PHOTO_DIRECTORY;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;


@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody @Valid RestaurantDTO restaurantDTO) {
        try {
            CustomLogger.logInfo("RestaurantController Creating a new restaurant: " + restaurantDTO.getName());
            Restaurant createdRestaurant = restaurantService.createRestaurant(restaurantDTO);
            return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
        } catch (Exception e) {
            CustomLogger.logError("RestaurantController Error creating restaurant: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<Page<Restaurant>> getAllRestaurants(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            CustomLogger.logInfo("RestaurantController Getting all restaurants");
            Page<Restaurant> restaurants = restaurantService.getAllRestaurants(page, size);
            return new ResponseEntity<>(restaurants, HttpStatus.OK);
        } catch (Exception e) {
            CustomLogger.logError("RestaurantController Error getting all restaurants: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        try {
            CustomLogger.logInfo("RestaurantController Deleting restaurant with ID: " + id);
            boolean deleted = restaurantService.deleteRestaurant(id);
            return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            CustomLogger.logError("RestaurantController Error deleting restaurant with ID " + id + ": " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        try {
            CustomLogger.logInfo("RestaurantController Getting restaurant by ID: " + id);
            Restaurant restaurant = restaurantService.getRestaurantById(id);
            return restaurant != null ? new ResponseEntity<>(restaurant, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            CustomLogger.logError("RestaurantController Error getting restaurant with ID " + id + ": " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/photo")
    public ResponseEntity<String> uploadPhoto(@RequestParam("id") Long id, @RequestParam("file") MultipartFile file) {
        try {
            CustomLogger.logInfo("RestaurantController Uploading photo for restaurant with ID: " + id);
            return ResponseEntity.ok().body(restaurantService.uploadPhoto(id, file));
        } catch (Exception e) {
            CustomLogger.logError("RestaurantController Error uploading photo for restaurant with ID " + id + ": " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/image/{filename}", produces = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE})
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        try {
            CustomLogger.logInfo("RestaurantController Getting photo with filename: " + filename);
            return Files.readAllBytes(Paths.get(PHOTO_DIRECTORY + filename));
        } catch (Exception e) {
            CustomLogger.logError("RestaurantController Error getting photo with filename " + filename + ": " + e.getMessage());
            throw e; // Rethrow the exception for Spring to handle
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Restaurant>> searchRestaurants(
            @RequestParam(value = "term") String searchTerm,
            Pageable pageable) {
        try {
            CustomLogger.logInfo("RestaurantController Searching for restaurants with term: " + searchTerm);
            Page<Restaurant> foundRestaurants = restaurantService.searchByNameOrFirstLetter(searchTerm, pageable);
            return new ResponseEntity<>(foundRestaurants, HttpStatus.OK);
        } catch (Exception e) {
            CustomLogger.logError("RestaurantController Error searching for restaurants with term " + searchTerm + ": " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search/multiple")
    public ResponseEntity<Page<Restaurant>> searchRestaurantsByMultipleFields(
            @RequestParam(value = "terms") List<String> searchTerms,
            Pageable pageable) {
        try {
            CustomLogger.logInfo("RestaurantController Searching for restaurants with terms: " + searchTerms);
            Page<Restaurant> foundRestaurants = restaurantService.searchByMultipleFields(searchTerms, pageable);
            return new ResponseEntity<>(foundRestaurants, HttpStatus.OK);
        } catch (Exception e) {
            CustomLogger.logError("RestaurantController Error searching for restaurants with terms " + searchTerms + ": " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/updatePopularity")
    public ResponseEntity<Void> updatePopularityCount(@RequestParam Long restaurantId,
                                                      @RequestParam int newPopularityCount) {
        try {
            CustomLogger.logInfo("RestaurantController Updating popularity count for restaurant with ID: " + restaurantId);
            restaurantService.updatePopularityCount(restaurantId, newPopularityCount);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            CustomLogger.logError("RestaurantController Error updating popularity count for restaurant with ID " + restaurantId + ": " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sortedByRating")
    public ResponseEntity<Page<Restaurant>> getRestaurantsSortedByRating(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            CustomLogger.logInfo("RestaurantController Getting restaurants sorted by rating");
            Page<Restaurant> restaurants = restaurantService.getAllRestaurantsSortedByRating(page, size);
            return new ResponseEntity<>(restaurants, HttpStatus.OK);
        } catch (Exception e) {
            CustomLogger.logError("RestaurantController Error getting restaurants sorted by rating: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sortedByPopularity")
    public ResponseEntity<Page<Restaurant>> getRestaurantsSortedByPopularity(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            CustomLogger.logInfo("RestaurantController Getting restaurants sorted by popularity");
            Page<Restaurant> restaurants = restaurantService.getAllRestaurantsSortedByPopularity(page, size);
            return new ResponseEntity<>(restaurants, HttpStatus.OK);
        } catch (Exception e) {
            CustomLogger.logError("RestaurantController Error getting restaurants sorted by popularity: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

