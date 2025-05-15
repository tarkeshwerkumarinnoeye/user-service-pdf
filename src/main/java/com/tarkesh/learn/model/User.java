package com.tarkesh.learn.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import com.tarkesh.learn.enums.AccountStatus;

public record User(
    Integer id, 
    String username, 
    String password, 
    String email, 
    String firstName, 
    String lastName,
    LocalDate dateOfBirth, 
    String phoneNumber, 
    Address address,
    Set<String> roles,
    AccountStatus accountStatus,
    String profilePictureUrl,
    LocalDateTime lastLoginDate, 
    LocalDateTime createdDate,
    LocalDateTime lastModifiedDate
) {}
