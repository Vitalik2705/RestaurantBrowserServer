package com.coursework.project.dto;

import com.coursework.project.entity.DayOfWeek;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class WorkHoursDTO {
  @NotNull(message = "Day of week cannot be null")
  private DayOfWeek dayOfWeek;

  private boolean isDayOff;

  @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "Invalid time format. Use HH:mm")
  private String startTime;

  @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "Invalid time format. Use HH:mm")
  private String endTime;
}

