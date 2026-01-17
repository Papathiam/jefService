package com.jef.sn.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jef.sn.domain.enumeration.StatutValidation;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Technicien.
 */
@Entity
@Table(name = "technicien")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Technicien implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nom", nullable = false)
    private String nom;

    @NotNull
    @Column(name = "prenom", nullable = false)
    private String prenom;

    @NotNull
    @Column(name = "tarif_min", nullable = false)
    private Double tarifMin;

    @NotNull
    @Column(name = "tarif_max", nullable = false)
    private Double tarifMax;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "disponible")
    private Boolean disponible;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_validation")
    private StatutValidation statutValidation;

    @Column(name = "note_moyenne")
    private Double noteMoyenne;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "technicien")
    @JsonIgnoreProperties(value = { "technicien" }, allowSetters = true)
    private Set<Document> documents = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "technicien")
    @JsonIgnoreProperties(value = { "paiement", "serviceJef", "client", "technicien" }, allowSetters = true)
    private Set<Mission> missions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_technicien__service_jefs",
        joinColumns = @JoinColumn(name = "technicien_id"),
        inverseJoinColumns = @JoinColumn(name = "service_jefs_id")
    )
    @JsonIgnoreProperties(value = { "missions", "techniciens" }, allowSetters = true)
    private Set<ServiceJef> serviceJefs = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Technicien id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Technicien nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public Technicien prenom(String prenom) {
        this.setPrenom(prenom);
        return this;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Double getTarifMin() {
        return this.tarifMin;
    }

    public Technicien tarifMin(Double tarifMin) {
        this.setTarifMin(tarifMin);
        return this;
    }

    public void setTarifMin(Double tarifMin) {
        this.tarifMin = tarifMin;
    }

    public Double getTarifMax() {
        return this.tarifMax;
    }

    public Technicien tarifMax(Double tarifMax) {
        this.setTarifMax(tarifMax);
        return this;
    }

    public void setTarifMax(Double tarifMax) {
        this.tarifMax = tarifMax;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Technicien latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Technicien longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getDisponible() {
        return this.disponible;
    }

    public Technicien disponible(Boolean disponible) {
        this.setDisponible(disponible);
        return this;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public StatutValidation getStatutValidation() {
        return this.statutValidation;
    }

    public Technicien statutValidation(StatutValidation statutValidation) {
        this.setStatutValidation(statutValidation);
        return this;
    }

    public void setStatutValidation(StatutValidation statutValidation) {
        this.statutValidation = statutValidation;
    }

    public Double getNoteMoyenne() {
        return this.noteMoyenne;
    }

    public Technicien noteMoyenne(Double noteMoyenne) {
        this.setNoteMoyenne(noteMoyenne);
        return this;
    }

    public void setNoteMoyenne(Double noteMoyenne) {
        this.noteMoyenne = noteMoyenne;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Technicien user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Document> getDocuments() {
        return this.documents;
    }

    public void setDocuments(Set<Document> documents) {
        if (this.documents != null) {
            this.documents.forEach(i -> i.setTechnicien(null));
        }
        if (documents != null) {
            documents.forEach(i -> i.setTechnicien(this));
        }
        this.documents = documents;
    }

    public Technicien documents(Set<Document> documents) {
        this.setDocuments(documents);
        return this;
    }

    public Technicien addDocument(Document document) {
        this.documents.add(document);
        document.setTechnicien(this);
        return this;
    }

    public Technicien removeDocument(Document document) {
        this.documents.remove(document);
        document.setTechnicien(null);
        return this;
    }

    public Set<Mission> getMissions() {
        return this.missions;
    }

    public void setMissions(Set<Mission> missions) {
        if (this.missions != null) {
            this.missions.forEach(i -> i.setTechnicien(null));
        }
        if (missions != null) {
            missions.forEach(i -> i.setTechnicien(this));
        }
        this.missions = missions;
    }

    public Technicien missions(Set<Mission> missions) {
        this.setMissions(missions);
        return this;
    }

    public Technicien addMission(Mission mission) {
        this.missions.add(mission);
        mission.setTechnicien(this);
        return this;
    }

    public Technicien removeMission(Mission mission) {
        this.missions.remove(mission);
        mission.setTechnicien(null);
        return this;
    }

    public Set<ServiceJef> getServiceJefs() {
        return this.serviceJefs;
    }

    public void setServiceJefs(Set<ServiceJef> serviceJefs) {
        this.serviceJefs = serviceJefs;
    }

    public Technicien serviceJefs(Set<ServiceJef> serviceJefs) {
        this.setServiceJefs(serviceJefs);
        return this;
    }

    public Technicien addServiceJefs(ServiceJef serviceJef) {
        this.serviceJefs.add(serviceJef);
        return this;
    }

    public Technicien removeServiceJefs(ServiceJef serviceJef) {
        this.serviceJefs.remove(serviceJef);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Technicien)) {
            return false;
        }
        return getId() != null && getId().equals(((Technicien) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Technicien{" +
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
            "}";
    }
}
