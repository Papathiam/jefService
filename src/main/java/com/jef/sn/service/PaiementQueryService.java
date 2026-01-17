package com.jef.sn.service;

import com.jef.sn.domain.*; // for static metamodels
import com.jef.sn.domain.Paiement;
import com.jef.sn.repository.PaiementRepository;
import com.jef.sn.service.criteria.PaiementCriteria;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Paiement} entities in the database.
 * The main input is a {@link PaiementCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Paiement} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PaiementQueryService extends QueryService<Paiement> {

    private static final Logger LOG = LoggerFactory.getLogger(PaiementQueryService.class);

    private final PaiementRepository paiementRepository;

    public PaiementQueryService(PaiementRepository paiementRepository) {
        this.paiementRepository = paiementRepository;
    }

    /**
     * Return a {@link List} of {@link Paiement} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Paiement> findByCriteria(PaiementCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Paiement> specification = createSpecification(criteria);
        return paiementRepository.findAll(specification);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PaiementCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Paiement> specification = createSpecification(criteria);
        return paiementRepository.count(specification);
    }

    /**
     * Function to convert {@link PaiementCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Paiement> createSpecification(PaiementCriteria criteria) {
        Specification<Paiement> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Paiement_.id),
                buildRangeSpecification(criteria.getMontantTotal(), Paiement_.montantTotal),
                buildRangeSpecification(criteria.getCommission(), Paiement_.commission),
                buildRangeSpecification(criteria.getMontantTechnicien(), Paiement_.montantTechnicien),
                buildSpecification(criteria.getMoyenPaiement(), Paiement_.moyenPaiement),
                buildSpecification(criteria.getStatut(), Paiement_.statut),
                buildStringSpecification(criteria.getReference(), Paiement_.reference),
                buildSpecification(criteria.getMissionId(), root -> root.join(Paiement_.mission, JoinType.LEFT).get(Mission_.id))
            );
        }
        return specification;
    }
}
