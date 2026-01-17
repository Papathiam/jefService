package com.jef.sn.web.rest;

import static com.jef.sn.domain.DocumentAsserts.*;
import static com.jef.sn.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jef.sn.IntegrationTest;
import com.jef.sn.domain.Document;
import com.jef.sn.domain.Technicien;
import com.jef.sn.domain.enumeration.TypeDocument;
import com.jef.sn.repository.DocumentRepository;
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
 * Integration tests for the {@link DocumentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DocumentResourceIT {

    private static final TypeDocument DEFAULT_TYPE = TypeDocument.CNI;
    private static final TypeDocument UPDATED_TYPE = TypeDocument.PASSPORT;

    private static final String DEFAULT_URL_FICHIER = "AAAAAAAAAA";
    private static final String UPDATED_URL_FICHIER = "BBBBBBBBBB";

    private static final Boolean DEFAULT_VALIDE = false;
    private static final Boolean UPDATED_VALIDE = true;

    private static final Instant DEFAULT_DATE_UPLOAD = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_UPLOAD = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/documents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDocumentMockMvc;

    private Document document;

    private Document insertedDocument;

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */

    @Test
    @Transactional
    void createDocument() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Document
        var returnedDocument = om.readValue(
            restDocumentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(document)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Document.class
        );

        // Validate the Document in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDocumentUpdatableFieldsEquals(returnedDocument, getPersistedDocument(returnedDocument));

        insertedDocument = returnedDocument;
    }

    @Test
    @Transactional
    void createDocumentWithExistingId() throws Exception {
        // Create the Document with an existing ID
        document.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(document)))
            .andExpect(status().isBadRequest());

        // Validate the Document in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        document.setType(null);

        // Create the Document, which fails.

        restDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(document)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUrlFichierIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();

        restDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(document)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDocuments() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);

        // Get all the documentList
        restDocumentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(document.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].valide").value(hasItem(DEFAULT_VALIDE)))
            .andExpect(jsonPath("$.[*].dateUpload").value(hasItem(DEFAULT_DATE_UPLOAD.toString())));
    }

    @Test
    @Transactional
    void getDocument() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);

        // Get the document
        restDocumentMockMvc
            .perform(get(ENTITY_API_URL_ID, document.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(document.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.valide").value(DEFAULT_VALIDE))
            .andExpect(jsonPath("$.dateUpload").value(DEFAULT_DATE_UPLOAD.toString()));
    }

    @Test
    @Transactional
    void getDocumentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);

        Long id = document.getId();

        defaultDocumentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDocumentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDocumentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDocumentsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);

        // Get all the documentList where type equals to
        defaultDocumentFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllDocumentsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);

        // Get all the documentList where type in
        defaultDocumentFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllDocumentsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);

        // Get all the documentList where type is not null
        defaultDocumentFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllDocumentsByUrlFichierIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);
        // Get all the documentList where urlFichier equals to
    }

    @Test
    @Transactional
    void getAllDocumentsByUrlFichierIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);
    }

    @Test
    @Transactional
    void getAllDocumentsByUrlFichierIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);
    }

    @Test
    @Transactional
    void getAllDocumentsByUrlFichierContainsSomething() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);
    }

    @Test
    @Transactional
    void getAllDocumentsByUrlFichierNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);
        // Get all the documentList where urlFichier does not contain
        // defaultDocumentFiltering("urlFichier.doesNotContain=" + UPDATED_URL_FICHIER, "urlFichier.doesNotContain=" + DEFAULT_URL_FICHIER);
    }

    @Test
    @Transactional
    void getAllDocumentsByValideIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);

        // Get all the documentList where valide equals to
        defaultDocumentFiltering("valide.equals=" + DEFAULT_VALIDE, "valide.equals=" + UPDATED_VALIDE);
    }

    @Test
    @Transactional
    void getAllDocumentsByValideIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);

        // Get all the documentList where valide in
        defaultDocumentFiltering("valide.in=" + DEFAULT_VALIDE + "," + UPDATED_VALIDE, "valide.in=" + UPDATED_VALIDE);
    }

    @Test
    @Transactional
    void getAllDocumentsByValideIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);

        // Get all the documentList where valide is not null
        defaultDocumentFiltering("valide.specified=true", "valide.specified=false");
    }

    @Test
    @Transactional
    void getAllDocumentsByDateUploadIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);

        // Get all the documentList where dateUpload equals to
        defaultDocumentFiltering("dateUpload.equals=" + DEFAULT_DATE_UPLOAD, "dateUpload.equals=" + UPDATED_DATE_UPLOAD);
    }

    @Test
    @Transactional
    void getAllDocumentsByDateUploadIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);

        // Get all the documentList where dateUpload in
        defaultDocumentFiltering(
            "dateUpload.in=" + DEFAULT_DATE_UPLOAD + "," + UPDATED_DATE_UPLOAD,
            "dateUpload.in=" + UPDATED_DATE_UPLOAD
        );
    }

    @Test
    @Transactional
    void getAllDocumentsByDateUploadIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);

        // Get all the documentList where dateUpload is not null
        defaultDocumentFiltering("dateUpload.specified=true", "dateUpload.specified=false");
    }

    @Test
    @Transactional
    void getAllDocumentsByTechnicienIsEqualToSomething() throws Exception {
        Technicien technicien;
        if (TestUtil.findAll(em, Technicien.class).isEmpty()) {
            documentRepository.saveAndFlush(document);
            technicien = TechnicienResourceIT.createEntity();
        } else {
            technicien = TestUtil.findAll(em, Technicien.class).get(0);
        }
        em.persist(technicien);
        em.flush();
        document.setTechnicien(technicien);
        documentRepository.saveAndFlush(document);
        Long technicienId = technicien.getId();
        // Get all the documentList where technicien equals to technicienId
        defaultDocumentShouldBeFound("technicienId.equals=" + technicienId);

        // Get all the documentList where technicien equals to (technicienId + 1)
        defaultDocumentShouldNotBeFound("technicienId.equals=" + (technicienId + 1));
    }

    private void defaultDocumentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDocumentShouldBeFound(shouldBeFound);
        defaultDocumentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDocumentShouldBeFound(String filter) throws Exception {
        restDocumentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(document.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].valide").value(hasItem(DEFAULT_VALIDE)))
            .andExpect(jsonPath("$.[*].dateUpload").value(hasItem(DEFAULT_DATE_UPLOAD.toString())));

        // Check, that the count call also returns 1
        restDocumentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDocumentShouldNotBeFound(String filter) throws Exception {
        restDocumentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDocumentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDocument() throws Exception {
        // Get the document
        restDocumentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDocument() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the document
        Document updatedDocument = documentRepository.findById(document.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDocument are not directly saved in db
        em.detach(updatedDocument);

        restDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDocument.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedDocument))
            )
            .andExpect(status().isOk());

        // Validate the Document in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDocumentToMatchAllProperties(updatedDocument);
    }

    @Test
    @Transactional
    void putNonExistingDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        document.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, document.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(document))
            )
            .andExpect(status().isBadRequest());

        // Validate the Document in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        document.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(document))
            )
            .andExpect(status().isBadRequest());

        // Validate the Document in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        document.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(document)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Document in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void fullUpdateDocumentWithPatch() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the document using partial update
        Document partialUpdatedDocument = new Document();
        partialUpdatedDocument.setId(document.getId());

        // Validate the Document in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchNonExistingDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        document.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, document.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(document))
            )
            .andExpect(status().isBadRequest());

        // Validate the Document in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        document.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(document))
            )
            .andExpect(status().isBadRequest());

        // Validate the Document in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        document.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(document)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Document in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDocument() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.saveAndFlush(document);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the document
        restDocumentMockMvc
            .perform(delete(ENTITY_API_URL_ID, document.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return documentRepository.count();
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

    protected Document getPersistedDocument(Document document) {
        return documentRepository.findById(document.getId()).orElseThrow();
    }

    protected void assertPersistedDocumentToMatchAllProperties(Document expectedDocument) {
        assertDocumentAllPropertiesEquals(expectedDocument, getPersistedDocument(expectedDocument));
    }

    protected void assertPersistedDocumentToMatchUpdatableProperties(Document expectedDocument) {
        assertDocumentAllUpdatablePropertiesEquals(expectedDocument, getPersistedDocument(expectedDocument));
    }
}
