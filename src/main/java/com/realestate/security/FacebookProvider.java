package com.realestate.security;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.User;

import jakarta.transaction.Transactional;

import com.facebook.ads.sdk.APIContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.realestate.dao.UserDetailsImpl;
import com.realestate.models.Role;
import com.realestate.services.RoleFactory;

import org.springframework.security.core.userdetails.UserDetails;

@Service
public class FacebookProvider {
    @Value("${spring.security.oauth2.client.registration.facebook.client-id}")
    private String facebookClientId;
    @Value("${spring.security.oauth2.client.registration.facebook.client-secret}")
    private String facebookClientSecret;
    @Autowired
    private RoleFactory roleFactory;

    private static final Logger logger = LoggerFactory.getLogger(FacebookProvider.class);

    public boolean validateFacebookToken(String accessToken) {
        try {
            APIContext context = new APIContext(accessToken, facebookClientSecret, facebookClientId);
            User user = new User("me", context).get().requestField("id").execute();
            if (user != null && user.getId() != null) {
                logger.info("Facebook token is valid for user ID: {}", user.getId());
                return true;
            }
        } catch (APIException e) {
            logger.error("APIException while validating Facebook token: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Error validating Facebook token: {}", e.getMessage());
            return false;
        }
        return false;
    }

    @Transactional
    public UserDetails getUserDetailsFromFacebook(String accessToken) {
        try {
            APIContext context = new APIContext(accessToken, facebookClientSecret, facebookClientId);
            User user = new User("me", context)
                            .get().requestField("id").requestField("name")
                            .requestField("email").execute();
            if (user != null && user.getId() != null) {
                logger.info("Retrieved user details from Facebook: {}", user);

                Set<Role> roles = new HashSet<Role>();
                roles.add(roleFactory.getInstance("SUBSCRIBER"));
                // Convert to UserDetailsImpl or similar as needed
                com.realestate.models.User userModel = new com.realestate.models.User();
                userModel.setUserId(user.getId());
                userModel.setUsername(user.getFieldFirstName() + " " + user.getFieldLastName());
                userModel.setEmail(user.getFieldEmail());
                userModel.setRoles(roles);
                userModel.setIsActive(true);
                return UserDetailsImpl.build(userModel);
            }
        } catch (APIException e) {
            logger.error("APIException while retrieving Facebook user details: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error retrieving Facebook user details: {}", e.getMessage());
        }
        return null;
    }
}
