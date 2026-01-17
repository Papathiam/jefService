package com.jef.sn.web.rest;

import static com.jef.sn.domain.ClientAsserts.*;
import static com.jef.sn.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jef.sn.IntegrationTest;
import com.jef.sn.domain.Client;
import com.jef.sn.domain.User;
import com.jef.sn.repository.ClientRepository;
import com.jef.sn.repository.UserRepository;
import com.jef.sn.service.dto.ClientDTO;
import com.jef.sn.service.mapper.ClientMapper;
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
 * Integration tests for the {@link ClientResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClientResourceIT {

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;
    private static final Double SMALLER_LATITUDE = 1D - 1D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;
    private static final Double SMALLER_LONGITUDE = 1D - 1D;

    private static final String DEFAULT_ADRESSE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/clients";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientMockMvc;

    private Client client;

    private Client insertedClient;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Client createEntity() {
        return new Client().latitude(DEFAULT_LATITUDE).longitude(DEFAULT_LONGITUDE).adresse(DEFAULT_ADRESSE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Client createUpdatedEntity() {
        return new Client().latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE).adresse(UPDATED_ADRESSE);
    }

    @BeforeEach
    void initTest() {
        client = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClient != null) {
            clientRepository.delete(insertedClient);
            insertedClient = null;
        }
    }

    @Test
    @Transactional
    void createClient() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);
        var returnedClientDTO = om.readValue(
            restClientMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClientDTO.class
        );

        // Validate the Client in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedClient = clientMapper.toEntity(returnedClientDTO);
        assertClientUpdatableFieldsEquals(returnedClient, getPersistedClient(returnedClient));

        insertedClient = returnedClient;
    }

    @Test
    @Transactional
    void createClientWithExistingId() throws Exception {
        // Create the Client with an existing ID
        client.setId(1L);
        ClientDTO clientDTO = clientMapper.toDto(client);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllClients() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(client.getId().intValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)));
    }

    @Test
    @Transactional
    void getClient() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get the client
        restClientMockMvc
            .perform(get(ENTITY_API_URL_ID, client.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(client.getId().intValue()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE))
            .andExpect(jsonPath("$.adresse").value(DEFAULT_ADRESSE));
    }

    @Test
    @Transactional
    void getClientsByIdFiltering() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        Long id = client.getId();

        defaultClientFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultClientFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultClientFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllClientsByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where latitude equals to
        defaultClientFiltering("latitude.equals=" + DEFAULT_LATITUDE, "latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllClientsByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where latitude in
        defaultClientFiltering("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE, "latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllClientsByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where latitude is not null
        defaultClientFiltering("latitude.specified=true", "latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where latitude is greater than or equal to
        defaultClientFiltering("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE, "latitude.greaterThanOrEqual=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllClientsByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where latitude is less than or equal to
        defaultClientFiltering("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE, "latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllClientsByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where latitude is less than
        defaultClientFiltering("latitude.lessThan=" + UPDATED_LATITUDE, "latitude.lessThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllClientsByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where latitude is greater than
        defaultClientFiltering("latitude.greaterThan=" + SMALLER_LATITUDE, "latitude.greaterThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllClientsByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where longitude equals to
        defaultClientFiltering("longitude.equals=" + DEFAULT_LONGITUDE, "longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllClientsByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where longitude in
        defaultClientFiltering("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE, "longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllClientsByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where longitude is not null
        defaultClientFiltering("longitude.specified=true", "longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where longitude is greater than or equal to
        defaultClientFiltering("longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.greaterThanOrEqual=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllClientsByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where longitude is less than or equal to
        defaultClientFiltering("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllClientsByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where longitude is less than
        defaultClientFiltering("longitude.lessThan=" + UPDATED_LONGITUDE, "longitude.lessThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllClientsByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where longitude is greater than
        defaultClientFiltering("longitude.greaterThan=" + SMALLER_LONGITUDE, "longitude.greaterThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllClientsByAdresseIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where adresse equals to
        defaultClientFiltering("adresse.equals=" + DEFAULT_ADRESSE, "adresse.equals=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllClientsByAdresseIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where adresse in
        defaultClientFiltering("adresse.in=" + DEFAULT_ADRESSE + "," + UPDATED_ADRESSE, "adresse.in=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllClientsByAdresseIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where adresse is not null
        defaultClientFiltering("adresse.specified=true", "adresse.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByAdresseContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where adresse contains
        defaultClientFiltering("adresse.contains=" + DEFAULT_ADRESSE, "adresse.contains=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllClientsByAdresseNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where adresse does not contain
        defaultClientFiltering("adresse.doesNotContain=" + UPDATED_ADRESSE, "adresse.doesNotContain=" + DEFAULT_ADRESSE);
    }

    @Test
    @Transactional
    void getAllClientsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            clientRepository.saveAndFlush(client);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        client.setUser(user);
        clientRepository.saveAndFlush(client);
        Long userId = user.getId();
        // Get all the clientList where user equals to userId
        defaultClientShouldBeFound("userId.equals=" + userId);

        // Get all the clientList where user equals to (userId + 1)
        defaultClientShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultClientFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultClientShouldBeFound(shouldBeFound);
        defaultClientShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClientShouldBeFound(String filter) throws Exception {
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(client.getId().intValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)));

        // Check, that the count call also returns 1
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClientShouldNotBeFound(String filter) throws Exception {
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingClient() throws Exception {
        // Get the client
        restClientMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClient() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the client
        Client updatedClient = clientRepository.findById(client.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClient are not directly saved in db
        em.detach(updatedClient);
        updatedClient.latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE).adresse(UPDATED_ADRESSE);
        ClientDTO clientDTO = clientMapper.toDto(updatedClient);

        restClientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientDTO))
            )
            .andExpect(status().isOk());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClientToMatchAllProperties(updatedClient);
    }

    @Test
    @Transactional
    void putNonExistingClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientWithPatch() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the client using partial update
        Client partialUpdatedClient = new Client();
        partialUpdatedClient.setId(client.getId());

        partialUpdatedClient.longitude(UPDATED_LONGITUDE);

        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClient.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClient))
            )
            .andExpect(status().isOk());

        // Validate the Client in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedClient, client), getPersistedClient(client));
    }

    @Test
    @Transactional
    void fullUpdateClientWithPatch() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the client using partial update
        Client partialUpdatedClient = new Client();
        partialUpdatedClient.setId(client.getId());

        partialUpdatedClient.latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE).adresse(UPDATED_ADRESSE);

        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClient.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClient))
            )
            .andExpect(status().isOk());

        // Validate the Client in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientUpdatableFieldsEquals(partialUpdatedClient, getPersistedClient(partialUpdatedClient));
    }

    @Test
    @Transactional
    void patchNonExistingClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clientDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(clientDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClient() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the client
        restClientMockMvc
            .perform(delete(ENTITY_API_URL_ID, client.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return clientRepository.count();
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

    protected Client getPersistedClient(Client client) {
        return clientRepository.findById(client.getId()).orElseThrow();
    }

    protected void assertPersistedClientToMatchAllProperties(Client expectedClient) {
        assertClientAllPropertiesEquals(expectedClient, getPersistedClient(expectedClient));
    }

    protected void assertPersistedClientToMatchUpdatableProperties(Client expectedClient) {
        assertClientAllUpdatablePropertiesEquals(expectedClient, getPersistedClient(expectedClient));
    }
}
