package com.example.keycloak.keycloakconfig;

import com.example.keycloak.response.SuccessResponse;
import com.example.keycloak.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("api")
public class KeycloakController {

    private final KeycloakService service;

    public KeycloakController(KeycloakService service) {
        this.service = service;
    }

    @PostMapping("/getUserTokens")
    public KeycloakTokenResponse getUserTokens(@RequestBody UserDTO userRequestDTO) {
        return service.getTokens(userRequestDTO.getEmail(), userRequestDTO.getPassword());
    }

    @PostMapping("/getAdminTokens")
    public KeycloakTokenResponse getAdminTokens() {
        return service.getAdminTokens();
    }

    @PostMapping("/getUserTokensFromRefreshToken")
    public KeycloakTokenResponse getUserTokensFromRefreshToken(@RequestBody TokenDto token){
        if (token == null) {
            throw new RuntimeException("Bad Request");
        }
        return service.getUserTokensFromRefreshToken(token.getToken());
    }

    @PostMapping("/userSignup")
    public SuccessResponse<Object> userSignup(@RequestBody UserDTO createUserRequestDTO) {
        String keycloakId = service.createUserId(createUserRequestDTO);
        createUserRequestDTO.setKuid(keycloakId);
        return service.userSignup(createUserRequestDTO);
    }

    @PostMapping("/logout")
    public Void logout(@RequestBody TokenDto token) {
        return service.logout(token.getToken());
    }

    @GetMapping("/check")
    public ResponseEntity<String> getAdmin(Principal principal) {
        if (principal instanceof JwtAuthenticationToken token) {
            String userName = (String) token.getTokenAttributes().get("name");
            String userEmail = (String) token.getTokenAttributes().get("email");
            return ResponseEntity.ok("Hello  \nUser Name : " + userName + "\nUser Email : " + userEmail);
        }
        return null;
    }

}



