package com.coursework.project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public class ContactInfoDTO {

    @Pattern(regexp = "^\\+?\\d{10}$"
            , message = "Invalid phone number format")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    private String email;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
