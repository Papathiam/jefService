package com.jef.sn.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jef.sn.domain.enumeration.MoyenPaiement;
import com.jef.sn.domain.enumeration.StatutPaiement;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A Paiement.
 */
@Entity
@Table(name = "paiement")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Paiement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "montant_total", nullable = false)
    private Double montantTotal;

    @NotNull
    @Column(name = "commission", nullable = false)
    private Double commission;

    @NotNull
    @Column(name = "montant_technicien", nullable = false)
    private Double montantTechnicien;

    @Enumerated(EnumType.STRING)
    @Column(name = "moyen_paiement")
    private MoyenPaiement moyenPaiement;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutPaiement statut;

    @Column(name = "reference")
    private String reference;

    @JsonIgnoreProperties(value = { "paiement", "serviceJef", "client", "technicien" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "paiement")
    private Mission mission;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Paiement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMontantTotal() {
        return this.montantTotal;
    }

    public Paiement montantTotal(Double montantTotal) {
        this.setMontantTotal(montantTotal);
        return this;
    }

    public void setMontantTotal(Double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public Double getCommission() {
        return this.commission;
    }

    public Paiement commission(Double commission) {
        this.setCommission(commission);
        return this;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public Double getMontantTechnicien() {
        return this.montantTechnicien;
    }

    public Paiement montantTechnicien(Double montantTechnicien) {
        this.setMontantTechnicien(montantTechnicien);
        return this;
    }

    public void setMontantTechnicien(Double montantTechnicien) {
        this.montantTechnicien = montantTechnicien;
    }

    public MoyenPaiement getMoyenPaiement() {
        return this.moyenPaiement;
    }

    public Paiement moyenPaiement(MoyenPaiement moyenPaiement) {
        this.setMoyenPaiement(moyenPaiement);
        return this;
    }

    public void setMoyenPaiement(MoyenPaiement moyenPaiement) {
        this.moyenPaiement = moyenPaiement;
    }

    public StatutPaiement getStatut() {
        return this.statut;
    }

    public Paiement statut(StatutPaiement statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutPaiement statut) {
        this.statut = statut;
    }

    public String getReference() {
        return this.reference;
    }

    public Paiement reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Mission getMission() {
        return this.mission;
    }

    public void setMission(Mission mission) {
        if (this.mission != null) {
            this.mission.setPaiement(null);
        }
        if (mission != null) {
            mission.setPaiement(this);
        }
        this.mission = mission;
    }

    public Paiement mission(Mission mission) {
        this.setMission(mission);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Paiement)) {
            return false;
        }
        return getId() != null && getId().equals(((Paiement) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Paiement{" +
            "id=" + getId() +
            ", montantTotal=" + getMontantTotal() +
            ", commission=" + getCommission() +
            ", montantTechnicien=" + getMontantTechnicien() +
            ", moyenPaiement='" + getMoyenPaiement() + "'" +
            ", statut='" + getStatut() + "'" +
            ", reference='" + getReference() + "'" +
            "}";
    }
}
