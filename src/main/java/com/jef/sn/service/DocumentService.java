package com.jef.sn.service;

import com.jef.sn.domain.*;
import com.jef.sn.domain.enumeration.*;
import java.util.*;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Interface for managing {@link com.jef.sn.domain.Document}.
 */
public interface DocumentService {
    /**
     * Save a document.
     *
     * @param document the entity to save.
     * @return the persisted entity.
     */
    Document save(Document document);

    /**
     * Updates a document.
     *
     * @param document the entity to update.
     * @return the persisted entity.
     */
    Document update(Document document);

    /**
     * Partially updates a document.
     *
     * @param document the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Document> partialUpdate(Document document);

    /**
     * Get the "id" document.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Document> findOne(Long id);

    /**
     * Delete the "id" document.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    // ------------------ NOUVELLE MÉTHODE ------------------
    Document saveFile(MultipartFile file, Technicien technicien, TypeDocument type);
}
