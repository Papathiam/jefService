package com.jef.sn.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TechnicienCriteriaTest {

    @Test
    void newTechnicienCriteriaHasAllFiltersNullTest() {
        var technicienCriteria = new TechnicienCriteria();
        assertThat(technicienCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void technicienCriteriaFluentMethodsCreatesFiltersTest() {
        var technicienCriteria = new TechnicienCriteria();

        setAllFilters(technicienCriteria);

        assertThat(technicienCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void technicienCriteriaCopyCreatesNullFilterTest() {
        var technicienCriteria = new TechnicienCriteria();
        var copy = technicienCriteria.copy();

        assertThat(technicienCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(technicienCriteria)
        );
    }

    @Test
    void technicienCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var technicienCriteria = new TechnicienCriteria();
        setAllFilters(technicienCriteria);

        var copy = technicienCriteria.copy();

        assertThat(technicienCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(technicienCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var technicienCriteria = new TechnicienCriteria();

        assertThat(technicienCriteria).hasToString("TechnicienCriteria{}");
    }

    private static void setAllFilters(TechnicienCriteria technicienCriteria) {
        technicienCriteria.id();
        technicienCriteria.nom();
        technicienCriteria.prenom();
        technicienCriteria.tarifMin();
        technicienCriteria.tarifMax();
        technicienCriteria.latitude();
        technicienCriteria.longitude();
        technicienCriteria.disponible();
        technicienCriteria.statutValidation();
        technicienCriteria.noteMoyenne();
        technicienCriteria.userId();
        technicienCriteria.documentId();
        technicienCriteria.missionId();
        technicienCriteria.serviceJefsId();
        technicienCriteria.distinct();
    }

    private static Condition<TechnicienCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNom()) &&
                condition.apply(criteria.getPrenom()) &&
                condition.apply(criteria.getTarifMin()) &&
                condition.apply(criteria.getTarifMax()) &&
                condition.apply(criteria.getLatitude()) &&
                condition.apply(criteria.getLongitude()) &&
                condition.apply(criteria.getDisponible()) &&
                condition.apply(criteria.getStatutValidation()) &&
                condition.apply(criteria.getNoteMoyenne()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDocumentId()) &&
                condition.apply(criteria.getMissionId()) &&
                condition.apply(criteria.getServiceJefsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TechnicienCriteria> copyFiltersAre(TechnicienCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNom(), copy.getNom()) &&
                condition.apply(criteria.getPrenom(), copy.getPrenom()) &&
                condition.apply(criteria.getTarifMin(), copy.getTarifMin()) &&
                condition.apply(criteria.getTarifMax(), copy.getTarifMax()) &&
                condition.apply(criteria.getLatitude(), copy.getLatitude()) &&
                condition.apply(criteria.getLongitude(), copy.getLongitude()) &&
                condition.apply(criteria.getDisponible(), copy.getDisponible()) &&
                condition.apply(criteria.getStatutValidation(), copy.getStatutValidation()) &&
                condition.apply(criteria.getNoteMoyenne(), copy.getNoteMoyenne()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDocumentId(), copy.getDocumentId()) &&
                condition.apply(criteria.getMissionId(), copy.getMissionId()) &&
                condition.apply(criteria.getServiceJefsId(), copy.getServiceJefsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
