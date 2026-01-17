package com.jef.sn.service;

import java.time.Instant;

public class OtpEntry {

    private final String otp;
    private final Instant expiresAt;

    public OtpEntry(String otp, Instant expiresAt) {
        this.otp = otp;
        this.expiresAt = expiresAt;
    }

    public String getOtp() {
        return otp;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
