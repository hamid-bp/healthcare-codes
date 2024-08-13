package com.semdatex.catalogs.web.rest;

import com.semdatex.catalogs.repository.OpsCodesRepository;
import com.semdatex.catalogs.service.OpsCodesQueryService;
import com.semdatex.catalogs.service.OpsCodesService;
import com.semdatex.catalogs.service.criteria.OpsCodesCriteria;
import com.semdatex.catalogs.service.dto.OpsCodesDTO;
import com.semdatex.catalogs.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.semdatex.catalogs.domain.OpsCodes}.
 */
@RestController
@RequestMapping("/api/ops-codes")
public class OpsCodesResource {

    private static final Logger log = LoggerFactory.getLogger(OpsCodesResource.class);

    private static final String ENTITY_NAME = "opsCodes";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OpsCodesService opsCodesService;

    private final OpsCodesRepository opsCodesRepository;

    private final OpsCodesQueryService opsCodesQueryService;

    public OpsCodesResource(
        OpsCodesService opsCodesService,
        OpsCodesRepository opsCodesRepository,
        OpsCodesQueryService opsCodesQueryService
    ) {
        this.opsCodesService = opsCodesService;
        this.opsCodesRepository = opsCodesRepository;
        this.opsCodesQueryService = opsCodesQueryService;
    }

    /**
     * {@code POST  /ops-codes} : Create a new opsCodes.
     *
     * @param opsCodesDTO the opsCodesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new opsCodesDTO, or with status {@code 400 (Bad Request)} if the opsCodes has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OpsCodesDTO> createOpsCodes(@RequestBody OpsCodesDTO opsCodesDTO) throws URISyntaxException {
        log.debug("REST request to save OpsCodes : {}", opsCodesDTO);
        if (opsCodesDTO.getId() != null) {
            throw new BadRequestAlertException("A new opsCodes cannot already have an ID", ENTITY_NAME, "idexists");
        }
        opsCodesDTO = opsCodesService.save(opsCodesDTO);
        return ResponseEntity.created(new URI("/api/ops-codes/" + opsCodesDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, opsCodesDTO.getId().toString()))
            .body(opsCodesDTO);
    }

    /**
     * {@code PUT  /ops-codes/:id} : Updates an existing opsCodes.
     *
     * @param id the id of the opsCodesDTO to save.
     * @param opsCodesDTO the opsCodesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated opsCodesDTO,
     * or with status {@code 400 (Bad Request)} if the opsCodesDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the opsCodesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OpsCodesDTO> updateOpsCodes(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OpsCodesDTO opsCodesDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OpsCodes : {}, {}", id, opsCodesDTO);
        if (opsCodesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, opsCodesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!opsCodesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        opsCodesDTO = opsCodesService.update(opsCodesDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, opsCodesDTO.getId().toString()))
            .body(opsCodesDTO);
    }

    /**
     * {@code PATCH  /ops-codes/:id} : Partial updates given fields of an existing opsCodes, field will ignore if it is null
     *
     * @param id the id of the opsCodesDTO to save.
     * @param opsCodesDTO the opsCodesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated opsCodesDTO,
     * or with status {@code 400 (Bad Request)} if the opsCodesDTO is not valid,
     * or with status {@code 404 (Not Found)} if the opsCodesDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the opsCodesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OpsCodesDTO> partialUpdateOpsCodes(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OpsCodesDTO opsCodesDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OpsCodes partially : {}, {}", id, opsCodesDTO);
        if (opsCodesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, opsCodesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!opsCodesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OpsCodesDTO> result = opsCodesService.partialUpdate(opsCodesDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, opsCodesDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ops-codes} : get all the opsCodes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of opsCodes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<OpsCodesDTO>> getAllOpsCodes(
        OpsCodesCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get OpsCodes by criteria: {}", criteria);

        Page<OpsCodesDTO> page = opsCodesQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ops-codes/count} : count all the opsCodes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countOpsCodes(OpsCodesCriteria criteria) {
        log.debug("REST request to count OpsCodes by criteria: {}", criteria);
        return ResponseEntity.ok().body(opsCodesQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ops-codes/:id} : get the "id" opsCodes.
     *
     * @param id the id of the opsCodesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the opsCodesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OpsCodesDTO> getOpsCodes(@PathVariable("id") Long id) {
        log.debug("REST request to get OpsCodes : {}", id);
        Optional<OpsCodesDTO> opsCodesDTO = opsCodesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(opsCodesDTO);
    }

    /**
     * {@code DELETE  /ops-codes/:id} : delete the "id" opsCodes.
     *
     * @param id the id of the opsCodesDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOpsCodes(@PathVariable("id") Long id) {
        log.debug("REST request to delete OpsCodes : {}", id);
        opsCodesService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
