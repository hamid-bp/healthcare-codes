package com.semdatex.catalogs.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OpsCodesTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static OpsCodes getOpsCodesSample1() {
        return new OpsCodes().id(1L).code("code1").description("description1");
    }

    public static OpsCodes getOpsCodesSample2() {
        return new OpsCodes().id(2L).code("code2").description("description2");
    }

    public static OpsCodes getOpsCodesRandomSampleGenerator() {
        return new OpsCodes().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
