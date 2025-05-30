package com.coursework.project.repository;

import com.coursework.project.entity.Restaurant;
import com.coursework.project.entity.WorkHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkHoursRepository extends JpaRepository<WorkHours, Long> {
  void deleteByRestaurant(Restaurant restaurant);
}
