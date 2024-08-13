package com.semdatex.catalogs.service.mapper;

import static com.semdatex.catalogs.domain.OpsCodesAsserts.*;
import static com.semdatex.catalogs.domain.OpsCodesTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OpsCodesMapperTest {

    private OpsCodesMapper opsCodesMapper;

    @BeforeEach
    void setUp() {
        opsCodesMapper = new OpsCodesMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOpsCodesSample1();
        var actual = opsCodesMapper.toEntity(opsCodesMapper.toDto(expected));
        assertOpsCodesAllPropertiesEquals(expected, actual);
    }
}
