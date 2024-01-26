package com.coursework.project.repository;

import com.coursework.project.entity.DiningTable;
import com.coursework.project.entity.Feedback;
import com.coursework.project.entity.Restaurant;
import com.coursework.project.entity.WorkHours;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.name) LIKE LOWER(concat('%', :searchTerm, '%'))")
    Page<Restaurant> findByNameOrFirstLetter(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE " +
            "(:cities IS NULL OR LOWER(r.city) IN :cities) AND " +
            "(:cuisines IS NULL OR LOWER(r.cuisineType) IN :cuisines) AND " +
            "(:ratings IS NULL OR FUNCTION('ROUND', r.rating) IN :ratings)")
    Page<Restaurant> searchByMultipleFields(
            @Param("cities") List<String> cities,
            @Param("cuisines") List<String> cuisines,
            @Param("ratings") List<String> ratings,
            Pageable pageable);

    @Modifying
    @Query("UPDATE Restaurant r SET r.popularityCount = :newPopularityCount WHERE r.restaurantId = :restaurantId")
    void updatePopularityCountById(@Param("restaurantId") Long restaurantId, @Param("newPopularityCount") int newPopularityCount);


}

