package com.coursework.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchCriteria {
  private List<String> cities;
  private List<String> cuisines;
  private List<String> ratings;
  private List<String> priceCategories;

  @Override
  public String toString() {
    return String.format(
            "Cities: %s, Cuisines: %s, Ratings: %s, PriceCategories: %s",
            cities, cuisines, ratings, priceCategories
    );
  }
}