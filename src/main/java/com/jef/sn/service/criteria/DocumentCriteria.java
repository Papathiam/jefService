package com.jef.sn.service.criteria;

import com.jef.sn.domain.enumeration.TypeDocument;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.jef.sn.domain.Document} entity. This class is used
 * in {@link com.jef.sn.web.rest.DocumentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /documents?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DocumentCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TypeDocument
     */
    public static class TypeDocumentFilter extends Filter<TypeDocument> {

        public TypeDocumentFilter() {}

        public TypeDocumentFilter(TypeDocumentFilter filter) {
            super(filter);
        }

        @Override
        public TypeDocumentFilter copy() {
            return new TypeDocumentFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private TypeDocumentFilter type;

    private StringFilter urlFichier;

    private BooleanFilter valide;

    private InstantFilter dateUpload;

    private LongFilter technicienId;

    private Boolean distinct;

    public DocumentCriteria() {}

    public DocumentCriteria(DocumentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.type = other.optionalType().map(TypeDocumentFilter::copy).orElse(null);
        this.urlFichier = other.optionalUrlFichier().map(StringFilter::copy).orElse(null);
        this.valide = other.optionalValide().map(BooleanFilter::copy).orElse(null);
        this.dateUpload = other.optionalDateUpload().map(InstantFilter::copy).orElse(null);
        this.technicienId = other.optionalTechnicienId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DocumentCriteria copy() {
        return new DocumentCriteria(this);
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

    public TypeDocumentFilter getType() {
        return type;
    }

    public Optional<TypeDocumentFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public TypeDocumentFilter type() {
        if (type == null) {
            setType(new TypeDocumentFilter());
        }
        return type;
    }

    public void setType(TypeDocumentFilter type) {
        this.type = type;
    }

    public StringFilter getUrlFichier() {
        return urlFichier;
    }

    public Optional<StringFilter> optionalUrlFichier() {
        return Optional.ofNullable(urlFichier);
    }

    public StringFilter urlFichier() {
        if (urlFichier == null) {
            setUrlFichier(new StringFilter());
        }
        return urlFichier;
    }

    public void setUrlFichier(StringFilter urlFichier) {
        this.urlFichier = urlFichier;
    }

    public BooleanFilter getValide() {
        return valide;
    }

    public Optional<BooleanFilter> optionalValide() {
        return Optional.ofNullable(valide);
    }

    public BooleanFilter valide() {
        if (valide == null) {
            setValide(new BooleanFilter());
        }
        return valide;
    }

    public void setValide(BooleanFilter valide) {
        this.valide = valide;
    }

    public InstantFilter getDateUpload() {
        return dateUpload;
    }

    public Optional<InstantFilter> optionalDateUpload() {
        return Optional.ofNullable(dateUpload);
    }

    public InstantFilter dateUpload() {
        if (dateUpload == null) {
            setDateUpload(new InstantFilter());
        }
        return dateUpload;
    }

    public void setDateUpload(InstantFilter dateUpload) {
        this.dateUpload = dateUpload;
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
        final DocumentCriteria that = (DocumentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(type, that.type) &&
            Objects.equals(urlFichier, that.urlFichier) &&
            Objects.equals(valide, that.valide) &&
            Objects.equals(dateUpload, that.dateUpload) &&
            Objects.equals(technicienId, that.technicienId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, urlFichier, valide, dateUpload, technicienId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DocumentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalUrlFichier().map(f -> "urlFichier=" + f + ", ").orElse("") +
            optionalValide().map(f -> "valide=" + f + ", ").orElse("") +
            optionalDateUpload().map(f -> "dateUpload=" + f + ", ").orElse("") +
            optionalTechnicienId().map(f -> "technicienId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
