package com.jef.sn.repository;

import com.jef.sn.domain.Technicien;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface TechnicienRepositoryWithBagRelationships {
    Optional<Technicien> fetchBagRelationships(Optional<Technicien> technicien);

    List<Technicien> fetchBagRelationships(List<Technicien> techniciens);

    Page<Technicien> fetchBagRelationships(Page<Technicien> techniciens);
}
