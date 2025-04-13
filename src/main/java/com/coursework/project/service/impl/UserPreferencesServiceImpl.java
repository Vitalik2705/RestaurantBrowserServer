package com.coursework.project.service.impl;

import com.coursework.project.entity.UserPreferences;
import com.coursework.project.repository.UserPreferencesRepository;
import com.coursework.project.repository.UserRepository;
import com.coursework.project.service.UserPreferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPreferencesServiceImpl implements UserPreferencesService {

  private final UserPreferencesRepository preferencesRepository;
  private final UserRepository userRepository;

  @Transactional
  public UserPreferences savePreferences(UserPreferences preferences, Long userId) {
    userRepository.findById(userId).ifPresent(preferences::setUser);
    return preferencesRepository.save(preferences);
  }

  @Transactional(readOnly = true)
  public UserPreferences getPreferencesByUser(Long userId) {
    return preferencesRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Preferences not found for user"));
  }

  @Transactional(readOnly = true)
  public boolean hasUserSetPreferences(Long userId) {
    return preferencesRepository.existsByUserId(userId);
  }
}