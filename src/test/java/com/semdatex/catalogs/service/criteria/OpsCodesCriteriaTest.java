package com.semdatex.catalogs.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class OpsCodesCriteriaTest {

    @Test
    void newOpsCodesCriteriaHasAllFiltersNullTest() {
        var opsCodesCriteria = new OpsCodesCriteria();
        assertThat(opsCodesCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void opsCodesCriteriaFluentMethodsCreatesFiltersTest() {
        var opsCodesCriteria = new OpsCodesCriteria();

        setAllFilters(opsCodesCriteria);

        assertThat(opsCodesCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void opsCodesCriteriaCopyCreatesNullFilterTest() {
        var opsCodesCriteria = new OpsCodesCriteria();
        var copy = opsCodesCriteria.copy();

        assertThat(opsCodesCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(opsCodesCriteria)
        );
    }

    @Test
    void opsCodesCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var opsCodesCriteria = new OpsCodesCriteria();
        setAllFilters(opsCodesCriteria);

        var copy = opsCodesCriteria.copy();

        assertThat(opsCodesCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(opsCodesCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var opsCodesCriteria = new OpsCodesCriteria();

        assertThat(opsCodesCriteria).hasToString("OpsCodesCriteria{}");
    }

    private static void setAllFilters(OpsCodesCriteria opsCodesCriteria) {
        opsCodesCriteria.id();
        opsCodesCriteria.code();
        opsCodesCriteria.description();
        opsCodesCriteria.distinct();
    }

    private static Condition<OpsCodesCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<OpsCodesCriteria> copyFiltersAre(OpsCodesCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
