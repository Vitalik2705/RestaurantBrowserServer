package com.coursework.project.repository;

import com.coursework.project.entity.DiningTable;
import com.coursework.project.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {
  void deleteByRestaurant(Restaurant restaurant);
}
