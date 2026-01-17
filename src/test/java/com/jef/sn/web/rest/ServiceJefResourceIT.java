package com.jef.sn.web.rest;

import static com.jef.sn.domain.ServiceJefAsserts.*;
import static com.jef.sn.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jef.sn.IntegrationTest;
import com.jef.sn.domain.ServiceJef;
import com.jef.sn.domain.Technicien;
import com.jef.sn.repository.ServiceJefRepository;
import com.jef.sn.service.dto.ServiceJefDTO;
import com.jef.sn.service.mapper.ServiceJefMapper;
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
 * Integration tests for the {@link ServiceJefResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ServiceJefResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIF = false;
    private static final Boolean UPDATED_ACTIF = true;

    private static final String ENTITY_API_URL = "/api/service-jefs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ServiceJefRepository serviceJefRepository;

    @Autowired
    private ServiceJefMapper serviceJefMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restServiceJefMockMvc;

    private ServiceJef serviceJef;

    private ServiceJef insertedServiceJef;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServiceJef createEntity() {
        return new ServiceJef().nom(DEFAULT_NOM).description(DEFAULT_DESCRIPTION).actif(DEFAULT_ACTIF);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServiceJef createUpdatedEntity() {
        return new ServiceJef().nom(UPDATED_NOM).description(UPDATED_DESCRIPTION).actif(UPDATED_ACTIF);
    }

    @BeforeEach
    void initTest() {
        serviceJef = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedServiceJef != null) {
            serviceJefRepository.delete(insertedServiceJef);
            insertedServiceJef = null;
        }
    }

    @Test
    @Transactional
    void createServiceJef() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ServiceJef
        ServiceJefDTO serviceJefDTO = serviceJefMapper.toDto(serviceJef);
        var returnedServiceJefDTO = om.readValue(
            restServiceJefMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceJefDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ServiceJefDTO.class
        );

        // Validate the ServiceJef in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedServiceJef = serviceJefMapper.toEntity(returnedServiceJefDTO);
        assertServiceJefUpdatableFieldsEquals(returnedServiceJef, getPersistedServiceJef(returnedServiceJef));

        insertedServiceJef = returnedServiceJef;
    }

    @Test
    @Transactional
    void createServiceJefWithExistingId() throws Exception {
        // Create the ServiceJef with an existing ID
        serviceJef.setId(1L);
        ServiceJefDTO serviceJefDTO = serviceJefMapper.toDto(serviceJef);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restServiceJefMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceJefDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ServiceJef in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        serviceJef.setNom(null);

        // Create the ServiceJef, which fails.
        ServiceJefDTO serviceJefDTO = serviceJefMapper.toDto(serviceJef);

        restServiceJefMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceJefDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllServiceJefs() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        // Get all the serviceJefList
        restServiceJefMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceJef.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));
    }

    @Test
    @Transactional
    void getServiceJef() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        // Get the serviceJef
        restServiceJefMockMvc
            .perform(get(ENTITY_API_URL_ID, serviceJef.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(serviceJef.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.actif").value(DEFAULT_ACTIF));
    }

    @Test
    @Transactional
    void getServiceJefsByIdFiltering() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        Long id = serviceJef.getId();

        defaultServiceJefFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultServiceJefFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultServiceJefFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllServiceJefsByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        // Get all the serviceJefList where nom equals to
        defaultServiceJefFiltering("nom.equals=" + DEFAULT_NOM, "nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllServiceJefsByNomIsInShouldWork() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        // Get all the serviceJefList where nom in
        defaultServiceJefFiltering("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM, "nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllServiceJefsByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        // Get all the serviceJefList where nom is not null
        defaultServiceJefFiltering("nom.specified=true", "nom.specified=false");
    }

    @Test
    @Transactional
    void getAllServiceJefsByNomContainsSomething() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        // Get all the serviceJefList where nom contains
        defaultServiceJefFiltering("nom.contains=" + DEFAULT_NOM, "nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllServiceJefsByNomNotContainsSomething() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        // Get all the serviceJefList where nom does not contain
        defaultServiceJefFiltering("nom.doesNotContain=" + UPDATED_NOM, "nom.doesNotContain=" + DEFAULT_NOM);
    }

    @Test
    @Transactional
    void getAllServiceJefsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        // Get all the serviceJefList where description equals to
        defaultServiceJefFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllServiceJefsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        // Get all the serviceJefList where description in
        defaultServiceJefFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllServiceJefsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        // Get all the serviceJefList where description is not null
        defaultServiceJefFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllServiceJefsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        // Get all the serviceJefList where description contains
        defaultServiceJefFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllServiceJefsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        // Get all the serviceJefList where description does not contain
        defaultServiceJefFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllServiceJefsByActifIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        // Get all the serviceJefList where actif equals to
        defaultServiceJefFiltering("actif.equals=" + DEFAULT_ACTIF, "actif.equals=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllServiceJefsByActifIsInShouldWork() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        // Get all the serviceJefList where actif in
        defaultServiceJefFiltering("actif.in=" + DEFAULT_ACTIF + "," + UPDATED_ACTIF, "actif.in=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllServiceJefsByActifIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        // Get all the serviceJefList where actif is not null
        defaultServiceJefFiltering("actif.specified=true", "actif.specified=false");
    }

    @Test
    @Transactional
    void getAllServiceJefsByTechniciensIsEqualToSomething() throws Exception {
        Technicien techniciens;
        if (TestUtil.findAll(em, Technicien.class).isEmpty()) {
            serviceJefRepository.saveAndFlush(serviceJef);
            techniciens = TechnicienResourceIT.createEntity();
        } else {
            techniciens = TestUtil.findAll(em, Technicien.class).get(0);
        }
        em.persist(techniciens);
        em.flush();
        serviceJef.addTechniciens(techniciens);
        serviceJefRepository.saveAndFlush(serviceJef);
        Long techniciensId = techniciens.getId();
        // Get all the serviceJefList where techniciens equals to techniciensId
        defaultServiceJefShouldBeFound("techniciensId.equals=" + techniciensId);

        // Get all the serviceJefList where techniciens equals to (techniciensId + 1)
        defaultServiceJefShouldNotBeFound("techniciensId.equals=" + (techniciensId + 1));
    }

    private void defaultServiceJefFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultServiceJefShouldBeFound(shouldBeFound);
        defaultServiceJefShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultServiceJefShouldBeFound(String filter) throws Exception {
        restServiceJefMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceJef.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));

        // Check, that the count call also returns 1
        restServiceJefMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultServiceJefShouldNotBeFound(String filter) throws Exception {
        restServiceJefMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restServiceJefMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingServiceJef() throws Exception {
        // Get the serviceJef
        restServiceJefMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingServiceJef() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceJef
        ServiceJef updatedServiceJef = serviceJefRepository.findById(serviceJef.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedServiceJef are not directly saved in db
        em.detach(updatedServiceJef);
        updatedServiceJef.nom(UPDATED_NOM).description(UPDATED_DESCRIPTION).actif(UPDATED_ACTIF);
        ServiceJefDTO serviceJefDTO = serviceJefMapper.toDto(updatedServiceJef);

        restServiceJefMockMvc
            .perform(
                put(ENTITY_API_URL_ID, serviceJefDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serviceJefDTO))
            )
            .andExpect(status().isOk());

        // Validate the ServiceJef in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedServiceJefToMatchAllProperties(updatedServiceJef);
    }

    @Test
    @Transactional
    void putNonExistingServiceJef() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceJef.setId(longCount.incrementAndGet());

        // Create the ServiceJef
        ServiceJefDTO serviceJefDTO = serviceJefMapper.toDto(serviceJef);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceJefMockMvc
            .perform(
                put(ENTITY_API_URL_ID, serviceJefDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serviceJefDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceJef in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchServiceJef() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceJef.setId(longCount.incrementAndGet());

        // Create the ServiceJef
        ServiceJefDTO serviceJefDTO = serviceJefMapper.toDto(serviceJef);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceJefMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serviceJefDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceJef in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamServiceJef() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceJef.setId(longCount.incrementAndGet());

        // Create the ServiceJef
        ServiceJefDTO serviceJefDTO = serviceJefMapper.toDto(serviceJef);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceJefMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceJefDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ServiceJef in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateServiceJefWithPatch() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceJef using partial update
        ServiceJef partialUpdatedServiceJef = new ServiceJef();
        partialUpdatedServiceJef.setId(serviceJef.getId());

        partialUpdatedServiceJef.nom(UPDATED_NOM).description(UPDATED_DESCRIPTION).actif(UPDATED_ACTIF);

        restServiceJefMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServiceJef.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedServiceJef))
            )
            .andExpect(status().isOk());

        // Validate the ServiceJef in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceJefUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedServiceJef, serviceJef),
            getPersistedServiceJef(serviceJef)
        );
    }

    @Test
    @Transactional
    void fullUpdateServiceJefWithPatch() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceJef using partial update
        ServiceJef partialUpdatedServiceJef = new ServiceJef();
        partialUpdatedServiceJef.setId(serviceJef.getId());

        partialUpdatedServiceJef.nom(UPDATED_NOM).description(UPDATED_DESCRIPTION).actif(UPDATED_ACTIF);

        restServiceJefMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServiceJef.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedServiceJef))
            )
            .andExpect(status().isOk());

        // Validate the ServiceJef in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceJefUpdatableFieldsEquals(partialUpdatedServiceJef, getPersistedServiceJef(partialUpdatedServiceJef));
    }

    @Test
    @Transactional
    void patchNonExistingServiceJef() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceJef.setId(longCount.incrementAndGet());

        // Create the ServiceJef
        ServiceJefDTO serviceJefDTO = serviceJefMapper.toDto(serviceJef);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceJefMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, serviceJefDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(serviceJefDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceJef in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchServiceJef() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceJef.setId(longCount.incrementAndGet());

        // Create the ServiceJef
        ServiceJefDTO serviceJefDTO = serviceJefMapper.toDto(serviceJef);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceJefMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(serviceJefDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceJef in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamServiceJef() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceJef.setId(longCount.incrementAndGet());

        // Create the ServiceJef
        ServiceJefDTO serviceJefDTO = serviceJefMapper.toDto(serviceJef);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceJefMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(serviceJefDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ServiceJef in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteServiceJef() throws Exception {
        // Initialize the database
        insertedServiceJef = serviceJefRepository.saveAndFlush(serviceJef);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the serviceJef
        restServiceJefMockMvc
            .perform(delete(ENTITY_API_URL_ID, serviceJef.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return serviceJefRepository.count();
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

    protected ServiceJef getPersistedServiceJef(ServiceJef serviceJef) {
        return serviceJefRepository.findById(serviceJef.getId()).orElseThrow();
    }

    protected void assertPersistedServiceJefToMatchAllProperties(ServiceJef expectedServiceJef) {
        assertServiceJefAllPropertiesEquals(expectedServiceJef, getPersistedServiceJef(expectedServiceJef));
    }

    protected void assertPersistedServiceJefToMatchUpdatableProperties(ServiceJef expectedServiceJef) {
        assertServiceJefAllUpdatablePropertiesEquals(expectedServiceJef, getPersistedServiceJef(expectedServiceJef));
    }
}
