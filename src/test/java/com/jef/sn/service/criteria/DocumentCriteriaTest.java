package com.jef.sn.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DocumentCriteriaTest {

    @Test
    void newDocumentCriteriaHasAllFiltersNullTest() {
        var documentCriteria = new DocumentCriteria();
        assertThat(documentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void documentCriteriaFluentMethodsCreatesFiltersTest() {
        var documentCriteria = new DocumentCriteria();

        setAllFilters(documentCriteria);

        assertThat(documentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void documentCriteriaCopyCreatesNullFilterTest() {
        var documentCriteria = new DocumentCriteria();
        var copy = documentCriteria.copy();

        assertThat(documentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(documentCriteria)
        );
    }

    @Test
    void documentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var documentCriteria = new DocumentCriteria();
        setAllFilters(documentCriteria);

        var copy = documentCriteria.copy();

        assertThat(documentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(documentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var documentCriteria = new DocumentCriteria();

        assertThat(documentCriteria).hasToString("DocumentCriteria{}");
    }

    private static void setAllFilters(DocumentCriteria documentCriteria) {
        documentCriteria.id();
        documentCriteria.type();
        documentCriteria.urlFichier();
        documentCriteria.valide();
        documentCriteria.dateUpload();
        documentCriteria.technicienId();
        documentCriteria.distinct();
    }

    private static Condition<DocumentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getUrlFichier()) &&
                condition.apply(criteria.getValide()) &&
                condition.apply(criteria.getDateUpload()) &&
                condition.apply(criteria.getTechnicienId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DocumentCriteria> copyFiltersAre(DocumentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getUrlFichier(), copy.getUrlFichier()) &&
                condition.apply(criteria.getValide(), copy.getValide()) &&
                condition.apply(criteria.getDateUpload(), copy.getDateUpload()) &&
                condition.apply(criteria.getTechnicienId(), copy.getTechnicienId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
