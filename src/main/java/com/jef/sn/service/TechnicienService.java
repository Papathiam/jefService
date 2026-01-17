package com.jef.sn.service;

import com.jef.sn.service.dto.TechnicienDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.jef.sn.domain.Technicien}.
 */
public interface TechnicienService {
    /**
     * Save a technicien.
     *
     * @param technicienDTO the entity to save.
     * @return the persisted entity.
     */
    TechnicienDTO save(TechnicienDTO technicienDTO);

    /**
     * Updates a technicien.
     *
     * @param technicienDTO the entity to update.
     * @return the persisted entity.
     */
    TechnicienDTO update(TechnicienDTO technicienDTO);

    /**
     * Partially updates a technicien.
     *
     * @param technicienDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TechnicienDTO> partialUpdate(TechnicienDTO technicienDTO);

    /**
     * Get all the techniciens with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TechnicienDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" technicien.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TechnicienDTO> findOne(Long id);

    /**
     * Delete the "id" technicien.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
