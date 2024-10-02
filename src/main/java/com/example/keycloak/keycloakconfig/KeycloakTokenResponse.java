package com.example.keycloak.keycloakconfig;


import com.fasterxml.jackson.annotation.JsonProperty;

public class KeycloakTokenResponse {

    @JsonProperty("access_token") 
    private String access_token;

    @JsonProperty("refresh_token") 
    private String refresh_token;

    @JsonProperty("expires_in") 
    private long expiresIn;

    @JsonProperty("scope") 
    private String scope;

    @JsonProperty("token_type") 
    private String tokenType;

    @JsonProperty("session_state") 
    private String sessionState;

    public String getAccess_token() {
        return access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public long getExpires_in() {
        return expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getSessionState() {
        return sessionState;
    }

}