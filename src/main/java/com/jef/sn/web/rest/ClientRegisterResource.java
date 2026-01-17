package com.jef.sn.web.rest;

import com.jef.sn.domain.Client;
import com.jef.sn.domain.Technicien;
import com.jef.sn.domain.User;
import com.jef.sn.domain.enumeration.*;
import com.jef.sn.repository.*;
import com.jef.sn.service.ClientService;
import com.jef.sn.service.DocumentService;
import com.jef.sn.service.OtpService;
import com.jef.sn.service.UserService;
import com.jef.sn.service.dto.*;
import com.jef.sn.service.dto.RegisterClientDTO;
import com.jef.sn.service.dto.ValidateOtpDTO;
import com.jef.sn.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for public client registration.
 */
@RestController
@RequestMapping("/api")
public class ClientRegisterResource {

    private final Logger log = LoggerFactory.getLogger(ClientRegisterResource.class);

    private final UserService userService;
    private final UserRepository userRepository;
    private final ClientService clientService;

    private final ClientRepository clientRepository;

    private final TechnicienRepository technicienRepository;

    private final DocumentService documentService;
    private final OtpService otpService;
    private final JwtEncoder jwtEncoder;

    private final AuthenticateController authenticateController;

    public ClientRegisterResource(
        UserService userService,
        UserRepository userRepository,
        ClientService clientService,
        OtpService otpService,
        JwtEncoder jwtEncoder,
        ClientRepository clientRepository,
        AuthenticateController authenticateController,
        TechnicienRepository technicienRepository,
        DocumentService documentService
    ) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.clientService = clientService;
        this.otpService = otpService;
        this.jwtEncoder = jwtEncoder;
        this.clientRepository = clientRepository;
        this.authenticateController = authenticateController;
        this.technicienRepository = technicienRepository;
        this.documentService = documentService;
    }

    /**
     * Étape 1 : Inscription / génération OTP
     */
    @PostMapping("/register-client")
    public ResponseEntity<ApiResponse> registerClient(@Valid @RequestBody RegisterClientDTO dto) {
        log.debug("Inscription client : {}", dto.getPhoneNumber());

        //Cas : numéro déjà existant → CONNEXION
        if (userRepository.findOneByLogin(dto.getPhoneNumber()).isPresent()) {
            // 📲 Envoi OTP (connexion)
            otpService.generateAndSendOtp(dto.getPhoneNumber());

            return ResponseEntity.ok(
                new ApiResponse(
                    true,
                    HttpStatus.OK.value(),
                    "OTP_LOGIN_SENT",
                    "OTP envoyé pour la connexion",
                    Map.of("phoneNumber", dto.getPhoneNumber())
                )
            );
        }

        // Cas : nouvel utilisateur → INSCRIPTION
        otpService.generateAndSendOtp(dto.getPhoneNumber());

        return ResponseEntity.ok(
            new ApiResponse(
                true,
                HttpStatus.OK.value(),
                "OTP_REGISTER_SENT",
                "OTP envoyé pour l'inscription",
                Map.of("phoneNumber", dto.getPhoneNumber())
            )
        );
    }

    /**
     * Génération JWT Spring Security (OAuth2 Resource Server)
     */
    private String generateJwt(User user) {
        Instant now = Instant.now();

        String scope = user.getAuthorities().stream().map(a -> a.getName()).collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("jef-service")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(86400)) // 24h
            .subject(user.getLogin())
            .claim("scope", scope)
            .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @PostMapping("/validate-otp")
    @Transactional
    public ResponseEntity<ApiResponse> validateOtp(@Valid @RequestBody ValidateOtpDTO dto) {
        // ❌ OTP invalide
        if (!otpService.validateOtp(dto.getPhoneNumber(), dto.getOtp())) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, HttpStatus.BAD_REQUEST.value(), "OTP_INVALID", "OTP invalide", null));
        }

        // 🔍 Vérifier si l'utilisateur existe
        Optional<User> existingUser = userRepository.findOneByLogin(dto.getPhoneNumber());

        User user;
        Client client = null;
        boolean isRegistration = false;

        // 🆕 INSCRIPTION
        if (existingUser.isEmpty()) {
            user = userService.createClientUser(dto.getPhoneNumber());

            client = new Client();
            client.setUser(user);
            client.setLatitude(dto.getLatitude());
            client.setLongitude(dto.getLongitude());
            clientRepository.save(client);

            isRegistration = true;
        }
        // 🔐 CONNEXION
        else {
            user = existingUser.get();
            client = clientRepository.findByUser(user).orElse(null);
        }

        // 🔑 Génération JWT
        String jwt = createJwtForUser(user);

        return ResponseEntity.ok(
            new ApiResponse(
                true,
                HttpStatus.OK.value(),
                isRegistration ? "REGISTER_SUCCESS" : "LOGIN_SUCCESS",
                isRegistration ? "Inscription réussie" : "Connexion réussie",
                Map.of("token", jwt, "clientId", client != null ? client.getId() : null)
            )
        );
    }

    private String createJwtForUser(User user) {
        var authorities = user.getAuthorities().stream().map(a -> new SimpleGrantedAuthority(a.getName())).toList();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getLogin(), null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authenticateController.createToken(authentication, false);
    }

    @PostMapping("/technicien/validate-otp")
    @Transactional
    public ResponseEntity<ApiResponse> validateOtpTechnicien(@Valid @RequestBody ValidateTechnicienOtpDTO dto) {
        // ❌ OTP invalide
        if (!otpService.validateOtp(dto.getPhoneNumber(), dto.getOtp())) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, HttpStatus.BAD_REQUEST.value(), "OTP_INVALID", "OTP invalide", null));
        }

        boolean userExists = userRepository.findOneByLogin(dto.getPhoneNumber()).isPresent();

        User user = userService.createTechnicienUser(dto.getPhoneNumber());

        Technicien technicien = technicienRepository
            .findOneByUser(user)
            .orElseGet(() -> {
                Technicien t = new Technicien();
                t.setUser(user);
                t.setNom(dto.getNom());
                t.setPrenom(dto.getPrenom());
                t.setTarifMin(dto.getTarifMin());
                t.setTarifMax(dto.getTarifMax());
                t.setLatitude(dto.getLatitude());
                t.setLongitude(dto.getLongitude());
                t.setDisponible(false);
                t.setStatutValidation(StatutValidation.EN_ATTENTE);
                return technicienRepository.save(t);
            });

        String jwt = createJwtForUser(user);

        return ResponseEntity.ok(
            new ApiResponse(
                true,
                HttpStatus.OK.value(),
                userExists ? "LOGIN_SUCCESS" : "TECHNICIEN_REGISTERED",
                userExists ? "Connexion réussie" : "Inscription technicien en attente de validation admin",
                Map.of("token", jwt, "technicienId", technicien.getId(), "statut", technicien.getStatutValidation())
            )
        );
    }

    // Upload documents techniciens
    @PostMapping(value = "/technicien/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<ApiResponse> uploadDocuments(
        @PathVariable Long id,
        @RequestParam("cni") MultipartFile cni,
        @RequestParam("DIPLOME") MultipartFile diplome
    ) {
        Technicien technicien = technicienRepository.findById(id).orElse(null);

        if (technicien == null) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, HttpStatus.BAD_REQUEST.value(), "NOT_FOUND", "Technicien introuvable", null));
        }

        documentService.saveFile(cni, technicien, TypeDocument.CNI);
        documentService.saveFile(diplome, technicien, TypeDocument.DIPLOME);

        return ResponseEntity.ok(
            new ApiResponse(
                true,
                HttpStatus.OK.value(),
                "DOCUMENTS_UPLOADED",
                "Documents envoyés avec succès. En attente de validation admin.",
                Map.of("technicienId", technicien.getId(), "statutValidation", technicien.getStatutValidation())
            )
        );
    }
}
