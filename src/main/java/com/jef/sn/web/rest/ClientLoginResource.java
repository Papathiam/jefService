package com.jef.sn.web.rest;

import com.jef.sn.domain.User;
import com.jef.sn.repository.UserRepository;
import com.jef.sn.service.OtpService;
import com.jef.sn.service.UserService;
import com.jef.sn.service.dto.ValidateOtpDTO;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ClientLoginResource {

    private final Logger log = LoggerFactory.getLogger(ClientLoginResource.class);

    private final UserService userService;
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final AuthenticateController authenticateController;

    public ClientLoginResource(
        UserService userService,
        UserRepository userRepository,
        OtpService otpService,
        AuthenticateController authenticateController
    ) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.authenticateController = authenticateController;
    }

    // =======================
    // Étape 1 : Demander OTP pour login
    // =======================
    @PostMapping("/login-otp-request")
    public ResponseEntity<?> requestLoginOtp(@RequestBody Map<String, String> body) {
        String phoneNumber = body.get("phoneNumber");
        log.debug("Demande OTP login pour : {}", phoneNumber);

        if (userRepository.findOneByLogin(phoneNumber).isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Numéro inconnu"));
        }

        otpService.generateAndSendOtp(phoneNumber);

        return ResponseEntity.ok(Map.of("message", "OTP envoyé sur " + phoneNumber));
    }

    // =======================
    // Étape 2 : Valider OTP + JWT
    // =======================
    @PostMapping("/login-otp-validate")
    @Transactional
    public ResponseEntity<?> validateLoginOtp(@Valid @RequestBody ValidateOtpDTO dto) {
        log.debug("Validation OTP login pour : {}", dto.getPhoneNumber());

        if (!otpService.validateOtp(dto.getPhoneNumber(), dto.getOtp())) {
            return ResponseEntity.badRequest().body(Map.of("message", "OTP invalide"));
        }

        User user = userService
            .getUserWithAuthoritiesByLogin(dto.getPhoneNumber())
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // 🔑 Création JWT JHipster
        String jwt = createJwtForUser(user);

        return ResponseEntity.ok(Map.of("token", jwt, "message", "Connexion réussie"));
    }

    // =======================
    // Génération JWT JHipster
    // =======================
    private String createJwtForUser(User user) {
        var authorities = user.getAuthorities().stream().map(a -> new SimpleGrantedAuthority(a.getName())).toList();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getLogin(), null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authenticateController.createToken(authentication, false);
    }
}
