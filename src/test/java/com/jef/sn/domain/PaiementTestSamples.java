package com.jef.sn.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PaiementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Paiement getPaiementSample1() {
        return new Paiement().id(1L).reference("reference1");
    }

    public static Paiement getPaiementSample2() {
        return new Paiement().id(2L).reference("reference2");
    }

    public static Paiement getPaiementRandomSampleGenerator() {
        return new Paiement().id(longCount.incrementAndGet()).reference(UUID.randomUUID().toString());
    }
}
