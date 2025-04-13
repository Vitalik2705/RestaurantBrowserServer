package com.coursework.project.repository;

import com.coursework.project.entity.User;
import com.coursework.project.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
  Optional<UserPreferences> findByUserId(Long userId);
  boolean existsByUserId(Long userId);
}
