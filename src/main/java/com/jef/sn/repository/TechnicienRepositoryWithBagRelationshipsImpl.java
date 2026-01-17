package com.jef.sn.repository;

import com.jef.sn.domain.Technicien;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class TechnicienRepositoryWithBagRelationshipsImpl implements TechnicienRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String TECHNICIENS_PARAMETER = "techniciens";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Technicien> fetchBagRelationships(Optional<Technicien> technicien) {
        return technicien.map(this::fetchServiceJefs);
    }

    @Override
    public Page<Technicien> fetchBagRelationships(Page<Technicien> techniciens) {
        return new PageImpl<>(fetchBagRelationships(techniciens.getContent()), techniciens.getPageable(), techniciens.getTotalElements());
    }

    @Override
    public List<Technicien> fetchBagRelationships(List<Technicien> techniciens) {
        return Optional.of(techniciens).map(this::fetchServiceJefs).orElse(Collections.emptyList());
    }

    Technicien fetchServiceJefs(Technicien result) {
        return entityManager
            .createQuery(
                "select technicien from Technicien technicien left join fetch technicien.serviceJefs where technicien.id = :id",
                Technicien.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Technicien> fetchServiceJefs(List<Technicien> techniciens) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, techniciens.size()).forEach(index -> order.put(techniciens.get(index).getId(), index));
        List<Technicien> result = entityManager
            .createQuery(
                "select technicien from Technicien technicien left join fetch technicien.serviceJefs where technicien in :techniciens",
                Technicien.class
            )
            .setParameter(TECHNICIENS_PARAMETER, techniciens)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
