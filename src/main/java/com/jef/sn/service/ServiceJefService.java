package com.jef.sn.service;

import com.jef.sn.service.dto.ServiceJefDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.jef.sn.domain.ServiceJef}.
 */
public interface ServiceJefService {
    /**
     * Save a serviceJef.
     *
     * @param serviceJefDTO the entity to save.
     * @return the persisted entity.
     */
    ServiceJefDTO save(ServiceJefDTO serviceJefDTO);

    /**
     * Updates a serviceJef.
     *
     * @param serviceJefDTO the entity to update.
     * @return the persisted entity.
     */
    ServiceJefDTO update(ServiceJefDTO serviceJefDTO);

    /**
     * Partially updates a serviceJef.
     *
     * @param serviceJefDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ServiceJefDTO> partialUpdate(ServiceJefDTO serviceJefDTO);

    /**
     * Get the "id" serviceJef.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ServiceJefDTO> findOne(Long id);

    /**
     * Delete the "id" serviceJef.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
