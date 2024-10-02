package com.example.keycloak;

import com.example.keycloak.dto.UserDTO;
import com.example.keycloak.model.User;
import com.example.keycloak.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.keycloak.keycloakconfig.KeycloakServiceImpl;
import com.example.keycloak.response.SuccessResponse;
import com.example.keycloak.keycloakconfig.KeycloakTokenResponse;
import org.apache.http.HttpEntity;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;

@SpringBootTest
class KeycloakApplicationTests {

    @InjectMocks
    KeycloakServiceImpl keycloakService;

    @Mock
    private RestTemplate restTemplate;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;
    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(keycloakService);
    }

    @Autowired
    UserRepository userRepository;

    @Test
    void testUserSignup() {
        UserDTO userSignUpRequest = new UserDTO();
        userSignUpRequest.setUsername("");
        userSignUpRequest.setPassword("");
        userSignUpRequest.setKuid("");
        when(userRepository.save(any(User.class))).thenReturn(new User());
        SuccessResponse<Object> response = keycloakService.userSignup(userSignUpRequest);
        verify(userRepository, times(1)).save(any((User.class)));
        assertEquals("Data Saved Successfully", response.getData());
    }

    @Test
    void testGetTokens() {
        KeycloakTokenResponse mockedResponse = new KeycloakTokenResponse();
        String username = "test";
        String password = "test";
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(KeycloakTokenResponse.class)))
                .thenReturn(mockedResponse);
        KeycloakTokenResponse response = keycloakService.getTokens(username, password);
        assertNotNull(response);
    }

    @Test
    void testGetUserTokensFromRefreshToken() {
        KeycloakTokenResponse mockedResponse = new KeycloakTokenResponse();
        String token = "token";
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(KeycloakTokenResponse.class)))
                .thenReturn(mockedResponse);
        KeycloakTokenResponse response = keycloakService.getUserTokensFromRefreshToken(token);
        assertNotNull(response);
    }

    @Test
    void testLogout() {
        String token = "token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String requestBody =
                "&client_id=" + clientId +
                        "&client_secret=" + clientSecret +
                        "&refresh_token=" + token;
        org.springframework.http.HttpEntity<String> request = new org.springframework.http.HttpEntity<>(requestBody, headers);
        when(restTemplate.postForObject(
                keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/logout",
                request,
                Void.class)).thenReturn(null);
        Void result = keycloakService.logout(token);
        assertNull(result);
    }

}