package com.jef.sn.web.rest;

import com.jef.sn.repository.ServiceJefRepository;
import com.jef.sn.service.ServiceJefQueryService;
import com.jef.sn.service.ServiceJefService;
import com.jef.sn.service.criteria.ServiceJefCriteria;
import com.jef.sn.service.dto.ServiceJefDTO;
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
 * REST controller for managing {@link com.jef.sn.domain.ServiceJef}.
 */
@RestController
@RequestMapping("/api/service-jefs")
public class ServiceJefResource {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceJefResource.class);

    private static final String ENTITY_NAME = "serviceJef";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ServiceJefService serviceJefService;

    private final ServiceJefRepository serviceJefRepository;

    private final ServiceJefQueryService serviceJefQueryService;

    public ServiceJefResource(
        ServiceJefService serviceJefService,
        ServiceJefRepository serviceJefRepository,
        ServiceJefQueryService serviceJefQueryService
    ) {
        this.serviceJefService = serviceJefService;
        this.serviceJefRepository = serviceJefRepository;
        this.serviceJefQueryService = serviceJefQueryService;
    }

    /**
     * {@code POST  /service-jefs} : Create a new serviceJef.
     *
     * @param serviceJefDTO the serviceJefDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new serviceJefDTO, or with status {@code 400 (Bad Request)} if the serviceJef has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ServiceJefDTO> createServiceJef(@Valid @RequestBody ServiceJefDTO serviceJefDTO) throws URISyntaxException {
        LOG.debug("REST request to save ServiceJef : {}", serviceJefDTO);
        if (serviceJefDTO.getId() != null) {
            throw new BadRequestAlertException("A new serviceJef cannot already have an ID", ENTITY_NAME, "idexists");
        }
        serviceJefDTO = serviceJefService.save(serviceJefDTO);
        return ResponseEntity.created(new URI("/api/service-jefs/" + serviceJefDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, serviceJefDTO.getId().toString()))
            .body(serviceJefDTO);
    }

    /**
     * {@code PUT  /service-jefs/:id} : Updates an existing serviceJef.
     *
     * @param id the id of the serviceJefDTO to save.
     * @param serviceJefDTO the serviceJefDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceJefDTO,
     * or with status {@code 400 (Bad Request)} if the serviceJefDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the serviceJefDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServiceJefDTO> updateServiceJef(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ServiceJefDTO serviceJefDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ServiceJef : {}, {}", id, serviceJefDTO);
        if (serviceJefDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serviceJefDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!serviceJefRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        serviceJefDTO = serviceJefService.update(serviceJefDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serviceJefDTO.getId().toString()))
            .body(serviceJefDTO);
    }

    /**
     * {@code PATCH  /service-jefs/:id} : Partial updates given fields of an existing serviceJef, field will ignore if it is null
     *
     * @param id the id of the serviceJefDTO to save.
     * @param serviceJefDTO the serviceJefDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceJefDTO,
     * or with status {@code 400 (Bad Request)} if the serviceJefDTO is not valid,
     * or with status {@code 404 (Not Found)} if the serviceJefDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the serviceJefDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ServiceJefDTO> partialUpdateServiceJef(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ServiceJefDTO serviceJefDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ServiceJef partially : {}, {}", id, serviceJefDTO);
        if (serviceJefDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serviceJefDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!serviceJefRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ServiceJefDTO> result = serviceJefService.partialUpdate(serviceJefDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serviceJefDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /service-jefs} : get all the serviceJefs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of serviceJefs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ServiceJefDTO>> getAllServiceJefs(
        ServiceJefCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ServiceJefs by criteria: {}", criteria);

        Page<ServiceJefDTO> page = serviceJefQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /service-jefs/count} : count all the serviceJefs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countServiceJefs(ServiceJefCriteria criteria) {
        LOG.debug("REST request to count ServiceJefs by criteria: {}", criteria);
        return ResponseEntity.ok().body(serviceJefQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /service-jefs/:id} : get the "id" serviceJef.
     *
     * @param id the id of the serviceJefDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the serviceJefDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceJefDTO> getServiceJef(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ServiceJef : {}", id);
        Optional<ServiceJefDTO> serviceJefDTO = serviceJefService.findOne(id);
        return ResponseUtil.wrapOrNotFound(serviceJefDTO);
    }

    /**
     * {@code DELETE  /service-jefs/:id} : delete the "id" serviceJef.
     *
     * @param id the id of the serviceJefDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceJef(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ServiceJef : {}", id);
        serviceJefService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
