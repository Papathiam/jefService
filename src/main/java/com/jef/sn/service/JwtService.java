package com.jef.sn.service;

import com.jef.sn.domain.User;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    // Ici tu peux utiliser JJWT ou JHipster JWT util
    public String createToken(User user) {
        // Pour tester, retourne juste login + timestamp
        return user.getLogin() + "-fake-jwt-" + System.currentTimeMillis();
    }
}
