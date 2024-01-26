package com.coursework.project.dto;

import com.coursework.project.entity.DayOfWeek;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class WorkHoursDTO {

    @NotNull(message = "Day of week cannot be null")
    private DayOfWeek dayOfWeek;

    @NotBlank(message = "Start time cannot be blank")
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "Invalid time format. Use HH:mm")
    private String startTime;

    @NotBlank(message = "End time cannot be blank")
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "Invalid time format. Use HH:mm")
    private String endTime;

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}

