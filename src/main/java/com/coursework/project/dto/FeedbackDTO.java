package com.coursework.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

public class FeedbackDTO {
    @NotNull(message = "Rating cannot be null")
    private double rating;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotBlank(message = "Advantages cannot be blank")
    private String advantages;

    @NotBlank(message = "Disadvantages cannot be blank")
    private String disadvantages;

    @NotNull(message = "Date cannot be null")
    private Date date;

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdvantages() {
        return advantages;
    }

    public void setAdvantages(String advantages) {
        this.advantages = advantages;
    }

    public String getDisadvantages() {
        return disadvantages;
    }

    public void setDisadvantages(String disadvantages) {
        this.disadvantages = disadvantages;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
