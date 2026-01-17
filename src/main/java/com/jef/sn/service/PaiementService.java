package com.jef.sn.service;

import com.jef.sn.domain.Paiement;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.jef.sn.domain.Paiement}.
 */
public interface PaiementService {
    /**
     * Save a paiement.
     *
     * @param paiement the entity to save.
     * @return the persisted entity.
     */
    Paiement save(Paiement paiement);

    /**
     * Updates a paiement.
     *
     * @param paiement the entity to update.
     * @return the persisted entity.
     */
    Paiement update(Paiement paiement);

    /**
     * Partially updates a paiement.
     *
     * @param paiement the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Paiement> partialUpdate(Paiement paiement);

    /**
     * Get all the Paiement where Mission is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<Paiement> findAllWhereMissionIsNull();

    /**
     * Get the "id" paiement.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Paiement> findOne(Long id);

    /**
     * Delete the "id" paiement.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
