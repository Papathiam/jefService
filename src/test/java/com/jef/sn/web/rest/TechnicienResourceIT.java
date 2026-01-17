package com.jef.sn.web.rest;

import static com.jef.sn.domain.TechnicienAsserts.*;
import static com.jef.sn.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jef.sn.IntegrationTest;
import com.jef.sn.domain.ServiceJef;
import com.jef.sn.domain.Technicien;
import com.jef.sn.domain.User;
import com.jef.sn.domain.enumeration.StatutValidation;
import com.jef.sn.repository.TechnicienRepository;
import com.jef.sn.repository.UserRepository;
import com.jef.sn.service.TechnicienService;
import com.jef.sn.service.dto.TechnicienDTO;
import com.jef.sn.service.mapper.TechnicienMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TechnicienResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TechnicienResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final Double DEFAULT_TARIF_MIN = 1D;
    private static final Double UPDATED_TARIF_MIN = 2D;
    private static final Double SMALLER_TARIF_MIN = 1D - 1D;

    private static final Double DEFAULT_TARIF_MAX = 1D;
    private static final Double UPDATED_TARIF_MAX = 2D;
    private static final Double SMALLER_TARIF_MAX = 1D - 1D;

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;
    private static final Double SMALLER_LATITUDE = 1D - 1D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;
    private static final Double SMALLER_LONGITUDE = 1D - 1D;

    private static final Boolean DEFAULT_DISPONIBLE = false;
    private static final Boolean UPDATED_DISPONIBLE = true;

    private static final StatutValidation DEFAULT_STATUT_VALIDATION = StatutValidation.EN_ATTENTE;
    private static final StatutValidation UPDATED_STATUT_VALIDATION = StatutValidation.VALIDE;

    private static final Double DEFAULT_NOTE_MOYENNE = 1D;
    private static final Double UPDATED_NOTE_MOYENNE = 2D;
    private static final Double SMALLER_NOTE_MOYENNE = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/techniciens";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TechnicienRepository technicienRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private TechnicienRepository technicienRepositoryMock;

    @Autowired
    private TechnicienMapper technicienMapper;

    @Mock
    private TechnicienService technicienServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTechnicienMockMvc;

    private Technicien technicien;

    private Technicien insertedTechnicien;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Technicien createEntity() {
        return new Technicien()
            .nom(DEFAULT_NOM)
            .prenom(DEFAULT_PRENOM)
            .tarifMin(DEFAULT_TARIF_MIN)
            .tarifMax(DEFAULT_TARIF_MAX)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .disponible(DEFAULT_DISPONIBLE)
            .statutValidation(DEFAULT_STATUT_VALIDATION)
            .noteMoyenne(DEFAULT_NOTE_MOYENNE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Technicien createUpdatedEntity() {
        return new Technicien()
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .tarifMin(UPDATED_TARIF_MIN)
            .tarifMax(UPDATED_TARIF_MAX)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .disponible(UPDATED_DISPONIBLE)
            .statutValidation(UPDATED_STATUT_VALIDATION)
            .noteMoyenne(UPDATED_NOTE_MOYENNE);
    }

    @BeforeEach
    void initTest() {
        technicien = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTechnicien != null) {
            technicienRepository.delete(insertedTechnicien);
            insertedTechnicien = null;
        }
    }

    @Test
    @Transactional
    void createTechnicien() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Technicien
        TechnicienDTO technicienDTO = technicienMapper.toDto(technicien);
        var returnedTechnicienDTO = om.readValue(
            restTechnicienMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(technicienDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TechnicienDTO.class
        );

        // Validate the Technicien in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTechnicien = technicienMapper.toEntity(returnedTechnicienDTO);
        assertTechnicienUpdatableFieldsEquals(returnedTechnicien, getPersistedTechnicien(returnedTechnicien));

        insertedTechnicien = returnedTechnicien;
    }

    @Test
    @Transactional
    void createTechnicienWithExistingId() throws Exception {
        // Create the Technicien with an existing ID
        technicien.setId(1L);
        TechnicienDTO technicienDTO = technicienMapper.toDto(technicien);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTechnicienMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(technicienDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Technicien in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        technicien.setNom(null);

        // Create the Technicien, which fails.
        TechnicienDTO technicienDTO = technicienMapper.toDto(technicien);

        restTechnicienMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(technicienDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrenomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        technicien.setPrenom(null);

        // Create the Technicien, which fails.
        TechnicienDTO technicienDTO = technicienMapper.toDto(technicien);

        restTechnicienMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(technicienDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTarifMinIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        technicien.setTarifMin(null);

        // Create the Technicien, which fails.
        TechnicienDTO technicienDTO = technicienMapper.toDto(technicien);

        restTechnicienMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(technicienDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTarifMaxIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        technicien.setTarifMax(null);

        // Create the Technicien, which fails.
        TechnicienDTO technicienDTO = technicienMapper.toDto(technicien);

        restTechnicienMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(technicienDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTechniciens() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList
        restTechnicienMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(technicien.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].tarifMin").value(hasItem(DEFAULT_TARIF_MIN)))
            .andExpect(jsonPath("$.[*].tarifMax").value(hasItem(DEFAULT_TARIF_MAX)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].disponible").value(hasItem(DEFAULT_DISPONIBLE)))
            .andExpect(jsonPath("$.[*].statutValidation").value(hasItem(DEFAULT_STATUT_VALIDATION.toString())))
            .andExpect(jsonPath("$.[*].noteMoyenne").value(hasItem(DEFAULT_NOTE_MOYENNE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTechniciensWithEagerRelationshipsIsEnabled() throws Exception {
        when(technicienServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTechnicienMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(technicienServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTechniciensWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(technicienServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTechnicienMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(technicienRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTechnicien() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get the technicien
        restTechnicienMockMvc
            .perform(get(ENTITY_API_URL_ID, technicien.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(technicien.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM))
            .andExpect(jsonPath("$.tarifMin").value(DEFAULT_TARIF_MIN))
            .andExpect(jsonPath("$.tarifMax").value(DEFAULT_TARIF_MAX))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE))
            .andExpect(jsonPath("$.disponible").value(DEFAULT_DISPONIBLE))
            .andExpect(jsonPath("$.statutValidation").value(DEFAULT_STATUT_VALIDATION.toString()))
            .andExpect(jsonPath("$.noteMoyenne").value(DEFAULT_NOTE_MOYENNE));
    }

    @Test
    @Transactional
    void getTechniciensByIdFiltering() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        Long id = technicien.getId();

        defaultTechnicienFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTechnicienFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTechnicienFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTechniciensByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where nom equals to
        defaultTechnicienFiltering("nom.equals=" + DEFAULT_NOM, "nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllTechniciensByNomIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where nom in
        defaultTechnicienFiltering("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM, "nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllTechniciensByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where nom is not null
        defaultTechnicienFiltering("nom.specified=true", "nom.specified=false");
    }

    @Test
    @Transactional
    void getAllTechniciensByNomContainsSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where nom contains
        defaultTechnicienFiltering("nom.contains=" + DEFAULT_NOM, "nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllTechniciensByNomNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where nom does not contain
        defaultTechnicienFiltering("nom.doesNotContain=" + UPDATED_NOM, "nom.doesNotContain=" + DEFAULT_NOM);
    }

    @Test
    @Transactional
    void getAllTechniciensByPrenomIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where prenom equals to
        defaultTechnicienFiltering("prenom.equals=" + DEFAULT_PRENOM, "prenom.equals=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllTechniciensByPrenomIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where prenom in
        defaultTechnicienFiltering("prenom.in=" + DEFAULT_PRENOM + "," + UPDATED_PRENOM, "prenom.in=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllTechniciensByPrenomIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where prenom is not null
        defaultTechnicienFiltering("prenom.specified=true", "prenom.specified=false");
    }

    @Test
    @Transactional
    void getAllTechniciensByPrenomContainsSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where prenom contains
        defaultTechnicienFiltering("prenom.contains=" + DEFAULT_PRENOM, "prenom.contains=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllTechniciensByPrenomNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where prenom does not contain
        defaultTechnicienFiltering("prenom.doesNotContain=" + UPDATED_PRENOM, "prenom.doesNotContain=" + DEFAULT_PRENOM);
    }

    @Test
    @Transactional
    void getAllTechniciensByTarifMinIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where tarifMin equals to
        defaultTechnicienFiltering("tarifMin.equals=" + DEFAULT_TARIF_MIN, "tarifMin.equals=" + UPDATED_TARIF_MIN);
    }

    @Test
    @Transactional
    void getAllTechniciensByTarifMinIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where tarifMin in
        defaultTechnicienFiltering("tarifMin.in=" + DEFAULT_TARIF_MIN + "," + UPDATED_TARIF_MIN, "tarifMin.in=" + UPDATED_TARIF_MIN);
    }

    @Test
    @Transactional
    void getAllTechniciensByTarifMinIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where tarifMin is not null
        defaultTechnicienFiltering("tarifMin.specified=true", "tarifMin.specified=false");
    }

    @Test
    @Transactional
    void getAllTechniciensByTarifMinIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where tarifMin is greater than or equal to
        defaultTechnicienFiltering("tarifMin.greaterThanOrEqual=" + DEFAULT_TARIF_MIN, "tarifMin.greaterThanOrEqual=" + UPDATED_TARIF_MIN);
    }

    @Test
    @Transactional
    void getAllTechniciensByTarifMinIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where tarifMin is less than or equal to
        defaultTechnicienFiltering("tarifMin.lessThanOrEqual=" + DEFAULT_TARIF_MIN, "tarifMin.lessThanOrEqual=" + SMALLER_TARIF_MIN);
    }

    @Test
    @Transactional
    void getAllTechniciensByTarifMinIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where tarifMin is less than
        defaultTechnicienFiltering("tarifMin.lessThan=" + UPDATED_TARIF_MIN, "tarifMin.lessThan=" + DEFAULT_TARIF_MIN);
    }

    @Test
    @Transactional
    void getAllTechniciensByTarifMinIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where tarifMin is greater than
        defaultTechnicienFiltering("tarifMin.greaterThan=" + SMALLER_TARIF_MIN, "tarifMin.greaterThan=" + DEFAULT_TARIF_MIN);
    }

    @Test
    @Transactional
    void getAllTechniciensByTarifMaxIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where tarifMax equals to
        defaultTechnicienFiltering("tarifMax.equals=" + DEFAULT_TARIF_MAX, "tarifMax.equals=" + UPDATED_TARIF_MAX);
    }

    @Test
    @Transactional
    void getAllTechniciensByTarifMaxIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where tarifMax in
        defaultTechnicienFiltering("tarifMax.in=" + DEFAULT_TARIF_MAX + "," + UPDATED_TARIF_MAX, "tarifMax.in=" + UPDATED_TARIF_MAX);
    }

    @Test
    @Transactional
    void getAllTechniciensByTarifMaxIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where tarifMax is not null
        defaultTechnicienFiltering("tarifMax.specified=true", "tarifMax.specified=false");
    }

    @Test
    @Transactional
    void getAllTechniciensByTarifMaxIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where tarifMax is greater than or equal to
        defaultTechnicienFiltering("tarifMax.greaterThanOrEqual=" + DEFAULT_TARIF_MAX, "tarifMax.greaterThanOrEqual=" + UPDATED_TARIF_MAX);
    }

    @Test
    @Transactional
    void getAllTechniciensByTarifMaxIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where tarifMax is less than or equal to
        defaultTechnicienFiltering("tarifMax.lessThanOrEqual=" + DEFAULT_TARIF_MAX, "tarifMax.lessThanOrEqual=" + SMALLER_TARIF_MAX);
    }

    @Test
    @Transactional
    void getAllTechniciensByTarifMaxIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where tarifMax is less than
        defaultTechnicienFiltering("tarifMax.lessThan=" + UPDATED_TARIF_MAX, "tarifMax.lessThan=" + DEFAULT_TARIF_MAX);
    }

    @Test
    @Transactional
    void getAllTechniciensByTarifMaxIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where tarifMax is greater than
        defaultTechnicienFiltering("tarifMax.greaterThan=" + SMALLER_TARIF_MAX, "tarifMax.greaterThan=" + DEFAULT_TARIF_MAX);
    }

    @Test
    @Transactional
    void getAllTechniciensByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where latitude equals to
        defaultTechnicienFiltering("latitude.equals=" + DEFAULT_LATITUDE, "latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllTechniciensByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where latitude in
        defaultTechnicienFiltering("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE, "latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllTechniciensByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where latitude is not null
        defaultTechnicienFiltering("latitude.specified=true", "latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllTechniciensByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where latitude is greater than or equal to
        defaultTechnicienFiltering("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE, "latitude.greaterThanOrEqual=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllTechniciensByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where latitude is less than or equal to
        defaultTechnicienFiltering("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE, "latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllTechniciensByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where latitude is less than
        defaultTechnicienFiltering("latitude.lessThan=" + UPDATED_LATITUDE, "latitude.lessThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllTechniciensByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where latitude is greater than
        defaultTechnicienFiltering("latitude.greaterThan=" + SMALLER_LATITUDE, "latitude.greaterThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllTechniciensByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where longitude equals to
        defaultTechnicienFiltering("longitude.equals=" + DEFAULT_LONGITUDE, "longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllTechniciensByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where longitude in
        defaultTechnicienFiltering("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE, "longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllTechniciensByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where longitude is not null
        defaultTechnicienFiltering("longitude.specified=true", "longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllTechniciensByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where longitude is greater than or equal to
        defaultTechnicienFiltering(
            "longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE,
            "longitude.greaterThanOrEqual=" + UPDATED_LONGITUDE
        );
    }

    @Test
    @Transactional
    void getAllTechniciensByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where longitude is less than or equal to
        defaultTechnicienFiltering("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllTechniciensByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where longitude is less than
        defaultTechnicienFiltering("longitude.lessThan=" + UPDATED_LONGITUDE, "longitude.lessThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllTechniciensByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where longitude is greater than
        defaultTechnicienFiltering("longitude.greaterThan=" + SMALLER_LONGITUDE, "longitude.greaterThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllTechniciensByDisponibleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where disponible equals to
        defaultTechnicienFiltering("disponible.equals=" + DEFAULT_DISPONIBLE, "disponible.equals=" + UPDATED_DISPONIBLE);
    }

    @Test
    @Transactional
    void getAllTechniciensByDisponibleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where disponible in
        defaultTechnicienFiltering("disponible.in=" + DEFAULT_DISPONIBLE + "," + UPDATED_DISPONIBLE, "disponible.in=" + UPDATED_DISPONIBLE);
    }

    @Test
    @Transactional
    void getAllTechniciensByDisponibleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where disponible is not null
        defaultTechnicienFiltering("disponible.specified=true", "disponible.specified=false");
    }

    @Test
    @Transactional
    void getAllTechniciensByStatutValidationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where statutValidation equals to
        defaultTechnicienFiltering(
            "statutValidation.equals=" + DEFAULT_STATUT_VALIDATION,
            "statutValidation.equals=" + UPDATED_STATUT_VALIDATION
        );
    }

    @Test
    @Transactional
    void getAllTechniciensByStatutValidationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where statutValidation in
        defaultTechnicienFiltering(
            "statutValidation.in=" + DEFAULT_STATUT_VALIDATION + "," + UPDATED_STATUT_VALIDATION,
            "statutValidation.in=" + UPDATED_STATUT_VALIDATION
        );
    }

    @Test
    @Transactional
    void getAllTechniciensByStatutValidationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where statutValidation is not null
        defaultTechnicienFiltering("statutValidation.specified=true", "statutValidation.specified=false");
    }

    @Test
    @Transactional
    void getAllTechniciensByNoteMoyenneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where noteMoyenne equals to
        defaultTechnicienFiltering("noteMoyenne.equals=" + DEFAULT_NOTE_MOYENNE, "noteMoyenne.equals=" + UPDATED_NOTE_MOYENNE);
    }

    @Test
    @Transactional
    void getAllTechniciensByNoteMoyenneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where noteMoyenne in
        defaultTechnicienFiltering(
            "noteMoyenne.in=" + DEFAULT_NOTE_MOYENNE + "," + UPDATED_NOTE_MOYENNE,
            "noteMoyenne.in=" + UPDATED_NOTE_MOYENNE
        );
    }

    @Test
    @Transactional
    void getAllTechniciensByNoteMoyenneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where noteMoyenne is not null
        defaultTechnicienFiltering("noteMoyenne.specified=true", "noteMoyenne.specified=false");
    }

    @Test
    @Transactional
    void getAllTechniciensByNoteMoyenneIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where noteMoyenne is greater than or equal to
        defaultTechnicienFiltering(
            "noteMoyenne.greaterThanOrEqual=" + DEFAULT_NOTE_MOYENNE,
            "noteMoyenne.greaterThanOrEqual=" + UPDATED_NOTE_MOYENNE
        );
    }

    @Test
    @Transactional
    void getAllTechniciensByNoteMoyenneIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where noteMoyenne is less than or equal to
        defaultTechnicienFiltering(
            "noteMoyenne.lessThanOrEqual=" + DEFAULT_NOTE_MOYENNE,
            "noteMoyenne.lessThanOrEqual=" + SMALLER_NOTE_MOYENNE
        );
    }

    @Test
    @Transactional
    void getAllTechniciensByNoteMoyenneIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where noteMoyenne is less than
        defaultTechnicienFiltering("noteMoyenne.lessThan=" + UPDATED_NOTE_MOYENNE, "noteMoyenne.lessThan=" + DEFAULT_NOTE_MOYENNE);
    }

    @Test
    @Transactional
    void getAllTechniciensByNoteMoyenneIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        // Get all the technicienList where noteMoyenne is greater than
        defaultTechnicienFiltering("noteMoyenne.greaterThan=" + SMALLER_NOTE_MOYENNE, "noteMoyenne.greaterThan=" + DEFAULT_NOTE_MOYENNE);
    }

    @Test
    @Transactional
    void getAllTechniciensByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            technicienRepository.saveAndFlush(technicien);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        technicien.setUser(user);
        technicienRepository.saveAndFlush(technicien);
        Long userId = user.getId();
        // Get all the technicienList where user equals to userId
        defaultTechnicienShouldBeFound("userId.equals=" + userId);

        // Get all the technicienList where user equals to (userId + 1)
        defaultTechnicienShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllTechniciensByServiceJefsIsEqualToSomething() throws Exception {
        ServiceJef serviceJefs;
        if (TestUtil.findAll(em, ServiceJef.class).isEmpty()) {
            technicienRepository.saveAndFlush(technicien);
            serviceJefs = ServiceJefResourceIT.createEntity();
        } else {
            serviceJefs = TestUtil.findAll(em, ServiceJef.class).get(0);
        }
        em.persist(serviceJefs);
        em.flush();
        technicien.addServiceJefs(serviceJefs);
        technicienRepository.saveAndFlush(technicien);
        Long serviceJefsId = serviceJefs.getId();
        // Get all the technicienList where serviceJefs equals to serviceJefsId
        defaultTechnicienShouldBeFound("serviceJefsId.equals=" + serviceJefsId);

        // Get all the technicienList where serviceJefs equals to (serviceJefsId + 1)
        defaultTechnicienShouldNotBeFound("serviceJefsId.equals=" + (serviceJefsId + 1));
    }

    private void defaultTechnicienFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTechnicienShouldBeFound(shouldBeFound);
        defaultTechnicienShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTechnicienShouldBeFound(String filter) throws Exception {
        restTechnicienMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(technicien.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].tarifMin").value(hasItem(DEFAULT_TARIF_MIN)))
            .andExpect(jsonPath("$.[*].tarifMax").value(hasItem(DEFAULT_TARIF_MAX)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].disponible").value(hasItem(DEFAULT_DISPONIBLE)))
            .andExpect(jsonPath("$.[*].statutValidation").value(hasItem(DEFAULT_STATUT_VALIDATION.toString())))
            .andExpect(jsonPath("$.[*].noteMoyenne").value(hasItem(DEFAULT_NOTE_MOYENNE)));

        // Check, that the count call also returns 1
        restTechnicienMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTechnicienShouldNotBeFound(String filter) throws Exception {
        restTechnicienMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTechnicienMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTechnicien() throws Exception {
        // Get the technicien
        restTechnicienMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTechnicien() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the technicien
        Technicien updatedTechnicien = technicienRepository.findById(technicien.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTechnicien are not directly saved in db
        em.detach(updatedTechnicien);
        updatedTechnicien
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .tarifMin(UPDATED_TARIF_MIN)
            .tarifMax(UPDATED_TARIF_MAX)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .disponible(UPDATED_DISPONIBLE)
            .statutValidation(UPDATED_STATUT_VALIDATION)
            .noteMoyenne(UPDATED_NOTE_MOYENNE);
        TechnicienDTO technicienDTO = technicienMapper.toDto(updatedTechnicien);

        restTechnicienMockMvc
            .perform(
                put(ENTITY_API_URL_ID, technicienDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(technicienDTO))
            )
            .andExpect(status().isOk());

        // Validate the Technicien in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTechnicienToMatchAllProperties(updatedTechnicien);
    }

    @Test
    @Transactional
    void putNonExistingTechnicien() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        technicien.setId(longCount.incrementAndGet());

        // Create the Technicien
        TechnicienDTO technicienDTO = technicienMapper.toDto(technicien);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTechnicienMockMvc
            .perform(
                put(ENTITY_API_URL_ID, technicienDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(technicienDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Technicien in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTechnicien() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        technicien.setId(longCount.incrementAndGet());

        // Create the Technicien
        TechnicienDTO technicienDTO = technicienMapper.toDto(technicien);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTechnicienMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(technicienDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Technicien in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTechnicien() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        technicien.setId(longCount.incrementAndGet());

        // Create the Technicien
        TechnicienDTO technicienDTO = technicienMapper.toDto(technicien);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTechnicienMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(technicienDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Technicien in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTechnicienWithPatch() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the technicien using partial update
        Technicien partialUpdatedTechnicien = new Technicien();
        partialUpdatedTechnicien.setId(technicien.getId());

        partialUpdatedTechnicien
            .tarifMin(UPDATED_TARIF_MIN)
            .tarifMax(UPDATED_TARIF_MAX)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .disponible(UPDATED_DISPONIBLE)
            .statutValidation(UPDATED_STATUT_VALIDATION);

        restTechnicienMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTechnicien.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTechnicien))
            )
            .andExpect(status().isOk());

        // Validate the Technicien in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTechnicienUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTechnicien, technicien),
            getPersistedTechnicien(technicien)
        );
    }

    @Test
    @Transactional
    void fullUpdateTechnicienWithPatch() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the technicien using partial update
        Technicien partialUpdatedTechnicien = new Technicien();
        partialUpdatedTechnicien.setId(technicien.getId());

        partialUpdatedTechnicien
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .tarifMin(UPDATED_TARIF_MIN)
            .tarifMax(UPDATED_TARIF_MAX)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .disponible(UPDATED_DISPONIBLE)
            .statutValidation(UPDATED_STATUT_VALIDATION)
            .noteMoyenne(UPDATED_NOTE_MOYENNE);

        restTechnicienMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTechnicien.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTechnicien))
            )
            .andExpect(status().isOk());

        // Validate the Technicien in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTechnicienUpdatableFieldsEquals(partialUpdatedTechnicien, getPersistedTechnicien(partialUpdatedTechnicien));
    }

    @Test
    @Transactional
    void patchNonExistingTechnicien() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        technicien.setId(longCount.incrementAndGet());

        // Create the Technicien
        TechnicienDTO technicienDTO = technicienMapper.toDto(technicien);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTechnicienMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, technicienDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(technicienDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Technicien in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTechnicien() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        technicien.setId(longCount.incrementAndGet());

        // Create the Technicien
        TechnicienDTO technicienDTO = technicienMapper.toDto(technicien);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTechnicienMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(technicienDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Technicien in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTechnicien() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        technicien.setId(longCount.incrementAndGet());

        // Create the Technicien
        TechnicienDTO technicienDTO = technicienMapper.toDto(technicien);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTechnicienMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(technicienDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Technicien in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTechnicien() throws Exception {
        // Initialize the database
        insertedTechnicien = technicienRepository.saveAndFlush(technicien);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the technicien
        restTechnicienMockMvc
            .perform(delete(ENTITY_API_URL_ID, technicien.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return technicienRepository.count();
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

    protected Technicien getPersistedTechnicien(Technicien technicien) {
        return technicienRepository.findById(technicien.getId()).orElseThrow();
    }

    protected void assertPersistedTechnicienToMatchAllProperties(Technicien expectedTechnicien) {
        assertTechnicienAllPropertiesEquals(expectedTechnicien, getPersistedTechnicien(expectedTechnicien));
    }

    protected void assertPersistedTechnicienToMatchUpdatableProperties(Technicien expectedTechnicien) {
        assertTechnicienAllUpdatablePropertiesEquals(expectedTechnicien, getPersistedTechnicien(expectedTechnicien));
    }
}
