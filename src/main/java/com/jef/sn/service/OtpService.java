package com.jef.sn.service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private final SmsService smsService;

    // Cache OTP en mémoire
    private final Map<String, OtpEntry> otpCache = new ConcurrentHashMap<>();

    public OtpService(SmsService smsService) {
        this.smsService = smsService;
    }

    public void generateAndSendOtp(String phoneNumber) {
        //Pour 4 chiffres
        String otp = String.format("%04d", new Random().nextInt(10000));

        Instant expiresAt = Instant.now().plusSeconds(500); // 5 min

        otpCache.put(phoneNumber, new OtpEntry(otp, expiresAt));
        smsService.sendOtp(phoneNumber, otp);
    }

    public boolean validateOtp(String phoneNumber, String otp) {
        OtpEntry entry = otpCache.get(phoneNumber);

        if (entry == null) {
            return false;
        }

        if (Instant.now().isAfter(entry.getExpiresAt())) {
            otpCache.remove(phoneNumber);
            return false;
        }

        boolean valid = entry.getOtp().equals(otp);

        if (valid) {
            otpCache.remove(phoneNumber); // usage unique
        }

        return valid;
    }
}
