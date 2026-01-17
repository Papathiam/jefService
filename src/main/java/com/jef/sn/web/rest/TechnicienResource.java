package com.jef.sn.web.rest;

import com.jef.sn.repository.TechnicienRepository;
import com.jef.sn.service.TechnicienQueryService;
import com.jef.sn.service.TechnicienService;
import com.jef.sn.service.criteria.TechnicienCriteria;
import com.jef.sn.service.dto.TechnicienDTO;
import com.jef.sn.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
 * REST controller for managing {@link com.jef.sn.domain.Technicien}.
 */
@RestController
@RequestMapping("/api/techniciens")
public class TechnicienResource {

    private static final Logger LOG = LoggerFactory.getLogger(TechnicienResource.class);

    private static final String ENTITY_NAME = "technicien";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TechnicienService technicienService;

    private final TechnicienRepository technicienRepository;

    private final TechnicienQueryService technicienQueryService;

    public TechnicienResource(
        TechnicienService technicienService,
        TechnicienRepository technicienRepository,
        TechnicienQueryService technicienQueryService
    ) {
        this.technicienService = technicienService;
        this.technicienRepository = technicienRepository;
        this.technicienQueryService = technicienQueryService;
    }

    /**
     * {@code POST  /techniciens} : Create a new technicien.
     *
     * @param technicienDTO the technicienDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new technicienDTO, or with status {@code 400 (Bad Request)} if the technicien has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TechnicienDTO> createTechnicien(@Valid @RequestBody TechnicienDTO technicienDTO) throws URISyntaxException {
        LOG.debug("REST request to save Technicien : {}", technicienDTO);
        if (technicienDTO.getId() != null) {
            throw new BadRequestAlertException("A new technicien cannot already have an ID", ENTITY_NAME, "idexists");
        }
        technicienDTO = technicienService.save(technicienDTO);
        return ResponseEntity.created(new URI("/api/techniciens/" + technicienDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, technicienDTO.getId().toString()))
            .body(technicienDTO);
    }

    /**
     * {@code PUT  /techniciens/:id} : Updates an existing technicien.
     *
     * @param id the id of the technicienDTO to save.
     * @param technicienDTO the technicienDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated technicienDTO,
     * or with status {@code 400 (Bad Request)} if the technicienDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the technicienDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TechnicienDTO> updateTechnicien(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TechnicienDTO technicienDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Technicien : {}, {}", id, technicienDTO);
        if (technicienDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, technicienDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!technicienRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        technicienDTO = technicienService.update(technicienDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, technicienDTO.getId().toString()))
            .body(technicienDTO);
    }

    /**
     * {@code PATCH  /techniciens/:id} : Partial updates given fields of an existing technicien, field will ignore if it is null
     *
     * @param id the id of the technicienDTO to save.
     * @param technicienDTO the technicienDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated technicienDTO,
     * or with status {@code 400 (Bad Request)} if the technicienDTO is not valid,
     * or with status {@code 404 (Not Found)} if the technicienDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the technicienDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TechnicienDTO> partialUpdateTechnicien(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TechnicienDTO technicienDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Technicien partially : {}, {}", id, technicienDTO);
        if (technicienDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, technicienDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!technicienRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TechnicienDTO> result = technicienService.partialUpdate(technicienDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, technicienDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /techniciens} : get all the techniciens.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of techniciens in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TechnicienDTO>> getAllTechniciens(
        TechnicienCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Techniciens by criteria: {}", criteria);

        Page<TechnicienDTO> page = technicienQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /techniciens/count} : count all the techniciens.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTechniciens(TechnicienCriteria criteria) {
        LOG.debug("REST request to count Techniciens by criteria: {}", criteria);
        return ResponseEntity.ok().body(technicienQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /techniciens/:id} : get the "id" technicien.
     *
     * @param id the id of the technicienDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the technicienDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TechnicienDTO> getTechnicien(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Technicien : {}", id);
        Optional<TechnicienDTO> technicienDTO = technicienService.findOne(id);
        return ResponseUtil.wrapOrNotFound(technicienDTO);
    }

    /**
     * {@code DELETE  /techniciens/:id} : delete the "id" technicien.
     *
     * @param id the id of the technicienDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTechnicien(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Technicien : {}", id);
        technicienService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
