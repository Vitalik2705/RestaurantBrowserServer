package com.coursework.project.controller;

import com.coursework.project.dto.RestaurantDTO;
import com.coursework.project.entity.Restaurant;
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
        Restaurant createdRestaurant = restaurantService.createRestaurant(restaurantDTO);
        return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<Restaurant>> getAllRestaurants(@RequestParam(value = "page", defaultValue = "0") int page,
                                                              @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<Restaurant> restaurants = restaurantService.getAllRestaurants(page, size);
        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        boolean deleted = restaurantService.deleteRestaurant(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        return restaurant != null ? new ResponseEntity<>(restaurant, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/photo")
    public ResponseEntity<String> uploadPhoto(@RequestParam("id") Long id, @RequestParam("file")MultipartFile file) {
        return ResponseEntity.ok().body(restaurantService.uploadPhoto(id, file));
    }


    @GetMapping(path = "/image/{filename}", produces = { IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE })
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(PHOTO_DIRECTORY + filename));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Restaurant>> searchRestaurants(
            @RequestParam(value = "term") String searchTerm,
            Pageable pageable) {
        Page<Restaurant> foundRestaurants = restaurantService.searchByNameOrFirstLetter(searchTerm, pageable);
        return new ResponseEntity<>(foundRestaurants, HttpStatus.OK);
    }

    @GetMapping("/search/multiple")
    public ResponseEntity<Page<Restaurant>> searchRestaurantsByMultipleFields(
            @RequestParam(value = "terms") List<String> searchTerms,
            Pageable pageable) {
        Page<Restaurant> foundRestaurants = restaurantService.searchByMultipleFields(searchTerms, pageable);
        return new ResponseEntity<>(foundRestaurants, HttpStatus.OK);
    }

    @PatchMapping("/updatePopularity")
    public ResponseEntity<Void> updatePopularityCount(@RequestParam Long restaurantId,
                                                      @RequestParam int newPopularityCount) {
        restaurantService.updatePopularityCount(restaurantId, newPopularityCount);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/sortedByRating")
    public ResponseEntity<Page<Restaurant>> getRestaurantsSortedByRating(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<Restaurant> restaurants = restaurantService.getAllRestaurantsSortedByRating(page, size);
        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    @GetMapping("/sortedByPopularity")
    public ResponseEntity<Page<Restaurant>> getRestaurantsSortedByPopularity(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<Restaurant> restaurants = restaurantService.getAllRestaurantsSortedByPopularity(page, size);
        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }
}
