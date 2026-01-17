package com.jef.sn.service.impl;

import com.jef.sn.domain.*;
import com.jef.sn.domain.enumeration.StatutValidation;
import com.jef.sn.domain.enumeration.TypeDocument;
import com.jef.sn.repository.DocumentRepository;
import com.jef.sn.repository.TechnicienRepository;
import com.jef.sn.service.DocumentService;
import com.jef.sn.service.dto.FileCompressionUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Implementation for managing {@link com.jef.sn.domain.Document}.
 */
@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentServiceImpl.class);

    private final DocumentRepository documentRepository;
    private final TechnicienRepository technicienRepository;

    public DocumentServiceImpl(DocumentRepository documentRepository, TechnicienRepository technicienRepository) {
        this.documentRepository = documentRepository;
        this.technicienRepository = technicienRepository;
    }

    @Override
    public Document save(Document document) {
        LOG.debug("Request to save Document : {}", document);
        Document savedDocument = documentRepository.save(document);
        syncTechnicienValidationStatus(savedDocument);
        return savedDocument;
    }

    @Override
    public Document update(Document document) {
        LOG.debug("Request to update Document : {}", document);
        Document savedDocument = documentRepository.save(document);
        syncTechnicienValidationStatus(savedDocument);
        return savedDocument;
    }

    @Override
    public Optional<Document> partialUpdate(Document document) {
        LOG.debug("Request to partially update Document : {}", document);

        return documentRepository
            .findById(document.getId())
            .map(existingDocument -> {
                if (document.getType() != null) {
                    existingDocument.setType(document.getType());
                }

                if (document.getValide() != null) {
                    existingDocument.setValide(document.getValide());
                }
                if (document.getDateUpload() != null) {
                    existingDocument.setDateUpload(document.getDateUpload());
                }
                if (document.getTechnicien() != null) {
                    existingDocument.setTechnicien(document.getTechnicien());
                }

                return existingDocument;
            })
            .map(documentRepository::save)
            .map(savedDocument -> {
                syncTechnicienValidationStatus(savedDocument);
                return savedDocument;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Document> findOne(Long id) {
        LOG.debug("Request to get Document : {}", id);
        return documentRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Document : {}", id);
        documentRepository.deleteById(id);
    }

    // ------------------ NOUVELLE MÉTHODE ------------------
    /**
     * Enregistre un fichier pour un Technicien avec type, dateUpload et valide=false
     */
    @Override
    public Document saveFile(MultipartFile file, Technicien technicien, TypeDocument type) {
        LOG.debug("Request to save file for Technicien {} : {}", technicien.getId(), file.getOriginalFilename());

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide ou invalide");
        }
        try {
            // Nettoyage du nom de fichier
            String originalFilename = file.getOriginalFilename() != null
                ? file.getOriginalFilename().replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9._-]", "")
                : "document";

            // Compression
            byte[] compressedData = FileCompressionUtil.compress(file.getBytes());

            // Création Document
            Document document = new Document();
            document.setType(type);
            document.setTechnicien(technicien);
            document.setFileName(originalFilename);
            document.setContentType(file.getContentType());
            document.setFileData(compressedData);
            document.setValide(false);
            document.setDateUpload(Instant.now());

            return documentRepository.save(document);
        } catch (Exception e) {
            LOG.error("Erreur lors de l'enregistrement du fichier", e);
            throw new RuntimeException("Impossible d'enregistrer le fichier " + file.getOriginalFilename(), e);
        }
    }

    private void syncTechnicienValidationStatus(Document document) {
        if (document == null || document.getValide() == null || !document.getValide()) {
            return;
        }
        if (document.getTechnicien() == null || document.getTechnicien().getId() == null) {
            return;
        }

        technicienRepository
            .findById(document.getTechnicien().getId())
            .ifPresent(technicien -> {
                if (technicien.getStatutValidation() != StatutValidation.VALIDE) {
                    technicien.setStatutValidation(StatutValidation.VALIDE);
                    technicienRepository.save(technicien);
                    LOG.info("Technicien {} validé automatiquement après validation du document {}", technicien.getId(), document.getId());
                }
            });
    }
}
