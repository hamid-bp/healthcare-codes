package com.semdatex.catalogs.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.semdatex.catalogs.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OpsCodesDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OpsCodesDTO.class);
        OpsCodesDTO opsCodesDTO1 = new OpsCodesDTO();
        opsCodesDTO1.setId(1L);
        OpsCodesDTO opsCodesDTO2 = new OpsCodesDTO();
        assertThat(opsCodesDTO1).isNotEqualTo(opsCodesDTO2);
        opsCodesDTO2.setId(opsCodesDTO1.getId());
        assertThat(opsCodesDTO1).isEqualTo(opsCodesDTO2);
        opsCodesDTO2.setId(2L);
        assertThat(opsCodesDTO1).isNotEqualTo(opsCodesDTO2);
        opsCodesDTO1.setId(null);
        assertThat(opsCodesDTO1).isNotEqualTo(opsCodesDTO2);
    }
}
