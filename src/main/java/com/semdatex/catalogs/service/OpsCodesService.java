package com.semdatex.catalogs.service;

import com.semdatex.catalogs.service.dto.OpsCodesDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.semdatex.catalogs.domain.OpsCodes}.
 */
public interface OpsCodesService {
    /**
     * Save a opsCodes.
     *
     * @param opsCodesDTO the entity to save.
     * @return the persisted entity.
     */
    OpsCodesDTO save(OpsCodesDTO opsCodesDTO);

    /**
     * Updates a opsCodes.
     *
     * @param opsCodesDTO the entity to update.
     * @return the persisted entity.
     */
    OpsCodesDTO update(OpsCodesDTO opsCodesDTO);

    /**
     * Partially updates a opsCodes.
     *
     * @param opsCodesDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<OpsCodesDTO> partialUpdate(OpsCodesDTO opsCodesDTO);

    /**
     * Get the "id" opsCodes.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OpsCodesDTO> findOne(Long id);

    /**
     * Delete the "id" opsCodes.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
