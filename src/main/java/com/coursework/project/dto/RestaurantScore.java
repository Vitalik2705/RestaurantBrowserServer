package com.coursework.project.dto;


import com.coursework.project.entity.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class RestaurantScore {
  private Restaurant restaurant;
  private Integer matchScore;
}
