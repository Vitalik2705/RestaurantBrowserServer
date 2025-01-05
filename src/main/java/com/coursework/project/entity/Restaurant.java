package com.coursework.project.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "restaurant")
public class Restaurant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "restaurant_id")
  private Long restaurantId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", nullable = false, columnDefinition = "TEXT")
  private String description;

  @ElementCollection
  @Column(name = "photos")
  private List<String> photos = new ArrayList<>();
  ;

  @Column(name = "address", nullable = false)
  private String address;

  @Column(name = "rating")
  private double rating;

  @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
  private List<WorkHours> workHours;

  @Enumerated(EnumType.STRING)
  @Column(name = "cuisine_type")
  private CuisineType cuisineType;

  @Column(name = "city", nullable = false)
  private String city;

  @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
  private List<DiningTable> diningTables;

  @Column(name = "website")
  private String website;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "contact_info_id", referencedColumnName = "contact_info_id")
  private ContactInfo contactInfo;

  @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
  private List<Feedback> feedbackList;

  @Column(name = "menu")
  private String menu;

  @Column(name = "popularity_count")
  private int popularityCount;

  @Enumerated(EnumType.STRING)
  @Column(name = "price_category")
  private PriceCategory priceCategory;
}
