package com.jef.sn.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.jef.sn.domain.ServiceJef} entity. This class is used
 * in {@link com.jef.sn.web.rest.ServiceJefResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /service-jefs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServiceJefCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nom;

    private StringFilter description;

    private BooleanFilter actif;

    private LongFilter missionsId;

    private LongFilter techniciensId;

    private Boolean distinct;

    public ServiceJefCriteria() {}

    public ServiceJefCriteria(ServiceJefCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nom = other.optionalNom().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.actif = other.optionalActif().map(BooleanFilter::copy).orElse(null);
        this.missionsId = other.optionalMissionsId().map(LongFilter::copy).orElse(null);
        this.techniciensId = other.optionalTechniciensId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ServiceJefCriteria copy() {
        return new ServiceJefCriteria(this);
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

    public BooleanFilter getActif() {
        return actif;
    }

    public Optional<BooleanFilter> optionalActif() {
        return Optional.ofNullable(actif);
    }

    public BooleanFilter actif() {
        if (actif == null) {
            setActif(new BooleanFilter());
        }
        return actif;
    }

    public void setActif(BooleanFilter actif) {
        this.actif = actif;
    }

    public LongFilter getMissionsId() {
        return missionsId;
    }

    public Optional<LongFilter> optionalMissionsId() {
        return Optional.ofNullable(missionsId);
    }

    public LongFilter missionsId() {
        if (missionsId == null) {
            setMissionsId(new LongFilter());
        }
        return missionsId;
    }

    public void setMissionsId(LongFilter missionsId) {
        this.missionsId = missionsId;
    }

    public LongFilter getTechniciensId() {
        return techniciensId;
    }

    public Optional<LongFilter> optionalTechniciensId() {
        return Optional.ofNullable(techniciensId);
    }

    public LongFilter techniciensId() {
        if (techniciensId == null) {
            setTechniciensId(new LongFilter());
        }
        return techniciensId;
    }

    public void setTechniciensId(LongFilter techniciensId) {
        this.techniciensId = techniciensId;
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
        final ServiceJefCriteria that = (ServiceJefCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nom, that.nom) &&
            Objects.equals(description, that.description) &&
            Objects.equals(actif, that.actif) &&
            Objects.equals(missionsId, that.missionsId) &&
            Objects.equals(techniciensId, that.techniciensId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, description, actif, missionsId, techniciensId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServiceJefCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNom().map(f -> "nom=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalActif().map(f -> "actif=" + f + ", ").orElse("") +
            optionalMissionsId().map(f -> "missionsId=" + f + ", ").orElse("") +
            optionalTechniciensId().map(f -> "techniciensId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
