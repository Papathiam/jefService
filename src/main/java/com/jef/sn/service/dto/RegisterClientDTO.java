package com.jef.sn.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class RegisterClientDTO {

    @NotNull
    @Pattern(regexp = "^\\d{9,15}$") // valide 8 à 15 chiffres
    private String phoneNumber;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    // Getters / Setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
