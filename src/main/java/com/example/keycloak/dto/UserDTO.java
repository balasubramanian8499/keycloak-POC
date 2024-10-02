package com.example.keycloak.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

	private Integer id;

	private String firstName;

	private String lastName;

	private String username;

	private String password;

	private String kuid;

	private String email;

}

