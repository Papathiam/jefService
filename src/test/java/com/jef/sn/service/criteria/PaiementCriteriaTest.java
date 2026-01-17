package com.jef.sn.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PaiementCriteriaTest {

    @Test
    void newPaiementCriteriaHasAllFiltersNullTest() {
        var paiementCriteria = new PaiementCriteria();
        assertThat(paiementCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void paiementCriteriaFluentMethodsCreatesFiltersTest() {
        var paiementCriteria = new PaiementCriteria();

        setAllFilters(paiementCriteria);

        assertThat(paiementCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void paiementCriteriaCopyCreatesNullFilterTest() {
        var paiementCriteria = new PaiementCriteria();
        var copy = paiementCriteria.copy();

        assertThat(paiementCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(paiementCriteria)
        );
    }

    @Test
    void paiementCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var paiementCriteria = new PaiementCriteria();
        setAllFilters(paiementCriteria);

        var copy = paiementCriteria.copy();

        assertThat(paiementCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(paiementCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var paiementCriteria = new PaiementCriteria();

        assertThat(paiementCriteria).hasToString("PaiementCriteria{}");
    }

    private static void setAllFilters(PaiementCriteria paiementCriteria) {
        paiementCriteria.id();
        paiementCriteria.montantTotal();
        paiementCriteria.commission();
        paiementCriteria.montantTechnicien();
        paiementCriteria.moyenPaiement();
        paiementCriteria.statut();
        paiementCriteria.reference();
        paiementCriteria.missionId();
        paiementCriteria.distinct();
    }

    private static Condition<PaiementCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getMontantTotal()) &&
                condition.apply(criteria.getCommission()) &&
                condition.apply(criteria.getMontantTechnicien()) &&
                condition.apply(criteria.getMoyenPaiement()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getMissionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PaiementCriteria> copyFiltersAre(PaiementCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getMontantTotal(), copy.getMontantTotal()) &&
                condition.apply(criteria.getCommission(), copy.getCommission()) &&
                condition.apply(criteria.getMontantTechnicien(), copy.getMontantTechnicien()) &&
                condition.apply(criteria.getMoyenPaiement(), copy.getMoyenPaiement()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getMissionId(), copy.getMissionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
