package com.jef.sn.service;

import com.jef.sn.domain.Mission;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.jef.sn.domain.Mission}.
 */
public interface MissionService {
    /**
     * Save a mission.
     *
     * @param mission the entity to save.
     * @return the persisted entity.
     */
    Mission save(Mission mission);

    /**
     * Updates a mission.
     *
     * @param mission the entity to update.
     * @return the persisted entity.
     */
    Mission update(Mission mission);

    /**
     * Partially updates a mission.
     *
     * @param mission the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Mission> partialUpdate(Mission mission);

    /**
     * Get the "id" mission.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Mission> findOne(Long id);

    /**
     * Delete the "id" mission.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
