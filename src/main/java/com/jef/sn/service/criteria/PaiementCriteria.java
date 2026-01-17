package com.jef.sn.service.criteria;

import com.jef.sn.domain.enumeration.MoyenPaiement;
import com.jef.sn.domain.enumeration.StatutPaiement;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.jef.sn.domain.Paiement} entity. This class is used
 * in {@link com.jef.sn.web.rest.PaiementResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /paiements?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaiementCriteria implements Serializable, Criteria {

    /**
     * Class for filtering MoyenPaiement
     */
    public static class MoyenPaiementFilter extends Filter<MoyenPaiement> {

        public MoyenPaiementFilter() {}

        public MoyenPaiementFilter(MoyenPaiementFilter filter) {
            super(filter);
        }

        @Override
        public MoyenPaiementFilter copy() {
            return new MoyenPaiementFilter(this);
        }
    }

    /**
     * Class for filtering StatutPaiement
     */
    public static class StatutPaiementFilter extends Filter<StatutPaiement> {

        public StatutPaiementFilter() {}

        public StatutPaiementFilter(StatutPaiementFilter filter) {
            super(filter);
        }

        @Override
        public StatutPaiementFilter copy() {
            return new StatutPaiementFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private DoubleFilter montantTotal;

    private DoubleFilter commission;

    private DoubleFilter montantTechnicien;

    private MoyenPaiementFilter moyenPaiement;

    private StatutPaiementFilter statut;

    private StringFilter reference;

    private LongFilter missionId;

    private Boolean distinct;

    public PaiementCriteria() {}

    public PaiementCriteria(PaiementCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.montantTotal = other.optionalMontantTotal().map(DoubleFilter::copy).orElse(null);
        this.commission = other.optionalCommission().map(DoubleFilter::copy).orElse(null);
        this.montantTechnicien = other.optionalMontantTechnicien().map(DoubleFilter::copy).orElse(null);
        this.moyenPaiement = other.optionalMoyenPaiement().map(MoyenPaiementFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutPaiementFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.missionId = other.optionalMissionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PaiementCriteria copy() {
        return new PaiementCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public DoubleFilter getMontantTotal() {
        return montantTotal;
    }

    public Optional<DoubleFilter> optionalMontantTotal() {
        return Optional.ofNullable(montantTotal);
    }

    public DoubleFilter montantTotal() {
        if (montantTotal == null) {
            setMontantTotal(new DoubleFilter());
        }
        return montantTotal;
    }

    public void setMontantTotal(DoubleFilter montantTotal) {
        this.montantTotal = montantTotal;
    }

    public DoubleFilter getCommission() {
        return commission;
    }

    public Optional<DoubleFilter> optionalCommission() {
        return Optional.ofNullable(commission);
    }

    public DoubleFilter commission() {
        if (commission == null) {
            setCommission(new DoubleFilter());
        }
        return commission;
    }

    public void setCommission(DoubleFilter commission) {
        this.commission = commission;
    }

    public DoubleFilter getMontantTechnicien() {
        return montantTechnicien;
    }

    public Optional<DoubleFilter> optionalMontantTechnicien() {
        return Optional.ofNullable(montantTechnicien);
    }

    public DoubleFilter montantTechnicien() {
        if (montantTechnicien == null) {
            setMontantTechnicien(new DoubleFilter());
        }
        return montantTechnicien;
    }

    public void setMontantTechnicien(DoubleFilter montantTechnicien) {
        this.montantTechnicien = montantTechnicien;
    }

    public MoyenPaiementFilter getMoyenPaiement() {
        return moyenPaiement;
    }

    public Optional<MoyenPaiementFilter> optionalMoyenPaiement() {
        return Optional.ofNullable(moyenPaiement);
    }

    public MoyenPaiementFilter moyenPaiement() {
        if (moyenPaiement == null) {
            setMoyenPaiement(new MoyenPaiementFilter());
        }
        return moyenPaiement;
    }

    public void setMoyenPaiement(MoyenPaiementFilter moyenPaiement) {
        this.moyenPaiement = moyenPaiement;
    }

    public StatutPaiementFilter getStatut() {
        return statut;
    }

    public Optional<StatutPaiementFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public StatutPaiementFilter statut() {
        if (statut == null) {
            setStatut(new StatutPaiementFilter());
        }
        return statut;
    }

    public void setStatut(StatutPaiementFilter statut) {
        this.statut = statut;
    }

    public StringFilter getReference() {
        return reference;
    }

    public Optional<StringFilter> optionalReference() {
        return Optional.ofNullable(reference);
    }

    public StringFilter reference() {
        if (reference == null) {
            setReference(new StringFilter());
        }
        return reference;
    }

    public void setReference(StringFilter reference) {
        this.reference = reference;
    }

    public LongFilter getMissionId() {
        return missionId;
    }

    public Optional<LongFilter> optionalMissionId() {
        return Optional.ofNullable(missionId);
    }

    public LongFilter missionId() {
        if (missionId == null) {
            setMissionId(new LongFilter());
        }
        return missionId;
    }

    public void setMissionId(LongFilter missionId) {
        this.missionId = missionId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PaiementCriteria that = (PaiementCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(montantTotal, that.montantTotal) &&
            Objects.equals(commission, that.commission) &&
            Objects.equals(montantTechnicien, that.montantTechnicien) &&
            Objects.equals(moyenPaiement, that.moyenPaiement) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(missionId, that.missionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, montantTotal, commission, montantTechnicien, moyenPaiement, statut, reference, missionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaiementCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalMontantTotal().map(f -> "montantTotal=" + f + ", ").orElse("") +
            optionalCommission().map(f -> "commission=" + f + ", ").orElse("") +
            optionalMontantTechnicien().map(f -> "montantTechnicien=" + f + ", ").orElse("") +
            optionalMoyenPaiement().map(f -> "moyenPaiement=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalMissionId().map(f -> "missionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
