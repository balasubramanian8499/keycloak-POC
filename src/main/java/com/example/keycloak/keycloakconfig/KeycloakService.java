package com.example.keycloak.keycloakconfig;

import com.example.keycloak.response.SuccessResponse;

import com.example.keycloak.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public interface KeycloakService {

    KeycloakTokenResponse getTokens(String email, String password);

    String createUserId(UserDTO userRequestDTO);

    SuccessResponse<Object> userSignup(UserDTO userSignUpRequest);

    KeycloakTokenResponse getAdminTokens();

    Void logout(String token);

    KeycloakTokenResponse getUserTokensFromRefreshToken(String token);
}
