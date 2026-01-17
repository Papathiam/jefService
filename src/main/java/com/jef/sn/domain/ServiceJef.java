package com.jef.sn.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A ServiceJef.
 */
@Entity
@Table(name = "service_jef")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServiceJef implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "description")
    private String description;

    @Column(name = "actif")
    private Boolean actif;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "serviceJef")
    @JsonIgnoreProperties(value = { "paiement", "serviceJef", "client", "technicien" }, allowSetters = true)
    private Set<Mission> missions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "serviceJefs")
    @JsonIgnoreProperties(value = { "user", "documents", "missions", "serviceJefs" }, allowSetters = true)
    private Set<Technicien> techniciens = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ServiceJef id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public ServiceJef nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return this.description;
    }

    public ServiceJef description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActif() {
        return this.actif;
    }

    public ServiceJef actif(Boolean actif) {
        this.setActif(actif);
        return this;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public Set<Mission> getMissions() {
        return this.missions;
    }

    public void setMissions(Set<Mission> missions) {
        if (this.missions != null) {
            this.missions.forEach(i -> i.setServiceJef(null));
        }
        if (missions != null) {
            missions.forEach(i -> i.setServiceJef(this));
        }
        this.missions = missions;
    }

    public ServiceJef missions(Set<Mission> missions) {
        this.setMissions(missions);
        return this;
    }

    public ServiceJef addMissions(Mission mission) {
        this.missions.add(mission);
        mission.setServiceJef(this);
        return this;
    }

    public ServiceJef removeMissions(Mission mission) {
        this.missions.remove(mission);
        mission.setServiceJef(null);
        return this;
    }

    public Set<Technicien> getTechniciens() {
        return this.techniciens;
    }

    public void setTechniciens(Set<Technicien> techniciens) {
        if (this.techniciens != null) {
            this.techniciens.forEach(i -> i.removeServiceJefs(this));
        }
        if (techniciens != null) {
            techniciens.forEach(i -> i.addServiceJefs(this));
        }
        this.techniciens = techniciens;
    }

    public ServiceJef techniciens(Set<Technicien> techniciens) {
        this.setTechniciens(techniciens);
        return this;
    }

    public ServiceJef addTechniciens(Technicien technicien) {
        this.techniciens.add(technicien);
        technicien.getServiceJefs().add(this);
        return this;
    }

    public ServiceJef removeTechniciens(Technicien technicien) {
        this.techniciens.remove(technicien);
        technicien.getServiceJefs().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceJef)) {
            return false;
        }
        return getId() != null && getId().equals(((ServiceJef) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServiceJef{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", description='" + getDescription() + "'" +
            ", actif='" + getActif() + "'" +
            "}";
    }
}
