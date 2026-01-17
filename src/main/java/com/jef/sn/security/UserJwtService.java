package com.jef.sn.security;

import static com.jef.sn.security.SecurityUtils.AUTHORITIES_CLAIM;
import static com.jef.sn.security.SecurityUtils.JWT_ALGORITHM;
import static com.jef.sn.security.SecurityUtils.USER_ID_CLAIM;

import com.jef.sn.security.DomainUserDetailsService.UserWithId;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

@Service
public class UserJwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds}")
    private long tokenValidityInSeconds;

    public UserJwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        Instant now = Instant.now();
        Instant validity = now.plus(tokenValidityInSeconds, ChronoUnit.SECONDS);

        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(validity)
            .subject(authentication.getName())
            .claim(AUTHORITIES_CLAIM, authorities);

        if (authentication.getPrincipal() instanceof UserWithId user) {
            claims.claim(USER_ID_CLAIM, user.getId());
        }

        JwsHeader header = JwsHeader.with(JWT_ALGORITHM).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
    }
}
