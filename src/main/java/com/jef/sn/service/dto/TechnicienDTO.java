package com.jef.sn.service.dto;

import com.jef.sn.domain.enumeration.StatutValidation;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.jef.sn.domain.Technicien} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TechnicienDTO implements Serializable {

    private Long id;

    @NotNull
    private String nom;

    @NotNull
    private String prenom;

    @NotNull
    private Double tarifMin;

    @NotNull
    private Double tarifMax;

    private Double latitude;

    private Double longitude;

    private Boolean disponible;

    private StatutValidation statutValidation;

    private Double noteMoyenne;

    private UserDTO user;

    private Set<ServiceJefDTO> serviceJefs = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Double getTarifMin() {
        return tarifMin;
    }

    public void setTarifMin(Double tarifMin) {
        this.tarifMin = tarifMin;
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

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public StatutValidation getStatutValidation() {
        return statutValidation;
    }

    public void setStatutValidation(StatutValidation statutValidation) {
        this.statutValidation = statutValidation;
    }

    public Double getNoteMoyenne() {
        return noteMoyenne;
    }

    public void setNoteMoyenne(Double noteMoyenne) {
        this.noteMoyenne = noteMoyenne;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Set<ServiceJefDTO> getServiceJefs() {
        return serviceJefs;
    }

    public void setServiceJefs(Set<ServiceJefDTO> serviceJefs) {
        this.serviceJefs = serviceJefs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TechnicienDTO)) {
            return false;
        }

        TechnicienDTO technicienDTO = (TechnicienDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, technicienDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TechnicienDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", tarifMin=" + getTarifMin() +
            ", tarifMax=" + getTarifMax() +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", disponible='" + getDisponible() + "'" +
            ", statutValidation='" + getStatutValidation() + "'" +
            ", noteMoyenne=" + getNoteMoyenne() +
            ", user=" + getUser() +
            ", serviceJefs=" + getServiceJefs() +
            "}";
    }
}
