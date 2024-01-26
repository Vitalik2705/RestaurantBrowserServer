package com.coursework.project.dto;

import com.coursework.project.entity.CuisineType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

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

    public int getPopularityCount() {
        return popularityCount;
    }

    public void setPopularityCount(int popularityCount) {
        this.popularityCount = popularityCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<WorkHoursDTO> getWorkHours() {
        return workHours;
    }

    public void setWorkHours(List<WorkHoursDTO> workHours) {
        this.workHours = workHours;
    }

    public CuisineType getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(CuisineType cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<DiningTableDTO> getDiningTables() {
        return diningTables;
    }

    public void setDiningTables(List<DiningTableDTO> diningTables) {
        this.diningTables = diningTables;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public ContactInfoDTO getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfoDTO contactInfo) {
        this.contactInfo = contactInfo;
    }

    public List<FeedbackDTO> getFeedbackList() {
        return feedbackList;
    }

    public void setFeedbackList(List<FeedbackDTO> feedbackList) {
        this.feedbackList = feedbackList;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }
}
