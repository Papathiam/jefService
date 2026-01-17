package com.jef.sn.service.criteria;

import com.jef.sn.domain.enumeration.StatutMission;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.jef.sn.domain.Mission} entity. This class is used
 * in {@link com.jef.sn.web.rest.MissionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /missions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MissionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StatutMission
     */
    public static class StatutMissionFilter extends Filter<StatutMission> {

        public StatutMissionFilter() {}

        public StatutMissionFilter(StatutMissionFilter filter) {
            super(filter);
        }

        @Override
        public StatutMissionFilter copy() {
            return new StatutMissionFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter description;

    private DoubleFilter latitude;

    private DoubleFilter longitude;

    private StatutMissionFilter statut;

    private DoubleFilter prixPropose;

    private InstantFilter dateCreation;

    private LongFilter paiementId;

    private LongFilter serviceJefId;

    private LongFilter clientId;

    private LongFilter technicienId;

    private Boolean distinct;

    public MissionCriteria() {}

    public MissionCriteria(MissionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.latitude = other.optionalLatitude().map(DoubleFilter::copy).orElse(null);
        this.longitude = other.optionalLongitude().map(DoubleFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutMissionFilter::copy).orElse(null);
        this.prixPropose = other.optionalPrixPropose().map(DoubleFilter::copy).orElse(null);
        this.dateCreation = other.optionalDateCreation().map(InstantFilter::copy).orElse(null);
        this.paiementId = other.optionalPaiementId().map(LongFilter::copy).orElse(null);
        this.serviceJefId = other.optionalServiceJefId().map(LongFilter::copy).orElse(null);
        this.clientId = other.optionalClientId().map(LongFilter::copy).orElse(null);
        this.technicienId = other.optionalTechnicienId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public MissionCriteria copy() {
        return new MissionCriteria(this);
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

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
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

    public StatutMissionFilter getStatut() {
        return statut;
    }

    public Optional<StatutMissionFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public StatutMissionFilter statut() {
        if (statut == null) {
            setStatut(new StatutMissionFilter());
        }
        return statut;
    }

    public void setStatut(StatutMissionFilter statut) {
        this.statut = statut;
    }

    public DoubleFilter getPrixPropose() {
        return prixPropose;
    }

    public Optional<DoubleFilter> optionalPrixPropose() {
        return Optional.ofNullable(prixPropose);
    }

    public DoubleFilter prixPropose() {
        if (prixPropose == null) {
            setPrixPropose(new DoubleFilter());
        }
        return prixPropose;
    }

    public void setPrixPropose(DoubleFilter prixPropose) {
        this.prixPropose = prixPropose;
    }

    public InstantFilter getDateCreation() {
        return dateCreation;
    }

    public Optional<InstantFilter> optionalDateCreation() {
        return Optional.ofNullable(dateCreation);
    }

    public InstantFilter dateCreation() {
        if (dateCreation == null) {
            setDateCreation(new InstantFilter());
        }
        return dateCreation;
    }

    public void setDateCreation(InstantFilter dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LongFilter getPaiementId() {
        return paiementId;
    }

    public Optional<LongFilter> optionalPaiementId() {
        return Optional.ofNullable(paiementId);
    }

    public LongFilter paiementId() {
        if (paiementId == null) {
            setPaiementId(new LongFilter());
        }
        return paiementId;
    }

    public void setPaiementId(LongFilter paiementId) {
        this.paiementId = paiementId;
    }

    public LongFilter getServiceJefId() {
        return serviceJefId;
    }

    public Optional<LongFilter> optionalServiceJefId() {
        return Optional.ofNullable(serviceJefId);
    }

    public LongFilter serviceJefId() {
        if (serviceJefId == null) {
            setServiceJefId(new LongFilter());
        }
        return serviceJefId;
    }

    public void setServiceJefId(LongFilter serviceJefId) {
        this.serviceJefId = serviceJefId;
    }

    public LongFilter getClientId() {
        return clientId;
    }

    public Optional<LongFilter> optionalClientId() {
        return Optional.ofNullable(clientId);
    }

    public LongFilter clientId() {
        if (clientId == null) {
            setClientId(new LongFilter());
        }
        return clientId;
    }

    public void setClientId(LongFilter clientId) {
        this.clientId = clientId;
    }

    public LongFilter getTechnicienId() {
        return technicienId;
    }

    public Optional<LongFilter> optionalTechnicienId() {
        return Optional.ofNullable(technicienId);
    }

    public LongFilter technicienId() {
        if (technicienId == null) {
            setTechnicienId(new LongFilter());
        }
        return technicienId;
    }

    public void setTechnicienId(LongFilter technicienId) {
        this.technicienId = technicienId;
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
        final MissionCriteria that = (MissionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(description, that.description) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(longitude, that.longitude) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(prixPropose, that.prixPropose) &&
            Objects.equals(dateCreation, that.dateCreation) &&
            Objects.equals(paiementId, that.paiementId) &&
            Objects.equals(serviceJefId, that.serviceJefId) &&
            Objects.equals(clientId, that.clientId) &&
            Objects.equals(technicienId, that.technicienId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            description,
            latitude,
            longitude,
            statut,
            prixPropose,
            dateCreation,
            paiementId,
            serviceJefId,
            clientId,
            technicienId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MissionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalLatitude().map(f -> "latitude=" + f + ", ").orElse("") +
            optionalLongitude().map(f -> "longitude=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalPrixPropose().map(f -> "prixPropose=" + f + ", ").orElse("") +
            optionalDateCreation().map(f -> "dateCreation=" + f + ", ").orElse("") +
            optionalPaiementId().map(f -> "paiementId=" + f + ", ").orElse("") +
            optionalServiceJefId().map(f -> "serviceJefId=" + f + ", ").orElse("") +
            optionalClientId().map(f -> "clientId=" + f + ", ").orElse("") +
            optionalTechnicienId().map(f -> "technicienId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
