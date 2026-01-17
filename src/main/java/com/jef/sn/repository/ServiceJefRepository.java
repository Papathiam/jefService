package com.jef.sn.repository;

import com.jef.sn.domain.ServiceJef;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ServiceJef entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ServiceJefRepository extends JpaRepository<ServiceJef, Long>, JpaSpecificationExecutor<ServiceJef> {}
