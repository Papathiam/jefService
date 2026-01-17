package com.jef.sn.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.jef.sn.domain.ServiceJef} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServiceJefDTO implements Serializable {

    private Long id;

    @NotNull
    private String nom;

    private String description;

    private Boolean actif;

    private Set<TechnicienDTO> techniciens = new HashSet<>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public Set<TechnicienDTO> getTechniciens() {
        return techniciens;
    }

    public void setTechniciens(Set<TechnicienDTO> techniciens) {
        this.techniciens = techniciens;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceJefDTO)) {
            return false;
        }

        ServiceJefDTO serviceJefDTO = (ServiceJefDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, serviceJefDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServiceJefDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", description='" + getDescription() + "'" +
            ", actif='" + getActif() + "'" +
            ", techniciens=" + getTechniciens() +
            "}";
    }
}
