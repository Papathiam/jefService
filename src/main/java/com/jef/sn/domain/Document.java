package com.jef.sn.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jef.sn.domain.enumeration.TypeDocument;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Document.
 */
@Entity
@Table(name = "document")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Document implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeDocument type;

    @Lob
    @Column(name = "file_data", nullable = false)
    private byte[] fileData;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "valide")
    private Boolean valide;

    @Column(name = "date_upload")
    private Instant dateUpload;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "documents", "missions", "serviceJefs" }, allowSetters = true)
    private Technicien technicien;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Document id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeDocument getType() {
        return this.type;
    }

    public Document type(TypeDocument type) {
        this.setType(type);
        return this;
    }

    public void setType(TypeDocument type) {
        this.type = type;
    }

    public Boolean getValide() {
        return this.valide;
    }

    public Document valide(Boolean valide) {
        this.setValide(valide);
        return this;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setValide(Boolean valide) {
        this.valide = valide;
    }

    public Instant getDateUpload() {
        return this.dateUpload;
    }

    public Document dateUpload(Instant dateUpload) {
        this.setDateUpload(dateUpload);
        return this;
    }

    public void setDateUpload(Instant dateUpload) {
        this.dateUpload = dateUpload;
    }

    public Technicien getTechnicien() {
        return this.technicien;
    }

    public void setTechnicien(Technicien technicien) {
        this.technicien = technicien;
    }

    public Document technicien(Technicien technicien) {
        this.setTechnicien(technicien);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Document)) {
            return false;
        }
        return getId() != null && getId().equals(((Document) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Document{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", valide='" + getValide() + "'" +
            ", dateUpload='" + getDateUpload() + "'" +
            "}";
    }
}
