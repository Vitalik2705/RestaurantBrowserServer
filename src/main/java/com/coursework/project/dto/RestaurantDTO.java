package com.coursework.project.dto;

import com.coursework.project.entity.CuisineType;
import com.coursework.project.entity.PriceCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class RestaurantDTO {

  @NotBlank(message = "Name cannot be blank")
  @Size(max = 255, message = "Name cannot exceed 255 characters")
  private String name;

  @NotBlank(message = "Description cannot be blank")
  private String description;

  private List<String> photos;

  @NotBlank(message = "Address cannot be blank")
  private String address;

  private double rating;

  private List<WorkHoursDTO> workHours;

  @NotNull(message = "Cuisine type cannot be null")
  private CuisineType cuisineType;

  @NotBlank(message = "City cannot be blank")
  private String city;

  private List<DiningTableDTO> diningTables;

  private String website;

  private ContactInfoDTO contactInfo;

  private List<FeedbackDTO> feedbackList;

  private String menu;

  private int popularityCount;

  @NotNull(message = "Price category cannot be null")
  private PriceCategory priceCategory;
}
