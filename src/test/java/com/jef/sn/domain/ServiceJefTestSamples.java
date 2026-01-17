package com.jef.sn.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ServiceJefTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ServiceJef getServiceJefSample1() {
        return new ServiceJef().id(1L).nom("nom1").description("description1");
    }

    public static ServiceJef getServiceJefSample2() {
        return new ServiceJef().id(2L).nom("nom2").description("description2");
    }

    public static ServiceJef getServiceJefRandomSampleGenerator() {
        return new ServiceJef().id(longCount.incrementAndGet()).nom(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
