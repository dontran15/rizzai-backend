package com.nighthawk.spring_portfolio.database.dating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nighthawk.spring_portfolio.database.person.Person;

import java.util.List;
import java.util.Optional;

/*
Extends the JpaRepository interface from Spring Data JPA.
-- Java Persistent API (JPA) - Hibernate: map, store, update and retrieve database
-- JpaRepository defines standard CRUD methods
-- Via JPA the developer can retrieve database from relational databases to Java objects and vice versa.
 */
public interface DatingProfileJpaRepository extends JpaRepository<DatingProfile, Long> {
        DatingProfile findByPerson(Person person);

        List<DatingProfile> findAllByOrderByIdAsc();

        List<DatingProfile> findAllByProfileDetail(ProfileDetail detail);

        // Custom JPA query
        @Query(value = "SELECT * FROM Person p WHERE p.name LIKE ?1 or p.email LIKE ?1", nativeQuery = true)
        List<DatingProfile> findByLikeTermNative(String term);
        /*
         * https://www.baeldung.com/spring-data-jpa-query
         */
}
