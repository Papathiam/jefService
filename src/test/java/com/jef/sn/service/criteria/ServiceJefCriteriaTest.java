package com.jef.sn.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ServiceJefCriteriaTest {

    @Test
    void newServiceJefCriteriaHasAllFiltersNullTest() {
        var serviceJefCriteria = new ServiceJefCriteria();
        assertThat(serviceJefCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void serviceJefCriteriaFluentMethodsCreatesFiltersTest() {
        var serviceJefCriteria = new ServiceJefCriteria();

        setAllFilters(serviceJefCriteria);

        assertThat(serviceJefCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void serviceJefCriteriaCopyCreatesNullFilterTest() {
        var serviceJefCriteria = new ServiceJefCriteria();
        var copy = serviceJefCriteria.copy();

        assertThat(serviceJefCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(serviceJefCriteria)
        );
    }

    @Test
    void serviceJefCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var serviceJefCriteria = new ServiceJefCriteria();
        setAllFilters(serviceJefCriteria);

        var copy = serviceJefCriteria.copy();

        assertThat(serviceJefCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(serviceJefCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var serviceJefCriteria = new ServiceJefCriteria();

        assertThat(serviceJefCriteria).hasToString("ServiceJefCriteria{}");
    }

    private static void setAllFilters(ServiceJefCriteria serviceJefCriteria) {
        serviceJefCriteria.id();
        serviceJefCriteria.nom();
        serviceJefCriteria.description();
        serviceJefCriteria.actif();
        serviceJefCriteria.missionsId();
        serviceJefCriteria.techniciensId();
        serviceJefCriteria.distinct();
    }

    private static Condition<ServiceJefCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNom()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getActif()) &&
                condition.apply(criteria.getMissionsId()) &&
                condition.apply(criteria.getTechniciensId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ServiceJefCriteria> copyFiltersAre(ServiceJefCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNom(), copy.getNom()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getActif(), copy.getActif()) &&
                condition.apply(criteria.getMissionsId(), copy.getMissionsId()) &&
                condition.apply(criteria.getTechniciensId(), copy.getTechniciensId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
