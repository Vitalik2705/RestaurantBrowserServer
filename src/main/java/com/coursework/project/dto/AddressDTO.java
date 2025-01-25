package com.coursework.project.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressDTO {
  @NotBlank(message = "Address cannot be blank")
  @Size(max = 255, message = "Address cannot exceed 255 characters")
  private String formattedAddress;

  @NotBlank(message = "Latitude cannot be blank")
  private Double latitude;

  @NotBlank(message = "Longitude cannot be blank")
  private Double longitude;

  @NotBlank(message = "City cannot be blank")
  @Size(max = 100, message = "City cannot exceed 100 characters")
  private String city;

  @NotBlank(message = "Country cannot be blank")
  @Size(max = 100, message = "Country cannot exceed 100 characters")
  private String country;
}
