package com.jef.sn.repository;

import com.jef.sn.domain.Technicien;
import com.jef.sn.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Technicien entity.
 *
 * When extending this class, extend TechnicienRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface TechnicienRepository
    extends TechnicienRepositoryWithBagRelationships, JpaRepository<Technicien, Long>, JpaSpecificationExecutor<Technicien> {
    default Optional<Technicien> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Technicien> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Technicien> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    Optional<Technicien> findOneByUser(User user);
}
