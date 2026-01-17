package com.jef.sn.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jef.sn.domain.enumeration.StatutMission;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Mission.
 */
@Entity
@Table(name = "mission")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Mission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutMission statut;

    @Column(name = "prix_propose")
    private Double prixPropose;

    @Column(name = "date_creation")
    private Instant dateCreation;

    @JsonIgnoreProperties(value = { "mission" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Paiement paiement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "missions", "techniciens" }, allowSetters = true)
    private ServiceJef serviceJef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "missions" }, allowSetters = true)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "documents", "missions", "serviceJefs" }, allowSetters = true)
    private Technicien technicien;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Mission id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public Mission description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Mission latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Mission longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public StatutMission getStatut() {
        return this.statut;
    }

    public Mission statut(StatutMission statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutMission statut) {
        this.statut = statut;
    }

    public Double getPrixPropose() {
        return this.prixPropose;
    }

    public Mission prixPropose(Double prixPropose) {
        this.setPrixPropose(prixPropose);
        return this;
    }

    public void setPrixPropose(Double prixPropose) {
        this.prixPropose = prixPropose;
    }

    public Instant getDateCreation() {
        return this.dateCreation;
    }

    public Mission dateCreation(Instant dateCreation) {
        this.setDateCreation(dateCreation);
        return this;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Paiement getPaiement() {
        return this.paiement;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }

    public Mission paiement(Paiement paiement) {
        this.setPaiement(paiement);
        return this;
    }

    public ServiceJef getServiceJef() {
        return this.serviceJef;
    }

    public void setServiceJef(ServiceJef serviceJef) {
        this.serviceJef = serviceJef;
    }

    public Mission serviceJef(ServiceJef serviceJef) {
        this.setServiceJef(serviceJef);
        return this;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Mission client(Client client) {
        this.setClient(client);
        return this;
    }

    public Technicien getTechnicien() {
        return this.technicien;
    }

    public void setTechnicien(Technicien technicien) {
        this.technicien = technicien;
    }

    public Mission technicien(Technicien technicien) {
        this.setTechnicien(technicien);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Mission)) {
            return false;
        }
        return getId() != null && getId().equals(((Mission) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Mission{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", statut='" + getStatut() + "'" +
            ", prixPropose=" + getPrixPropose() +
            ", dateCreation='" + getDateCreation() + "'" +
            "}";
    }
}
