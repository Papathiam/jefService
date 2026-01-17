package com.jef.sn.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class MissionCriteriaTest {

    @Test
    void newMissionCriteriaHasAllFiltersNullTest() {
        var missionCriteria = new MissionCriteria();
        assertThat(missionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void missionCriteriaFluentMethodsCreatesFiltersTest() {
        var missionCriteria = new MissionCriteria();

        setAllFilters(missionCriteria);

        assertThat(missionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void missionCriteriaCopyCreatesNullFilterTest() {
        var missionCriteria = new MissionCriteria();
        var copy = missionCriteria.copy();

        assertThat(missionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(missionCriteria)
        );
    }

    @Test
    void missionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var missionCriteria = new MissionCriteria();
        setAllFilters(missionCriteria);

        var copy = missionCriteria.copy();

        assertThat(missionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(missionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var missionCriteria = new MissionCriteria();

        assertThat(missionCriteria).hasToString("MissionCriteria{}");
    }

    private static void setAllFilters(MissionCriteria missionCriteria) {
        missionCriteria.id();
        missionCriteria.description();
        missionCriteria.latitude();
        missionCriteria.longitude();
        missionCriteria.statut();
        missionCriteria.prixPropose();
        missionCriteria.dateCreation();
        missionCriteria.paiementId();
        missionCriteria.serviceJefId();
        missionCriteria.clientId();
        missionCriteria.technicienId();
        missionCriteria.distinct();
    }

    private static Condition<MissionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getLatitude()) &&
                condition.apply(criteria.getLongitude()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getPrixPropose()) &&
                condition.apply(criteria.getDateCreation()) &&
                condition.apply(criteria.getPaiementId()) &&
                condition.apply(criteria.getServiceJefId()) &&
                condition.apply(criteria.getClientId()) &&
                condition.apply(criteria.getTechnicienId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<MissionCriteria> copyFiltersAre(MissionCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getLatitude(), copy.getLatitude()) &&
                condition.apply(criteria.getLongitude(), copy.getLongitude()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getPrixPropose(), copy.getPrixPropose()) &&
                condition.apply(criteria.getDateCreation(), copy.getDateCreation()) &&
                condition.apply(criteria.getPaiementId(), copy.getPaiementId()) &&
                condition.apply(criteria.getServiceJefId(), copy.getServiceJefId()) &&
                condition.apply(criteria.getClientId(), copy.getClientId()) &&
                condition.apply(criteria.getTechnicienId(), copy.getTechnicienId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
