package com.jef.sn.web.rest;

import com.jef.sn.domain.Mission;
import com.jef.sn.repository.MissionRepository;
import com.jef.sn.service.MissionQueryService;
import com.jef.sn.service.MissionService;
import com.jef.sn.service.criteria.MissionCriteria;
import com.jef.sn.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.jef.sn.domain.Mission}.
 */
@RestController
@RequestMapping("/api/missions")
public class MissionResource {

    private static final Logger LOG = LoggerFactory.getLogger(MissionResource.class);

    private static final String ENTITY_NAME = "mission";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MissionService missionService;

    private final MissionRepository missionRepository;

    private final MissionQueryService missionQueryService;

    public MissionResource(MissionService missionService, MissionRepository missionRepository, MissionQueryService missionQueryService) {
        this.missionService = missionService;
        this.missionRepository = missionRepository;
        this.missionQueryService = missionQueryService;
    }

    /**
     * {@code POST  /missions} : Create a new mission.
     *
     * @param mission the mission to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mission, or with status {@code 400 (Bad Request)} if the mission has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Mission> createMission(@RequestBody Mission mission) throws URISyntaxException {
        LOG.debug("REST request to save Mission : {}", mission);
        if (mission.getId() != null) {
            throw new BadRequestAlertException("A new mission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        mission = missionService.save(mission);
        return ResponseEntity.created(new URI("/api/missions/" + mission.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, mission.getId().toString()))
            .body(mission);
    }

    /**
     * {@code PUT  /missions/:id} : Updates an existing mission.
     *
     * @param id the id of the mission to save.
     * @param mission the mission to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mission,
     * or with status {@code 400 (Bad Request)} if the mission is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mission couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Mission> updateMission(@PathVariable(value = "id", required = false) final Long id, @RequestBody Mission mission)
        throws URISyntaxException {
        LOG.debug("REST request to update Mission : {}, {}", id, mission);
        if (mission.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mission.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!missionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        mission = missionService.update(mission);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mission.getId().toString()))
            .body(mission);
    }

    /**
     * {@code PATCH  /missions/:id} : Partial updates given fields of an existing mission, field will ignore if it is null
     *
     * @param id the id of the mission to save.
     * @param mission the mission to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mission,
     * or with status {@code 400 (Bad Request)} if the mission is not valid,
     * or with status {@code 404 (Not Found)} if the mission is not found,
     * or with status {@code 500 (Internal Server Error)} if the mission couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Mission> partialUpdateMission(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Mission mission
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Mission partially : {}, {}", id, mission);
        if (mission.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mission.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!missionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Mission> result = missionService.partialUpdate(mission);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mission.getId().toString())
        );
    }

    /**
     * {@code GET  /missions} : get all the missions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of missions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Mission>> getAllMissions(
        MissionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Missions by criteria: {}", criteria);

        Page<Mission> page = missionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /missions/count} : count all the missions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countMissions(MissionCriteria criteria) {
        LOG.debug("REST request to count Missions by criteria: {}", criteria);
        return ResponseEntity.ok().body(missionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /missions/:id} : get the "id" mission.
     *
     * @param id the id of the mission to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mission, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mission> getMission(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Mission : {}", id);
        Optional<Mission> mission = missionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mission);
    }

    /**
     * {@code DELETE  /missions/:id} : delete the "id" mission.
     *
     * @param id the id of the mission to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Mission : {}", id);
        missionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
