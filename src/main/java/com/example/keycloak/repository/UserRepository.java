package com.example.keycloak.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import com.example.keycloak.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}

