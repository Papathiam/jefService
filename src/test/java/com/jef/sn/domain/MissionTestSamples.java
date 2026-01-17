package com.jef.sn.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MissionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Mission getMissionSample1() {
        return new Mission().id(1L).description("description1");
    }

    public static Mission getMissionSample2() {
        return new Mission().id(2L).description("description2");
    }

    public static Mission getMissionRandomSampleGenerator() {
        return new Mission().id(longCount.incrementAndGet()).description(UUID.randomUUID().toString());
    }
}
