package com.coursework.project.repository;

import com.coursework.project.entity.Restaurant;
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

  @Query("SELECT r FROM Restaurant r WHERE " + "(:#{#cities.isEmpty()} = true OR LOWER(r.address.city) IN :cities) AND "
          + "(:#{#cuisines.isEmpty()} = true OR LOWER(r.cuisineType) IN :cuisines) AND "
          + "(:#{#ratings.isEmpty()} = true OR FUNCTION('ROUND', r.rating) IN :ratings) AND "
          + "(:#{#priceCategories.isEmpty()} = true OR r.priceCategory IN :priceCategories)")
  Page<Restaurant> searchByMultipleFields(@Param("cities") List<String> cities, @Param("cuisines") List<String> cuisines,
          @Param("ratings") List<String> ratings, @Param("priceCategories") List<String> priceCategories, Pageable pageable);

  @Modifying
  @Query("UPDATE Restaurant r SET r.popularityCount = :newPopularityCount WHERE r.restaurantId = :restaurantId")
  void updatePopularityCountById(@Param("restaurantId") Long restaurantId, @Param("newPopularityCount") int newPopularityCount);

  List<Restaurant> findByCreatorId(Long creatorId);

}

