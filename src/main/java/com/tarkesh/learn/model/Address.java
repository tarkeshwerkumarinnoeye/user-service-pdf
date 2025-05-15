package com.tarkesh.learn.model;

public record Address(
    String street, 
    String city, 
    String state, 
    String zipCode, 
    String country
) {}
