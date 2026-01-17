package com.jef.sn.service;

import com.jef.sn.domain.*; // for static metamodels
import com.jef.sn.domain.Technicien;
import com.jef.sn.repository.TechnicienRepository;
import com.jef.sn.service.criteria.TechnicienCriteria;
import com.jef.sn.service.dto.TechnicienDTO;
import com.jef.sn.service.mapper.TechnicienMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Technicien} entities in the database.
 * The main input is a {@link TechnicienCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TechnicienDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TechnicienQueryService extends QueryService<Technicien> {

    private static final Logger LOG = LoggerFactory.getLogger(TechnicienQueryService.class);

    private final TechnicienRepository technicienRepository;

    private final TechnicienMapper technicienMapper;

    public TechnicienQueryService(TechnicienRepository technicienRepository, TechnicienMapper technicienMapper) {
        this.technicienRepository = technicienRepository;
        this.technicienMapper = technicienMapper;
    }

    /**
     * Return a {@link Page} of {@link TechnicienDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TechnicienDTO> findByCriteria(TechnicienCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Technicien> specification = createSpecification(criteria);
        return technicienRepository.fetchBagRelationships(technicienRepository.findAll(specification, page)).map(technicienMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TechnicienCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Technicien> specification = createSpecification(criteria);
        return technicienRepository.count(specification);
    }

    /**
     * Function to convert {@link TechnicienCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Technicien> createSpecification(TechnicienCriteria criteria) {
        Specification<Technicien> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Technicien_.id),
                buildStringSpecification(criteria.getNom(), Technicien_.nom),
                buildStringSpecification(criteria.getPrenom(), Technicien_.prenom),
                buildRangeSpecification(criteria.getTarifMin(), Technicien_.tarifMin),
                buildRangeSpecification(criteria.getTarifMax(), Technicien_.tarifMax),
                buildRangeSpecification(criteria.getLatitude(), Technicien_.latitude),
                buildRangeSpecification(criteria.getLongitude(), Technicien_.longitude),
                buildSpecification(criteria.getDisponible(), Technicien_.disponible),
                buildSpecification(criteria.getStatutValidation(), Technicien_.statutValidation),
                buildRangeSpecification(criteria.getNoteMoyenne(), Technicien_.noteMoyenne),
                buildSpecification(criteria.getUserId(), root -> root.join(Technicien_.user, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getDocumentId(), root -> root.join(Technicien_.documents, JoinType.LEFT).get(Document_.id)),
                buildSpecification(criteria.getMissionId(), root -> root.join(Technicien_.missions, JoinType.LEFT).get(Mission_.id)),
                buildSpecification(criteria.getServiceJefsId(), root ->
                    root.join(Technicien_.serviceJefs, JoinType.LEFT).get(ServiceJef_.id)
                )
            );
        }
        return specification;
    }
}
