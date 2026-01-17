package com.jef.sn.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

public class ValidateTechnicienOtpDTO {

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String otp;

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    @NotNull
    private Double tarifMin;

    @NotNull
    private Double tarifMax;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Double getTarifMin() {
        return tarifMin;
    }

    public void setTarifMin(Double tarifMin) {
        this.tarifMin = tarifMin;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Double getTarifMax() {
        return tarifMax;
    }

    public void setTarifMax(Double tarifMax) {
        this.tarifMax = tarifMax;
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

    @java.lang.Override
    public java.lang.String toString() {
        return (
            "ValidateTechnicienOtpDTO{" +
            "phoneNumber='" +
            phoneNumber +
            '\'' +
            ", otp='" +
            otp +
            '\'' +
            ", nom='" +
            nom +
            '\'' +
            ", prenom='" +
            prenom +
            '\'' +
            ", tarifMin=" +
            tarifMin +
            ", tarifMax=" +
            tarifMax +
            ", latitude=" +
            latitude +
            ", longitude=" +
            longitude +
            '}'
        );
    }
}
