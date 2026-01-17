package com.jef.sn.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.from-number}")
    private String fromNumber;

    @PostConstruct
    void init() {
        Twilio.init(accountSid, authToken);
    }

    public void sendOtp(String phoneNumber, String otp) {
        sendMessage(phoneNumber, "Votre OTP est : " + otp);
    }

    public void sendMessage(String phoneNumber, String messageBody) {
        // Assure-toi que le numéro client est en format international
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+221" + phoneNumber; // Sénégal
        }

        Message message = Message.creator(
            new PhoneNumber(phoneNumber), // To
            new PhoneNumber(fromNumber), // From (ton Twilio validé)
            messageBody
        ).create();

        System.out.println("SMS envoyé à " + phoneNumber + " : " + message.getSid());
    }
}
