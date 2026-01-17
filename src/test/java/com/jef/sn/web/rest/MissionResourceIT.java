package com.jef.sn.web.rest;

import static com.jef.sn.domain.MissionAsserts.*;
import static com.jef.sn.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jef.sn.IntegrationTest;
import com.jef.sn.domain.Client;
import com.jef.sn.domain.Mission;
import com.jef.sn.domain.Paiement;
import com.jef.sn.domain.ServiceJef;
import com.jef.sn.domain.Technicien;
import com.jef.sn.domain.enumeration.StatutMission;
import com.jef.sn.repository.MissionRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link MissionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MissionResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;
    private static final Double SMALLER_LATITUDE = 1D - 1D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;
    private static final Double SMALLER_LONGITUDE = 1D - 1D;

    private static final StatutMission DEFAULT_STATUT = StatutMission.EN_ATTENTE;
    private static final StatutMission UPDATED_STATUT = StatutMission.ACCEPTEE;

    private static final Double DEFAULT_PRIX_PROPOSE = 1D;
    private static final Double UPDATED_PRIX_PROPOSE = 2D;
    private static final Double SMALLER_PRIX_PROPOSE = 1D - 1D;

    private static final Instant DEFAULT_DATE_CREATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CREATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/missions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMissionMockMvc;

    private Mission mission;

    private Mission insertedMission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mission createEntity() {
        return new Mission()
            .description(DEFAULT_DESCRIPTION)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .statut(DEFAULT_STATUT)
            .prixPropose(DEFAULT_PRIX_PROPOSE)
            .dateCreation(DEFAULT_DATE_CREATION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mission createUpdatedEntity() {
        return new Mission()
            .description(UPDATED_DESCRIPTION)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .statut(UPDATED_STATUT)
            .prixPropose(UPDATED_PRIX_PROPOSE)
            .dateCreation(UPDATED_DATE_CREATION);
    }

    @BeforeEach
    void initTest() {
        mission = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMission != null) {
            missionRepository.delete(insertedMission);
            insertedMission = null;
        }
    }

    @Test
    @Transactional
    void createMission() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Mission
        var returnedMission = om.readValue(
            restMissionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mission)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Mission.class
        );

        // Validate the Mission in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMissionUpdatableFieldsEquals(returnedMission, getPersistedMission(returnedMission));

        insertedMission = returnedMission;
    }

    @Test
    @Transactional
    void createMissionWithExistingId() throws Exception {
        // Create the Mission with an existing ID
        mission.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mission)))
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllMissions() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList
        restMissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mission.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].prixPropose").value(hasItem(DEFAULT_PRIX_PROPOSE)))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())));
    }

    @Test
    @Transactional
    void getMission() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get the mission
        restMissionMockMvc
            .perform(get(ENTITY_API_URL_ID, mission.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(mission.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()))
            .andExpect(jsonPath("$.prixPropose").value(DEFAULT_PRIX_PROPOSE))
            .andExpect(jsonPath("$.dateCreation").value(DEFAULT_DATE_CREATION.toString()));
    }

    @Test
    @Transactional
    void getMissionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        Long id = mission.getId();

        defaultMissionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMissionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMissionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMissionsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where description equals to
        defaultMissionFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMissionsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where description in
        defaultMissionFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllMissionsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where description is not null
        defaultMissionFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllMissionsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where description contains
        defaultMissionFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMissionsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where description does not contain
        defaultMissionFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMissionsByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where latitude equals to
        defaultMissionFiltering("latitude.equals=" + DEFAULT_LATITUDE, "latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllMissionsByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where latitude in
        defaultMissionFiltering("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE, "latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllMissionsByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where latitude is not null
        defaultMissionFiltering("latitude.specified=true", "latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllMissionsByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where latitude is greater than or equal to
        defaultMissionFiltering("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE, "latitude.greaterThanOrEqual=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllMissionsByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where latitude is less than or equal to
        defaultMissionFiltering("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE, "latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllMissionsByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where latitude is less than
        defaultMissionFiltering("latitude.lessThan=" + UPDATED_LATITUDE, "latitude.lessThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllMissionsByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where latitude is greater than
        defaultMissionFiltering("latitude.greaterThan=" + SMALLER_LATITUDE, "latitude.greaterThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllMissionsByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where longitude equals to
        defaultMissionFiltering("longitude.equals=" + DEFAULT_LONGITUDE, "longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllMissionsByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where longitude in
        defaultMissionFiltering("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE, "longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllMissionsByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where longitude is not null
        defaultMissionFiltering("longitude.specified=true", "longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllMissionsByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where longitude is greater than or equal to
        defaultMissionFiltering("longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.greaterThanOrEqual=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllMissionsByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where longitude is less than or equal to
        defaultMissionFiltering("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllMissionsByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where longitude is less than
        defaultMissionFiltering("longitude.lessThan=" + UPDATED_LONGITUDE, "longitude.lessThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllMissionsByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where longitude is greater than
        defaultMissionFiltering("longitude.greaterThan=" + SMALLER_LONGITUDE, "longitude.greaterThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllMissionsByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where statut equals to
        defaultMissionFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllMissionsByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where statut in
        defaultMissionFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllMissionsByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where statut is not null
        defaultMissionFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllMissionsByPrixProposeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where prixPropose equals to
        defaultMissionFiltering("prixPropose.equals=" + DEFAULT_PRIX_PROPOSE, "prixPropose.equals=" + UPDATED_PRIX_PROPOSE);
    }

    @Test
    @Transactional
    void getAllMissionsByPrixProposeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where prixPropose in
        defaultMissionFiltering(
            "prixPropose.in=" + DEFAULT_PRIX_PROPOSE + "," + UPDATED_PRIX_PROPOSE,
            "prixPropose.in=" + UPDATED_PRIX_PROPOSE
        );
    }

    @Test
    @Transactional
    void getAllMissionsByPrixProposeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where prixPropose is not null
        defaultMissionFiltering("prixPropose.specified=true", "prixPropose.specified=false");
    }

    @Test
    @Transactional
    void getAllMissionsByPrixProposeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where prixPropose is greater than or equal to
        defaultMissionFiltering(
            "prixPropose.greaterThanOrEqual=" + DEFAULT_PRIX_PROPOSE,
            "prixPropose.greaterThanOrEqual=" + UPDATED_PRIX_PROPOSE
        );
    }

    @Test
    @Transactional
    void getAllMissionsByPrixProposeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where prixPropose is less than or equal to
        defaultMissionFiltering(
            "prixPropose.lessThanOrEqual=" + DEFAULT_PRIX_PROPOSE,
            "prixPropose.lessThanOrEqual=" + SMALLER_PRIX_PROPOSE
        );
    }

    @Test
    @Transactional
    void getAllMissionsByPrixProposeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where prixPropose is less than
        defaultMissionFiltering("prixPropose.lessThan=" + UPDATED_PRIX_PROPOSE, "prixPropose.lessThan=" + DEFAULT_PRIX_PROPOSE);
    }

    @Test
    @Transactional
    void getAllMissionsByPrixProposeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where prixPropose is greater than
        defaultMissionFiltering("prixPropose.greaterThan=" + SMALLER_PRIX_PROPOSE, "prixPropose.greaterThan=" + DEFAULT_PRIX_PROPOSE);
    }

    @Test
    @Transactional
    void getAllMissionsByDateCreationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where dateCreation equals to
        defaultMissionFiltering("dateCreation.equals=" + DEFAULT_DATE_CREATION, "dateCreation.equals=" + UPDATED_DATE_CREATION);
    }

    @Test
    @Transactional
    void getAllMissionsByDateCreationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where dateCreation in
        defaultMissionFiltering(
            "dateCreation.in=" + DEFAULT_DATE_CREATION + "," + UPDATED_DATE_CREATION,
            "dateCreation.in=" + UPDATED_DATE_CREATION
        );
    }

    @Test
    @Transactional
    void getAllMissionsByDateCreationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList where dateCreation is not null
        defaultMissionFiltering("dateCreation.specified=true", "dateCreation.specified=false");
    }

    @Test
    @Transactional
    void getAllMissionsByPaiementIsEqualToSomething() throws Exception {
        Paiement paiement;
        if (TestUtil.findAll(em, Paiement.class).isEmpty()) {
            missionRepository.saveAndFlush(mission);
            paiement = PaiementResourceIT.createEntity();
        } else {
            paiement = TestUtil.findAll(em, Paiement.class).get(0);
        }
        em.persist(paiement);
        em.flush();
        mission.setPaiement(paiement);
        missionRepository.saveAndFlush(mission);
        Long paiementId = paiement.getId();
        // Get all the missionList where paiement equals to paiementId
        defaultMissionShouldBeFound("paiementId.equals=" + paiementId);

        // Get all the missionList where paiement equals to (paiementId + 1)
        defaultMissionShouldNotBeFound("paiementId.equals=" + (paiementId + 1));
    }

    @Test
    @Transactional
    void getAllMissionsByServiceJefIsEqualToSomething() throws Exception {
        ServiceJef serviceJef;
        if (TestUtil.findAll(em, ServiceJef.class).isEmpty()) {
            missionRepository.saveAndFlush(mission);
            serviceJef = ServiceJefResourceIT.createEntity();
        } else {
            serviceJef = TestUtil.findAll(em, ServiceJef.class).get(0);
        }
        em.persist(serviceJef);
        em.flush();
        mission.setServiceJef(serviceJef);
        missionRepository.saveAndFlush(mission);
        Long serviceJefId = serviceJef.getId();
        // Get all the missionList where serviceJef equals to serviceJefId
        defaultMissionShouldBeFound("serviceJefId.equals=" + serviceJefId);

        // Get all the missionList where serviceJef equals to (serviceJefId + 1)
        defaultMissionShouldNotBeFound("serviceJefId.equals=" + (serviceJefId + 1));
    }

    @Test
    @Transactional
    void getAllMissionsByClientIsEqualToSomething() throws Exception {
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            missionRepository.saveAndFlush(mission);
            client = ClientResourceIT.createEntity();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        em.persist(client);
        em.flush();
        mission.setClient(client);
        missionRepository.saveAndFlush(mission);
        Long clientId = client.getId();
        // Get all the missionList where client equals to clientId
        defaultMissionShouldBeFound("clientId.equals=" + clientId);

        // Get all the missionList where client equals to (clientId + 1)
        defaultMissionShouldNotBeFound("clientId.equals=" + (clientId + 1));
    }

    @Test
    @Transactional
    void getAllMissionsByTechnicienIsEqualToSomething() throws Exception {
        Technicien technicien;
        if (TestUtil.findAll(em, Technicien.class).isEmpty()) {
            missionRepository.saveAndFlush(mission);
            technicien = TechnicienResourceIT.createEntity();
        } else {
            technicien = TestUtil.findAll(em, Technicien.class).get(0);
        }
        em.persist(technicien);
        em.flush();
        mission.setTechnicien(technicien);
        missionRepository.saveAndFlush(mission);
        Long technicienId = technicien.getId();
        // Get all the missionList where technicien equals to technicienId
        defaultMissionShouldBeFound("technicienId.equals=" + technicienId);

        // Get all the missionList where technicien equals to (technicienId + 1)
        defaultMissionShouldNotBeFound("technicienId.equals=" + (technicienId + 1));
    }

    private void defaultMissionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultMissionShouldBeFound(shouldBeFound);
        defaultMissionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMissionShouldBeFound(String filter) throws Exception {
        restMissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mission.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].prixPropose").value(hasItem(DEFAULT_PRIX_PROPOSE)))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())));

        // Check, that the count call also returns 1
        restMissionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMissionShouldNotBeFound(String filter) throws Exception {
        restMissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMissionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMission() throws Exception {
        // Get the mission
        restMissionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMission() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mission
        Mission updatedMission = missionRepository.findById(mission.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMission are not directly saved in db
        em.detach(updatedMission);
        updatedMission
            .description(UPDATED_DESCRIPTION)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .statut(UPDATED_STATUT)
            .prixPropose(UPDATED_PRIX_PROPOSE)
            .dateCreation(UPDATED_DATE_CREATION);

        restMissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMission.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedMission))
            )
            .andExpect(status().isOk());

        // Validate the Mission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMissionToMatchAllProperties(updatedMission);
    }

    @Test
    @Transactional
    void putNonExistingMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mission.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(put(ENTITY_API_URL_ID, mission.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mission)))
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mission.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(mission))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mission.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mission)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Mission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMissionWithPatch() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mission using partial update
        Mission partialUpdatedMission = new Mission();
        partialUpdatedMission.setId(mission.getId());

        partialUpdatedMission
            .description(UPDATED_DESCRIPTION)
            .latitude(UPDATED_LATITUDE)
            .statut(UPDATED_STATUT)
            .prixPropose(UPDATED_PRIX_PROPOSE);

        restMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMission))
            )
            .andExpect(status().isOk());

        // Validate the Mission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMissionUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMission, mission), getPersistedMission(mission));
    }

    @Test
    @Transactional
    void fullUpdateMissionWithPatch() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mission using partial update
        Mission partialUpdatedMission = new Mission();
        partialUpdatedMission.setId(mission.getId());

        partialUpdatedMission
            .description(UPDATED_DESCRIPTION)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .statut(UPDATED_STATUT)
            .prixPropose(UPDATED_PRIX_PROPOSE)
            .dateCreation(UPDATED_DATE_CREATION);

        restMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMission))
            )
            .andExpect(status().isOk());

        // Validate the Mission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMissionUpdatableFieldsEquals(partialUpdatedMission, getPersistedMission(partialUpdatedMission));
    }

    @Test
    @Transactional
    void patchNonExistingMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mission.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, mission.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(mission))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mission.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(mission))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mission.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(mission)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Mission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMission() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the mission
        restMissionMockMvc
            .perform(delete(ENTITY_API_URL_ID, mission.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return missionRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Mission getPersistedMission(Mission mission) {
        return missionRepository.findById(mission.getId()).orElseThrow();
    }

    protected void assertPersistedMissionToMatchAllProperties(Mission expectedMission) {
        assertMissionAllPropertiesEquals(expectedMission, getPersistedMission(expectedMission));
    }

    protected void assertPersistedMissionToMatchUpdatableProperties(Mission expectedMission) {
        assertMissionAllUpdatablePropertiesEquals(expectedMission, getPersistedMission(expectedMission));
    }
}
