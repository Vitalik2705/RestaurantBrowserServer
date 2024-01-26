package com.coursework.project.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DiningTableDTO {
    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
