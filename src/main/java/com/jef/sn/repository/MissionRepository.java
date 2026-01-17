package com.jef.sn.repository;

import com.jef.sn.domain.Mission;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Mission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MissionRepository extends JpaRepository<Mission, Long>, JpaSpecificationExecutor<Mission> {}
