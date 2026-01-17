package com.jef.sn.service;

import com.jef.sn.domain.*; // for static metamodels
import com.jef.sn.domain.Mission;
import com.jef.sn.repository.MissionRepository;
import com.jef.sn.service.criteria.MissionCriteria;
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
 * Service for executing complex queries for {@link Mission} entities in the database.
 * The main input is a {@link MissionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Mission} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MissionQueryService extends QueryService<Mission> {

    private static final Logger LOG = LoggerFactory.getLogger(MissionQueryService.class);

    private final MissionRepository missionRepository;

    public MissionQueryService(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
    }

    /**
     * Return a {@link Page} of {@link Mission} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Mission> findByCriteria(MissionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Mission> specification = createSpecification(criteria);
        return missionRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MissionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Mission> specification = createSpecification(criteria);
        return missionRepository.count(specification);
    }

    /**
     * Function to convert {@link MissionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Mission> createSpecification(MissionCriteria criteria) {
        Specification<Mission> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Mission_.id),
                buildStringSpecification(criteria.getDescription(), Mission_.description),
                buildRangeSpecification(criteria.getLatitude(), Mission_.latitude),
                buildRangeSpecification(criteria.getLongitude(), Mission_.longitude),
                buildSpecification(criteria.getStatut(), Mission_.statut),
                buildRangeSpecification(criteria.getPrixPropose(), Mission_.prixPropose),
                buildRangeSpecification(criteria.getDateCreation(), Mission_.dateCreation),
                buildSpecification(criteria.getPaiementId(), root -> root.join(Mission_.paiement, JoinType.LEFT).get(Paiement_.id)),
                buildSpecification(criteria.getServiceJefId(), root -> root.join(Mission_.serviceJef, JoinType.LEFT).get(ServiceJef_.id)),
                buildSpecification(criteria.getClientId(), root -> root.join(Mission_.client, JoinType.LEFT).get(Client_.id)),
                buildSpecification(criteria.getTechnicienId(), root -> root.join(Mission_.technicien, JoinType.LEFT).get(Technicien_.id))
            );
        }
        return specification;
    }
}
