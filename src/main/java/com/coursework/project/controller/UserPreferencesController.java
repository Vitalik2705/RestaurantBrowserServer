package com.coursework.project.controller;

import com.coursework.project.entity.UserPreferences;
import com.coursework.project.service.UserPreferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/preferences")
@RequiredArgsConstructor
public class UserPreferencesController {

  private final UserPreferencesService preferencesService;

  @PostMapping("/{userId}")
  public ResponseEntity<UserPreferences> savePreferences(
          @RequestBody UserPreferences preferences,
          @PathVariable Long userId) {
    UserPreferences savedPreferences = preferencesService.savePreferences(preferences, userId);
    return ResponseEntity.ok(savedPreferences);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserPreferences> getPreferences( @PathVariable Long userId) {
    UserPreferences preferences = preferencesService.getPreferencesByUser(userId);
    return ResponseEntity.ok(preferences);
  }

  @GetMapping("/check/{userId}")
  public ResponseEntity<Boolean> hasUserSetPreferences(@PathVariable Long userId) {
    boolean hasPreferences = preferencesService.hasUserSetPreferences(userId);
    return ResponseEntity.ok(hasPreferences);
  }
}
