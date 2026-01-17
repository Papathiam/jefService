package com.jef.sn.web.rest;

import static com.jef.sn.domain.PaiementAsserts.*;
import static com.jef.sn.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jef.sn.IntegrationTest;
import com.jef.sn.domain.Paiement;
import com.jef.sn.domain.enumeration.MoyenPaiement;
import com.jef.sn.domain.enumeration.StatutPaiement;
import com.jef.sn.repository.PaiementRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link PaiementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PaiementResourceIT {

    private static final Double DEFAULT_MONTANT_TOTAL = 1D;
    private static final Double UPDATED_MONTANT_TOTAL = 2D;
    private static final Double SMALLER_MONTANT_TOTAL = 1D - 1D;

    private static final Double DEFAULT_COMMISSION = 1D;
    private static final Double UPDATED_COMMISSION = 2D;
    private static final Double SMALLER_COMMISSION = 1D - 1D;

    private static final Double DEFAULT_MONTANT_TECHNICIEN = 1D;
    private static final Double UPDATED_MONTANT_TECHNICIEN = 2D;
    private static final Double SMALLER_MONTANT_TECHNICIEN = 1D - 1D;

    private static final MoyenPaiement DEFAULT_MOYEN_PAIEMENT = MoyenPaiement.WAVE;
    private static final MoyenPaiement UPDATED_MOYEN_PAIEMENT = MoyenPaiement.ORANGE;

    private static final StatutPaiement DEFAULT_STATUT = StatutPaiement.INITIE;
    private static final StatutPaiement UPDATED_STATUT = StatutPaiement.CONFIRME;

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/paiements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaiementMockMvc;

    private Paiement paiement;

    private Paiement insertedPaiement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Paiement createEntity() {
        return new Paiement()
            .montantTotal(DEFAULT_MONTANT_TOTAL)
            .commission(DEFAULT_COMMISSION)
            .montantTechnicien(DEFAULT_MONTANT_TECHNICIEN)
            .moyenPaiement(DEFAULT_MOYEN_PAIEMENT)
            .statut(DEFAULT_STATUT)
            .reference(DEFAULT_REFERENCE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Paiement createUpdatedEntity() {
        return new Paiement()
            .montantTotal(UPDATED_MONTANT_TOTAL)
            .commission(UPDATED_COMMISSION)
            .montantTechnicien(UPDATED_MONTANT_TECHNICIEN)
            .moyenPaiement(UPDATED_MOYEN_PAIEMENT)
            .statut(UPDATED_STATUT)
            .reference(UPDATED_REFERENCE);
    }

    @BeforeEach
    void initTest() {
        paiement = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPaiement != null) {
            paiementRepository.delete(insertedPaiement);
            insertedPaiement = null;
        }
    }

    @Test
    @Transactional
    void createPaiement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Paiement
        var returnedPaiement = om.readValue(
            restPaiementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiement)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Paiement.class
        );

        // Validate the Paiement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPaiementUpdatableFieldsEquals(returnedPaiement, getPersistedPaiement(returnedPaiement));

        insertedPaiement = returnedPaiement;
    }

    @Test
    @Transactional
    void createPaiementWithExistingId() throws Exception {
        // Create the Paiement with an existing ID
        paiement.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaiementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiement)))
            .andExpect(status().isBadRequest());

        // Validate the Paiement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMontantTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paiement.setMontantTotal(null);

        // Create the Paiement, which fails.

        restPaiementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiement)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCommissionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paiement.setCommission(null);

        // Create the Paiement, which fails.

        restPaiementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiement)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMontantTechnicienIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paiement.setMontantTechnicien(null);

        // Create the Paiement, which fails.

        restPaiementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiement)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPaiements() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList
        restPaiementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paiement.getId().intValue())))
            .andExpect(jsonPath("$.[*].montantTotal").value(hasItem(DEFAULT_MONTANT_TOTAL)))
            .andExpect(jsonPath("$.[*].commission").value(hasItem(DEFAULT_COMMISSION)))
            .andExpect(jsonPath("$.[*].montantTechnicien").value(hasItem(DEFAULT_MONTANT_TECHNICIEN)))
            .andExpect(jsonPath("$.[*].moyenPaiement").value(hasItem(DEFAULT_MOYEN_PAIEMENT.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)));
    }

    @Test
    @Transactional
    void getPaiement() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get the paiement
        restPaiementMockMvc
            .perform(get(ENTITY_API_URL_ID, paiement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paiement.getId().intValue()))
            .andExpect(jsonPath("$.montantTotal").value(DEFAULT_MONTANT_TOTAL))
            .andExpect(jsonPath("$.commission").value(DEFAULT_COMMISSION))
            .andExpect(jsonPath("$.montantTechnicien").value(DEFAULT_MONTANT_TECHNICIEN))
            .andExpect(jsonPath("$.moyenPaiement").value(DEFAULT_MOYEN_PAIEMENT.toString()))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE));
    }

    @Test
    @Transactional
    void getPaiementsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        Long id = paiement.getId();

        defaultPaiementFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPaiementFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPaiementFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPaiementsByMontantTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where montantTotal equals to
        defaultPaiementFiltering("montantTotal.equals=" + DEFAULT_MONTANT_TOTAL, "montantTotal.equals=" + UPDATED_MONTANT_TOTAL);
    }

    @Test
    @Transactional
    void getAllPaiementsByMontantTotalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where montantTotal in
        defaultPaiementFiltering(
            "montantTotal.in=" + DEFAULT_MONTANT_TOTAL + "," + UPDATED_MONTANT_TOTAL,
            "montantTotal.in=" + UPDATED_MONTANT_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllPaiementsByMontantTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where montantTotal is not null
        defaultPaiementFiltering("montantTotal.specified=true", "montantTotal.specified=false");
    }

    @Test
    @Transactional
    void getAllPaiementsByMontantTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where montantTotal is greater than or equal to
        defaultPaiementFiltering(
            "montantTotal.greaterThanOrEqual=" + DEFAULT_MONTANT_TOTAL,
            "montantTotal.greaterThanOrEqual=" + UPDATED_MONTANT_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllPaiementsByMontantTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where montantTotal is less than or equal to
        defaultPaiementFiltering(
            "montantTotal.lessThanOrEqual=" + DEFAULT_MONTANT_TOTAL,
            "montantTotal.lessThanOrEqual=" + SMALLER_MONTANT_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllPaiementsByMontantTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where montantTotal is less than
        defaultPaiementFiltering("montantTotal.lessThan=" + UPDATED_MONTANT_TOTAL, "montantTotal.lessThan=" + DEFAULT_MONTANT_TOTAL);
    }

    @Test
    @Transactional
    void getAllPaiementsByMontantTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where montantTotal is greater than
        defaultPaiementFiltering("montantTotal.greaterThan=" + SMALLER_MONTANT_TOTAL, "montantTotal.greaterThan=" + DEFAULT_MONTANT_TOTAL);
    }

    @Test
    @Transactional
    void getAllPaiementsByCommissionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where commission equals to
        defaultPaiementFiltering("commission.equals=" + DEFAULT_COMMISSION, "commission.equals=" + UPDATED_COMMISSION);
    }

    @Test
    @Transactional
    void getAllPaiementsByCommissionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where commission in
        defaultPaiementFiltering("commission.in=" + DEFAULT_COMMISSION + "," + UPDATED_COMMISSION, "commission.in=" + UPDATED_COMMISSION);
    }

    @Test
    @Transactional
    void getAllPaiementsByCommissionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where commission is not null
        defaultPaiementFiltering("commission.specified=true", "commission.specified=false");
    }

    @Test
    @Transactional
    void getAllPaiementsByCommissionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where commission is greater than or equal to
        defaultPaiementFiltering(
            "commission.greaterThanOrEqual=" + DEFAULT_COMMISSION,
            "commission.greaterThanOrEqual=" + UPDATED_COMMISSION
        );
    }

    @Test
    @Transactional
    void getAllPaiementsByCommissionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where commission is less than or equal to
        defaultPaiementFiltering("commission.lessThanOrEqual=" + DEFAULT_COMMISSION, "commission.lessThanOrEqual=" + SMALLER_COMMISSION);
    }

    @Test
    @Transactional
    void getAllPaiementsByCommissionIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where commission is less than
        defaultPaiementFiltering("commission.lessThan=" + UPDATED_COMMISSION, "commission.lessThan=" + DEFAULT_COMMISSION);
    }

    @Test
    @Transactional
    void getAllPaiementsByCommissionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where commission is greater than
        defaultPaiementFiltering("commission.greaterThan=" + SMALLER_COMMISSION, "commission.greaterThan=" + DEFAULT_COMMISSION);
    }

    @Test
    @Transactional
    void getAllPaiementsByMontantTechnicienIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where montantTechnicien equals to
        defaultPaiementFiltering(
            "montantTechnicien.equals=" + DEFAULT_MONTANT_TECHNICIEN,
            "montantTechnicien.equals=" + UPDATED_MONTANT_TECHNICIEN
        );
    }

    @Test
    @Transactional
    void getAllPaiementsByMontantTechnicienIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where montantTechnicien in
        defaultPaiementFiltering(
            "montantTechnicien.in=" + DEFAULT_MONTANT_TECHNICIEN + "," + UPDATED_MONTANT_TECHNICIEN,
            "montantTechnicien.in=" + UPDATED_MONTANT_TECHNICIEN
        );
    }

    @Test
    @Transactional
    void getAllPaiementsByMontantTechnicienIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where montantTechnicien is not null
        defaultPaiementFiltering("montantTechnicien.specified=true", "montantTechnicien.specified=false");
    }

    @Test
    @Transactional
    void getAllPaiementsByMontantTechnicienIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where montantTechnicien is greater than or equal to
        defaultPaiementFiltering(
            "montantTechnicien.greaterThanOrEqual=" + DEFAULT_MONTANT_TECHNICIEN,
            "montantTechnicien.greaterThanOrEqual=" + UPDATED_MONTANT_TECHNICIEN
        );
    }

    @Test
    @Transactional
    void getAllPaiementsByMontantTechnicienIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where montantTechnicien is less than or equal to
        defaultPaiementFiltering(
            "montantTechnicien.lessThanOrEqual=" + DEFAULT_MONTANT_TECHNICIEN,
            "montantTechnicien.lessThanOrEqual=" + SMALLER_MONTANT_TECHNICIEN
        );
    }

    @Test
    @Transactional
    void getAllPaiementsByMontantTechnicienIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where montantTechnicien is less than
        defaultPaiementFiltering(
            "montantTechnicien.lessThan=" + UPDATED_MONTANT_TECHNICIEN,
            "montantTechnicien.lessThan=" + DEFAULT_MONTANT_TECHNICIEN
        );
    }

    @Test
    @Transactional
    void getAllPaiementsByMontantTechnicienIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where montantTechnicien is greater than
        defaultPaiementFiltering(
            "montantTechnicien.greaterThan=" + SMALLER_MONTANT_TECHNICIEN,
            "montantTechnicien.greaterThan=" + DEFAULT_MONTANT_TECHNICIEN
        );
    }

    @Test
    @Transactional
    void getAllPaiementsByMoyenPaiementIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where moyenPaiement equals to
        defaultPaiementFiltering("moyenPaiement.equals=" + DEFAULT_MOYEN_PAIEMENT, "moyenPaiement.equals=" + UPDATED_MOYEN_PAIEMENT);
    }

    @Test
    @Transactional
    void getAllPaiementsByMoyenPaiementIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where moyenPaiement in
        defaultPaiementFiltering(
            "moyenPaiement.in=" + DEFAULT_MOYEN_PAIEMENT + "," + UPDATED_MOYEN_PAIEMENT,
            "moyenPaiement.in=" + UPDATED_MOYEN_PAIEMENT
        );
    }

    @Test
    @Transactional
    void getAllPaiementsByMoyenPaiementIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where moyenPaiement is not null
        defaultPaiementFiltering("moyenPaiement.specified=true", "moyenPaiement.specified=false");
    }

    @Test
    @Transactional
    void getAllPaiementsByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where statut equals to
        defaultPaiementFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllPaiementsByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where statut in
        defaultPaiementFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllPaiementsByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where statut is not null
        defaultPaiementFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllPaiementsByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where reference equals to
        defaultPaiementFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllPaiementsByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where reference in
        defaultPaiementFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllPaiementsByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where reference is not null
        defaultPaiementFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllPaiementsByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where reference contains
        defaultPaiementFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllPaiementsByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList where reference does not contain
        defaultPaiementFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    private void defaultPaiementFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPaiementShouldBeFound(shouldBeFound);
        defaultPaiementShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPaiementShouldBeFound(String filter) throws Exception {
        restPaiementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paiement.getId().intValue())))
            .andExpect(jsonPath("$.[*].montantTotal").value(hasItem(DEFAULT_MONTANT_TOTAL)))
            .andExpect(jsonPath("$.[*].commission").value(hasItem(DEFAULT_COMMISSION)))
            .andExpect(jsonPath("$.[*].montantTechnicien").value(hasItem(DEFAULT_MONTANT_TECHNICIEN)))
            .andExpect(jsonPath("$.[*].moyenPaiement").value(hasItem(DEFAULT_MOYEN_PAIEMENT.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)));

        // Check, that the count call also returns 1
        restPaiementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPaiementShouldNotBeFound(String filter) throws Exception {
        restPaiementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPaiementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPaiement() throws Exception {
        // Get the paiement
        restPaiementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPaiement() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paiement
        Paiement updatedPaiement = paiementRepository.findById(paiement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPaiement are not directly saved in db
        em.detach(updatedPaiement);
        updatedPaiement
            .montantTotal(UPDATED_MONTANT_TOTAL)
            .commission(UPDATED_COMMISSION)
            .montantTechnicien(UPDATED_MONTANT_TECHNICIEN)
            .moyenPaiement(UPDATED_MOYEN_PAIEMENT)
            .statut(UPDATED_STATUT)
            .reference(UPDATED_REFERENCE);

        restPaiementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPaiement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedPaiement))
            )
            .andExpect(status().isOk());

        // Validate the Paiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPaiementToMatchAllProperties(updatedPaiement);
    }

    @Test
    @Transactional
    void putNonExistingPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiement.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaiementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paiement.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Paiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaiementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paiement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Paiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaiementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Paiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePaiementWithPatch() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paiement using partial update
        Paiement partialUpdatedPaiement = new Paiement();
        partialUpdatedPaiement.setId(paiement.getId());

        partialUpdatedPaiement.statut(UPDATED_STATUT).reference(UPDATED_REFERENCE);

        restPaiementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaiement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaiement))
            )
            .andExpect(status().isOk());

        // Validate the Paiement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaiementUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPaiement, paiement), getPersistedPaiement(paiement));
    }

    @Test
    @Transactional
    void fullUpdatePaiementWithPatch() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paiement using partial update
        Paiement partialUpdatedPaiement = new Paiement();
        partialUpdatedPaiement.setId(paiement.getId());

        partialUpdatedPaiement
            .montantTotal(UPDATED_MONTANT_TOTAL)
            .commission(UPDATED_COMMISSION)
            .montantTechnicien(UPDATED_MONTANT_TECHNICIEN)
            .moyenPaiement(UPDATED_MOYEN_PAIEMENT)
            .statut(UPDATED_STATUT)
            .reference(UPDATED_REFERENCE);

        restPaiementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaiement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaiement))
            )
            .andExpect(status().isOk());

        // Validate the Paiement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaiementUpdatableFieldsEquals(partialUpdatedPaiement, getPersistedPaiement(partialUpdatedPaiement));
    }

    @Test
    @Transactional
    void patchNonExistingPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiement.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaiementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paiement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paiement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Paiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaiementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paiement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Paiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaiementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(paiement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Paiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePaiement() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the paiement
        restPaiementMockMvc
            .perform(delete(ENTITY_API_URL_ID, paiement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return paiementRepository.count();
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

    protected Paiement getPersistedPaiement(Paiement paiement) {
        return paiementRepository.findById(paiement.getId()).orElseThrow();
    }

    protected void assertPersistedPaiementToMatchAllProperties(Paiement expectedPaiement) {
        assertPaiementAllPropertiesEquals(expectedPaiement, getPersistedPaiement(expectedPaiement));
    }

    protected void assertPersistedPaiementToMatchUpdatableProperties(Paiement expectedPaiement) {
        assertPaiementAllUpdatablePropertiesEquals(expectedPaiement, getPersistedPaiement(expectedPaiement));
    }
}
