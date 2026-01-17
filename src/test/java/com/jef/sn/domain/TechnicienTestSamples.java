package com.jef.sn.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TechnicienTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Technicien getTechnicienSample1() {
        return new Technicien().id(1L).nom("nom1").prenom("prenom1");
    }

    public static Technicien getTechnicienSample2() {
        return new Technicien().id(2L).nom("nom2").prenom("prenom2");
    }

    public static Technicien getTechnicienRandomSampleGenerator() {
        return new Technicien().id(longCount.incrementAndGet()).nom(UUID.randomUUID().toString()).prenom(UUID.randomUUID().toString());
    }
}
