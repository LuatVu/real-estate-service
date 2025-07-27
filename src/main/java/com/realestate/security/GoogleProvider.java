package com.realestate.security;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.realestate.models.Role;
import com.realestate.models.User;
import com.realestate.services.RoleFactory;
import com.google.api.client.json.JsonFactory;
import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import com.realestate.dao.UserDetailsImpl;
import jakarta.transaction.Transactional;

@Service
public class GoogleProvider {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Autowired
    private RoleFactory roleFactory;

    private GoogleIdTokenVerifier verifier;
    private HttpTransport httpTransport;

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final Logger logger = LoggerFactory.getLogger(GoogleProvider.class);

    @PostConstruct
    public void init() throws GeneralSecurityException, IOException {
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        
        verifier = new GoogleIdTokenVerifier.Builder(httpTransport, JSON_FACTORY)
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    @Transactional
    public UserDetails validateIdToken(String idToken) {
        try {
            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken != null) {
                GoogleIdToken.Payload payload = googleIdToken.getPayload();

                Long expirationTime = payload.getExpirationTimeSeconds();
                // comment for testing purposes
                if (expirationTime == null || expirationTime < System.currentTimeMillis() / 1000) {
                    logger.error("Token is expired", new Exception("Token is expired"));
                    return null;
                }
                Set<Role> roles = new HashSet<Role>();
                roles.add(roleFactory.getInstance("SUBSCRIBER"));

                User user = User.builder()
                        .userId(payload.getSubject())
                        .email(payload.getEmail())
                        .username((String) payload.get("name"))
                        .phoneNumber((String) payload.get("phone_number"))
                        .roles(roles)
                        .isActive(true)
                        // .picture((String) payload.get("picture"))
                        .build();
                
                return UserDetailsImpl.build(user);
            }
        } catch (Exception e) {
            // Not a valid ID token, might be access token
            return null;
        }
        return null;
    }
}
