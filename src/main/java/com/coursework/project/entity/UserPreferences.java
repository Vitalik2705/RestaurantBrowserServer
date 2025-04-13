package com.coursework.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "user_preferences")
public class UserPreferences {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false)
  @JsonIgnore
  private User user;

  @ElementCollection
  @Enumerated(EnumType.STRING)
  @CollectionTable(name = "user_preferred_cuisines")
  @Column(name = "cuisine_type")
  private Set<CuisineType> preferredCuisines;

  @Enumerated(EnumType.STRING)
  @Column(name = "price_preference")
  private PriceCategory pricePreference;

  @Column(name = "minimum_rating")
  private Double minimumRating;

  @Column(name = "preferred_city")
  private String city;

  @Column(name = "preferred_country")
  private String country;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}