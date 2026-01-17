package com.jef.sn.service.criteria;

import com.jef.sn.domain.enumeration.StatutValidation;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.jef.sn.domain.Technicien} entity. This class is used
 * in {@link com.jef.sn.web.rest.TechnicienResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /techniciens?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TechnicienCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StatutValidation
     */
    public static class StatutValidationFilter extends Filter<StatutValidation> {

        public StatutValidationFilter() {}

        public StatutValidationFilter(StatutValidationFilter filter) {
            super(filter);
        }

        @Override
        public StatutValidationFilter copy() {
            return new StatutValidationFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nom;

    private StringFilter prenom;

    private DoubleFilter tarifMin;

    private DoubleFilter tarifMax;

    private DoubleFilter latitude;

    private DoubleFilter longitude;

    private BooleanFilter disponible;

    private StatutValidationFilter statutValidation;

    private DoubleFilter noteMoyenne;

    private LongFilter userId;

    private LongFilter documentId;

    private LongFilter missionId;

    private LongFilter serviceJefsId;

    private Boolean distinct;

    public TechnicienCriteria() {}

    public TechnicienCriteria(TechnicienCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nom = other.optionalNom().map(StringFilter::copy).orElse(null);
        this.prenom = other.optionalPrenom().map(StringFilter::copy).orElse(null);
        this.tarifMin = other.optionalTarifMin().map(DoubleFilter::copy).orElse(null);
        this.tarifMax = other.optionalTarifMax().map(DoubleFilter::copy).orElse(null);
        this.latitude = other.optionalLatitude().map(DoubleFilter::copy).orElse(null);
        this.longitude = other.optionalLongitude().map(DoubleFilter::copy).orElse(null);
        this.disponible = other.optionalDisponible().map(BooleanFilter::copy).orElse(null);
        this.statutValidation = other.optionalStatutValidation().map(StatutValidationFilter::copy).orElse(null);
        this.noteMoyenne = other.optionalNoteMoyenne().map(DoubleFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.documentId = other.optionalDocumentId().map(LongFilter::copy).orElse(null);
        this.missionId = other.optionalMissionId().map(LongFilter::copy).orElse(null);
        this.serviceJefsId = other.optionalServiceJefsId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TechnicienCriteria copy() {
        return new TechnicienCriteria(this);
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

    public StringFilter getNom() {
        return nom;
    }

    public Optional<StringFilter> optionalNom() {
        return Optional.ofNullable(nom);
    }

    public StringFilter nom() {
        if (nom == null) {
            setNom(new StringFilter());
        }
        return nom;
    }

    public void setNom(StringFilter nom) {
        this.nom = nom;
    }

    public StringFilter getPrenom() {
        return prenom;
    }

    public Optional<StringFilter> optionalPrenom() {
        return Optional.ofNullable(prenom);
    }

    public StringFilter prenom() {
        if (prenom == null) {
            setPrenom(new StringFilter());
        }
        return prenom;
    }

    public void setPrenom(StringFilter prenom) {
        this.prenom = prenom;
    }

    public DoubleFilter getTarifMin() {
        return tarifMin;
    }

    public Optional<DoubleFilter> optionalTarifMin() {
        return Optional.ofNullable(tarifMin);
    }

    public DoubleFilter tarifMin() {
        if (tarifMin == null) {
            setTarifMin(new DoubleFilter());
        }
        return tarifMin;
    }

    public void setTarifMin(DoubleFilter tarifMin) {
        this.tarifMin = tarifMin;
    }

    public DoubleFilter getTarifMax() {
        return tarifMax;
    }

    public Optional<DoubleFilter> optionalTarifMax() {
        return Optional.ofNullable(tarifMax);
    }

    public DoubleFilter tarifMax() {
        if (tarifMax == null) {
            setTarifMax(new DoubleFilter());
        }
        return tarifMax;
    }

    public void setTarifMax(DoubleFilter tarifMax) {
        this.tarifMax = tarifMax;
    }

    public DoubleFilter getLatitude() {
        return latitude;
    }

    public Optional<DoubleFilter> optionalLatitude() {
        return Optional.ofNullable(latitude);
    }

    public DoubleFilter latitude() {
        if (latitude == null) {
            setLatitude(new DoubleFilter());
        }
        return latitude;
    }

    public void setLatitude(DoubleFilter latitude) {
        this.latitude = latitude;
    }

    public DoubleFilter getLongitude() {
        return longitude;
    }

    public Optional<DoubleFilter> optionalLongitude() {
        return Optional.ofNullable(longitude);
    }

    public DoubleFilter longitude() {
        if (longitude == null) {
            setLongitude(new DoubleFilter());
        }
        return longitude;
    }

    public void setLongitude(DoubleFilter longitude) {
        this.longitude = longitude;
    }

    public BooleanFilter getDisponible() {
        return disponible;
    }

    public Optional<BooleanFilter> optionalDisponible() {
        return Optional.ofNullable(disponible);
    }

    public BooleanFilter disponible() {
        if (disponible == null) {
            setDisponible(new BooleanFilter());
        }
        return disponible;
    }

    public void setDisponible(BooleanFilter disponible) {
        this.disponible = disponible;
    }

    public StatutValidationFilter getStatutValidation() {
        return statutValidation;
    }

    public Optional<StatutValidationFilter> optionalStatutValidation() {
        return Optional.ofNullable(statutValidation);
    }

    public StatutValidationFilter statutValidation() {
        if (statutValidation == null) {
            setStatutValidation(new StatutValidationFilter());
        }
        return statutValidation;
    }

    public void setStatutValidation(StatutValidationFilter statutValidation) {
        this.statutValidation = statutValidation;
    }

    public DoubleFilter getNoteMoyenne() {
        return noteMoyenne;
    }

    public Optional<DoubleFilter> optionalNoteMoyenne() {
        return Optional.ofNullable(noteMoyenne);
    }

    public DoubleFilter noteMoyenne() {
        if (noteMoyenne == null) {
            setNoteMoyenne(new DoubleFilter());
        }
        return noteMoyenne;
    }

    public void setNoteMoyenne(DoubleFilter noteMoyenne) {
        this.noteMoyenne = noteMoyenne;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getDocumentId() {
        return documentId;
    }

    public Optional<LongFilter> optionalDocumentId() {
        return Optional.ofNullable(documentId);
    }

    public LongFilter documentId() {
        if (documentId == null) {
            setDocumentId(new LongFilter());
        }
        return documentId;
    }

    public void setDocumentId(LongFilter documentId) {
        this.documentId = documentId;
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

    public LongFilter getServiceJefsId() {
        return serviceJefsId;
    }

    public Optional<LongFilter> optionalServiceJefsId() {
        return Optional.ofNullable(serviceJefsId);
    }

    public LongFilter serviceJefsId() {
        if (serviceJefsId == null) {
            setServiceJefsId(new LongFilter());
        }
        return serviceJefsId;
    }

    public void setServiceJefsId(LongFilter serviceJefsId) {
        this.serviceJefsId = serviceJefsId;
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
        final TechnicienCriteria that = (TechnicienCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nom, that.nom) &&
            Objects.equals(prenom, that.prenom) &&
            Objects.equals(tarifMin, that.tarifMin) &&
            Objects.equals(tarifMax, that.tarifMax) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(longitude, that.longitude) &&
            Objects.equals(disponible, that.disponible) &&
            Objects.equals(statutValidation, that.statutValidation) &&
            Objects.equals(noteMoyenne, that.noteMoyenne) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(documentId, that.documentId) &&
            Objects.equals(missionId, that.missionId) &&
            Objects.equals(serviceJefsId, that.serviceJefsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            nom,
            prenom,
            tarifMin,
            tarifMax,
            latitude,
            longitude,
            disponible,
            statutValidation,
            noteMoyenne,
            userId,
            documentId,
            missionId,
            serviceJefsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TechnicienCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNom().map(f -> "nom=" + f + ", ").orElse("") +
            optionalPrenom().map(f -> "prenom=" + f + ", ").orElse("") +
            optionalTarifMin().map(f -> "tarifMin=" + f + ", ").orElse("") +
            optionalTarifMax().map(f -> "tarifMax=" + f + ", ").orElse("") +
            optionalLatitude().map(f -> "latitude=" + f + ", ").orElse("") +
            optionalLongitude().map(f -> "longitude=" + f + ", ").orElse("") +
            optionalDisponible().map(f -> "disponible=" + f + ", ").orElse("") +
            optionalStatutValidation().map(f -> "statutValidation=" + f + ", ").orElse("") +
            optionalNoteMoyenne().map(f -> "noteMoyenne=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalDocumentId().map(f -> "documentId=" + f + ", ").orElse("") +
            optionalMissionId().map(f -> "missionId=" + f + ", ").orElse("") +
            optionalServiceJefsId().map(f -> "serviceJefsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
