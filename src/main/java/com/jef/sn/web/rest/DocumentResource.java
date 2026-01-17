package com.jef.sn.web.rest;

import com.jef.sn.domain.Document;
import com.jef.sn.domain.Technicien;
import com.jef.sn.domain.User;
import com.jef.sn.domain.enumeration.StatutValidation;
import com.jef.sn.repository.DocumentRepository;
import com.jef.sn.repository.TechnicienRepository;
import com.jef.sn.repository.UserRepository;
import com.jef.sn.service.DocumentQueryService;
import com.jef.sn.service.DocumentService;
import com.jef.sn.service.SmsService;
import com.jef.sn.service.criteria.DocumentCriteria;
import com.jef.sn.service.dto.*;
import com.jef.sn.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.jef.sn.domain.Document}.
 */
@RestController
@RequestMapping("/api/documents")
public class DocumentResource {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentResource.class);

    private static final String ENTITY_NAME = "document";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DocumentService documentService;

    private final DocumentRepository documentRepository;
    private final TechnicienRepository technicienRepository;
    private final UserRepository userRepository;
    private final SmsService smsService;

    private final DocumentQueryService documentQueryService;

    public DocumentResource(
        DocumentService documentService,
        DocumentRepository documentRepository,
        DocumentQueryService documentQueryService,
        TechnicienRepository technicienRepository,
        UserRepository userRepository,
        SmsService smsService
    ) {
        this.documentService = documentService;
        this.documentRepository = documentRepository;
        this.documentQueryService = documentQueryService;
        this.technicienRepository = technicienRepository;
        this.userRepository = userRepository;
        this.smsService = smsService;
    }

    /**
     * {@code POST  /documents} : Create a new document.
     *
     * @param document the document to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new document, or with status {@code 400 (Bad Request)} if the document has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Document> createDocument(@Valid @RequestBody Document document) throws URISyntaxException {
        LOG.debug("REST request to save Document : {}", document);
        if (document.getId() != null) {
            throw new BadRequestAlertException("A new document cannot already have an ID", ENTITY_NAME, "idexists");
        }
        document = documentService.save(document);
        return ResponseEntity.created(new URI("/api/documents/" + document.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, document.getId().toString()))
            .body(document);
    }

    /**
     * {@code PUT  /documents/:id} : Updates an existing document.
     *
     * @param id       the id of the document to save.
     * @param document the document to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated document,
     * or with status {@code 400 (Bad Request)} if the document is not valid,
     * or with status {@code 500 (Internal Server Error)} if the document couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Document document
    ) throws URISyntaxException {
        LOG.debug("REST request to update Document : {}, {}", id, document);
        if (document.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, document.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!documentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        document = documentService.update(document);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, document.getId().toString()))
            .body(document);
    }

    /**
     * {@code PATCH  /documents/:id} : Partial updates given fields of an existing document, field will ignore if it is null
     *
     * @param id       the id of the document to save.
     * @param document the document to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated document,
     * or with status {@code 400 (Bad Request)} if the document is not valid,
     * or with status {@code 404 (Not Found)} if the document is not found,
     * or with status {@code 500 (Internal Server Error)} if the document couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Document> partialUpdateDocument(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Document document
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Document partially : {}, {}", id, document);
        if (document.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, document.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!documentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Document> result = documentService.partialUpdate(document);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, document.getId().toString())
        );
    }

    /**
     * {@code GET  /documents} : get all the documents.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of documents in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Document>> getAllDocuments(DocumentCriteria criteria) {
        LOG.debug("REST request to get Documents by criteria: {}", criteria);

        List<Document> entityList = documentQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /documents/count} : count all the documents.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDocuments(DocumentCriteria criteria) {
        LOG.debug("REST request to count Documents by criteria: {}", criteria);
        return ResponseEntity.ok().body(documentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /documents/:id} : get the "id" document.
     *
     * @param id the id of the document to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the document, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Document : {}", id);
        Optional<Document> document = documentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(document);
    }

    /**
     * {@code DELETE  /documents/:id} : delete the "id" document.
     *
     * @param id the id of the document to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Document : {}", id);
        documentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    // Validation d'un technicien par ADMIN
    @PutMapping("/admin/documents/{id}/validate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> validateDocument(@PathVariable Long id) {
        Optional<Document> document = documentService.findOne(id);

        if (document.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, HttpStatus.BAD_REQUEST.value(), "NOT_FOUND", "Document introuvable", null));
        }

        Document documentOpt = document.get();

        documentOpt.setValide(true);
        documentRepository.save(documentOpt);

        if (documentOpt.getTechnicien() != null && documentOpt.getTechnicien().getId() != null) {
            technicienRepository
                .findById(documentOpt.getTechnicien().getId())
                .ifPresent(technicien -> {
                    if (technicien.getStatutValidation() != StatutValidation.VALIDE) {
                        technicien.setStatutValidation(StatutValidation.VALIDE);
                        technicienRepository.save(technicien);
                    }
                    this.updateUserActivationAndNotify(technicien, true);
                });
        }

        return ResponseEntity.ok(
            new ApiResponse(
                true,
                HttpStatus.OK.value(),
                "DOCUMENT_VALIDATED",
                "Document validé par admin. Le technicien en attente a été validé automatiquement.",
                null
            )
        );
    }

    // Refus d'un document par ADMIN
    @PutMapping("/admin/documents/{id}/refuse")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> refuseDocument(@PathVariable Long id) {
        Optional<Document> document = documentService.findOne(id);

        if (document.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, HttpStatus.BAD_REQUEST.value(), "NOT_FOUND", "Document introuvable", null));
        }

        Document documentOpt = document.get();
        documentOpt.setValide(false);
        documentRepository.save(documentOpt);

        if (documentOpt.getTechnicien() != null && documentOpt.getTechnicien().getId() != null) {
            technicienRepository
                .findById(documentOpt.getTechnicien().getId())
                .ifPresent(technicien -> {
                    technicien.setStatutValidation(StatutValidation.REFUSE);
                    technicienRepository.save(technicien);
                    this.updateUserActivationAndNotify(technicien, false);
                });
        }

        return ResponseEntity.ok(
            new ApiResponse(
                true,
                HttpStatus.OK.value(),
                "DOCUMENT_REFUSED",
                "Document refusé par admin. Compte technicien bloqué et notification envoyée.",
                null
            )
        );
    }

    @GetMapping("/documents/download/{filename:.+}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable String filename) {
        try {
            // Chemin vers le fichier stocké
            Path filePath = Paths.get("uploads").resolve(filename).normalize();
            if (!Files.exists(filePath)) {
                throw new BadRequestAlertException("Fichier introuvable", "document", "NOT_FOUND");
            }

            // Créer la ressource à partir du fichier
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new BadRequestAlertException("Impossible de lire le fichier", "document", "UNREADABLE");
            }

            // Détecter le type MIME (image, pdf, etc.)
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream"; // fallback générique
            }

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
        } catch (IOException e) {
            throw new BadRequestAlertException("Erreur lors de l'accès au fichier", "document", "IO_ERROR");
        }
    }

    @GetMapping({ "/{id}/download", "/documents/{id}/download" })
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        Document doc = documentRepository.findById(id).orElseThrow(() -> new RuntimeException("Document non trouvé"));

        // Décompression
        byte[] decompressedData = FileCompressionUtil.decompress(doc.getFileData());
        String fileName = (doc.getFileName() == null || doc.getFileName().isBlank()) ? "document-" + id : doc.getFileName();
        String contentType = (doc.getContentType() == null || doc.getContentType().isBlank())
            ? MediaType.APPLICATION_OCTET_STREAM_VALUE
            : doc.getContentType();

        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + URLEncoder.encode(fileName, StandardCharsets.UTF_8)
            )
            .contentType(MediaType.parseMediaType(contentType))
            .body(decompressedData);
    }

    private void updateUserActivationAndNotify(Technicien technicien, boolean approved) {
        if (technicien.getUser() == null || technicien.getUser().getId() == null) {
            return;
        }

        userRepository
            .findById(technicien.getUser().getId())
            .ifPresent(user -> {
                user.setActivated(approved);
                userRepository.save(user);

                String fullName =
                    ((technicien.getPrenom() != null ? technicien.getPrenom() + " " : "") +
                        (technicien.getNom() != null ? technicien.getNom() : "")).trim();
                String message = approved
                    ? "Bonjour " + fullName + ", vos documents ont ete valides. Votre compte technicien est active."
                    : "Bonjour " + fullName + ", vos documents ont ete refuses. Votre compte technicien est bloque.";
                try {
                    smsService.sendMessage(user.getLogin(), message);
                } catch (Exception e) {
                    LOG.warn("Echec envoi SMS pour le technicien {}: {}", technicien.getId(), e.getMessage());
                }
            });
    }
}
