package com.tarkesh.learn.repository;

import com.github.javafaker.Faker;
import com.tarkesh.learn.enums.AccountStatus;
import com.tarkesh.learn.model.Address;
import com.tarkesh.learn.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Repository;


@Repository
public class UserRepository {

    private static final List<Set<String>> ROLES = List.of(Set.of("USER"), Set.of("ADMIN"), Set.of("USER", "ADMIN"));

    public List<User> getSampleUsers(int count) {
        if (count <= 0) {
            return new ArrayList<>();
        }
        Faker faker = new Faker();
        return IntStream.rangeClosed(1, count).mapToObj(i -> {
            Address address = new Address(
                faker.address().streetAddress(),
                faker.address().city(),
                faker.address().state(),
                faker.address().zipCode(),
                faker.address().country());
            AccountStatus accountStatus = AccountStatus.values()[rnb(0, AccountStatus.values().length - 1)];

            return new User(i, 
            faker.name().username(),
            faker.internet().password(),
            faker.internet().emailAddress(),
            faker.name().firstName(),
            faker.name().lastName(),
            getLDT(rnb(20, 60), rnb(0, 30)).toLocalDate(),
            faker.phoneNumber().cellPhone(),
            address,
            ROLES.get(rnb(0, ROLES.size() - 1)),
            accountStatus,
            faker.internet().url(),
            getLDT(0, rnb(0, 31)),
            getLDT(rnb(3, 5), rnb(1, 31)),
            getLDT(rnb(1, 2), rnb(1, 31))
            );
        }).collect(Collectors.toList());
    }
    
    private LocalDateTime getLDT(int yearsBack, int daysBack){
    return LocalDateTime.now().minusYears(yearsBack).minusDays(daysBack);
    }

    private int rnb(int min, int max){
    return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
