package com.coursework.project.service;

import com.coursework.project.entity.User;
import com.coursework.project.entity.UserPreferences;

public interface UserPreferencesService {
  UserPreferences getPreferencesByUser(Long userId);
  UserPreferences savePreferences(UserPreferences preferences, Long userId);
  boolean hasUserSetPreferences(Long userId);
}
