package com.jef.sn.service;

import com.jef.sn.domain.*; // for static metamodels
import com.jef.sn.domain.Document;
import com.jef.sn.repository.DocumentRepository;
import com.jef.sn.service.criteria.DocumentCriteria;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Document} entities in the database.
 * The main input is a {@link DocumentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Document} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DocumentQueryService extends QueryService<Document> {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentQueryService.class);

    private final DocumentRepository documentRepository;

    public DocumentQueryService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    /**
     * Return a {@link List} of {@link Document} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Document> findByCriteria(DocumentCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Document> specification = createSpecification(criteria);
        return documentRepository.findAll(specification);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DocumentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Document> specification = createSpecification(criteria);
        return documentRepository.count(specification);
    }

    /**
     * Function to convert {@link DocumentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Document> createSpecification(DocumentCriteria criteria) {
        Specification<Document> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Document_.id),
                buildSpecification(criteria.getType(), Document_.type),
                buildSpecification(criteria.getValide(), Document_.valide),
                buildRangeSpecification(criteria.getDateUpload(), Document_.dateUpload),
                buildSpecification(criteria.getTechnicienId(), root -> root.join(Document_.technicien, JoinType.LEFT).get(Technicien_.id))
            );
        }
        return specification;
    }
}
