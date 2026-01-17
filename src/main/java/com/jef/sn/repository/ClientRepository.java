package com.jef.sn.repository;

import com.jef.sn.domain.Client;
import com.jef.sn.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Client entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {
    Optional<Client> findByUser(User user);
}
