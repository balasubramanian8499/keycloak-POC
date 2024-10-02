package com.example.keycloak.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SuccessResponse<T> implements Serializable { 

    private int statusCode = 200;
    private String statusMessage = "Success";
    private transient T data;

}
