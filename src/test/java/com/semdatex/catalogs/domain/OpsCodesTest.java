package com.semdatex.catalogs.domain;

import static com.semdatex.catalogs.domain.OpsCodesTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.semdatex.catalogs.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OpsCodesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OpsCodes.class);
        OpsCodes opsCodes1 = getOpsCodesSample1();
        OpsCodes opsCodes2 = new OpsCodes();
        assertThat(opsCodes1).isNotEqualTo(opsCodes2);

        opsCodes2.setId(opsCodes1.getId());
        assertThat(opsCodes1).isEqualTo(opsCodes2);

        opsCodes2 = getOpsCodesSample2();
        assertThat(opsCodes1).isNotEqualTo(opsCodes2);
    }
}
