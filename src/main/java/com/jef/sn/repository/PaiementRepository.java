package com.jef.sn.repository;

import com.jef.sn.domain.Paiement;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Paiement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long>, JpaSpecificationExecutor<Paiement> {}
