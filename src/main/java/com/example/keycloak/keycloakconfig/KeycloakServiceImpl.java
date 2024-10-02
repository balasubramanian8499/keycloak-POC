package com.example.keycloak.keycloakconfig;

import com.example.keycloak.response.SuccessResponse;
import com.example.keycloak.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.keycloak.admin.client.Keycloak;
import org.modelmapper.ModelMapper;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;

import static org.keycloak.representations.idm.CredentialRepresentation.PASSWORD;

import java.util.ArrayList;
import java.net.URI;
import java.util.List;

import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.keycloak.repository.UserRepository;

import com.example.keycloak.model.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KeycloakServiceImpl implements KeycloakService {

    @Value("${keycloak.logout}")
    private String keycloakLogout;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.authorization-grant-type}")
    private String grantType;

    @Value("${keycloak.client-admin-id}")
    private String adminClientId;

    @Value("${keycloak.adminClientSecret}")
    private String adminClientSecret;

    @Value("${keycloak.admin.grant-type}")
    private String adminGrantType;

    private final ModelMapper modelMapper;

    private final Keycloak keycloak;

    private final UserRepository userRepository;

    public KeycloakServiceImpl(ModelMapper modelMapper, Keycloak keycloak, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.keycloak = keycloak;
        this.userRepository = userRepository;
    }


    @Override
    public KeycloakTokenResponse getTokens(String email, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String requestBody = "&grant_type=" + grantType +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&username=" + email +
                "&password=" + password;
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(
                keycloakServerUrl + "realms/" + realm + "/protocol/openid-connect/token",
                request,
                KeycloakTokenResponse.class);
    }

    @Override
    public KeycloakTokenResponse getUserTokensFromRefreshToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String requestBody = "&grant_type=" + "refresh_token" +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&refresh_token=" + token;
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(
                keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token",
                request,
                KeycloakTokenResponse.class);
    }

    @Override
    public String createUserId(UserDTO userRequestDTO) {
        String userId = "";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        KeycloakTokenResponse keycloakTokenResponse = getAdminTokens();

        headers.set("Authorization", "Bearer " + keycloakTokenResponse.getAccess_token());
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setEmailVerified(true);
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(userRequestDTO.getPassword());
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(PASSWORD);

        List<CredentialRepresentation> list = new ArrayList<>();
        list.add(credentialRepresentation);
        user.setCredentials(list);

        HttpEntity<UserRepresentation> request = new HttpEntity<>(user, headers);
        RestTemplate restTemplate = new RestTemplate();
        String createUserUrl = keycloakServerUrl + "admin/realms/" + realm + "/users";
        final ResponseEntity<UserRepresentation> response = restTemplate
                .postForEntity(createUserUrl, request, UserRepresentation.class);
        if (response.getStatusCode() == HttpStatus.CREATED) {
            final URI location = response.getHeaders().getLocation();
            if (location != null) {
                final String locationStr = location.toString();
                final String[] locationArr = locationStr.split("/");
                userId = locationArr[locationArr.length - 1];
            }
        }
        return userId;
    }

    @Override
    public KeycloakTokenResponse getAdminTokens() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String requestBody = "&grant_type=" + adminGrantType +
                "&client_id=" + adminClientId +
                "&client_secret=" + adminClientSecret;
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(
                keycloakServerUrl + "realms/" + realm + "/protocol/openid-connect/token",
                request,
                KeycloakTokenResponse.class);
    }

    @Override
    public Void logout(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String requestBody = "&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&refresh_token=" + token;

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(
                keycloakServerUrl + "realms/" + realm + "/protocol/openid-connect/logout",
                request,
                Void.class);
    }

    @Override
    public SuccessResponse<Object> userSignup(UserDTO userSignUpRequest) {
        SuccessResponse<Object> successResponse = new SuccessResponse<>();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setUsername(userSignUpRequest.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userSignUpRequest.getPassword()));
        user.setKuid(userSignUpRequest.getKuid());
        userRepository.save(user);
        successResponse.setData("Data Saved Successfully");
        return successResponse;
    }
}
