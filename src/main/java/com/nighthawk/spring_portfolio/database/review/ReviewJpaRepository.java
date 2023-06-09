package com.nighthawk.spring_portfolio.database.review;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// JPA is an object-relational mapping (ORM) to persistent data, originally relational databases (SQL). Today JPA implementations has been extended for NoSQL.
public interface ReviewJpaRepository extends JpaRepository<Review, Long> {
    // JPA has many built in methods, these few have been prototyped for this
    // application
    void save(String reviewername);

    // A
    List<Review> findByReviewernameIgnoreCase(String reviewername); // look to see if Joke(s) exist

    @Query(value = "SELECT * FROM Review r WHERE r.name LIKE ?1 or r.contact LIKE ?1", nativeQuery = true)
    List<Review> findByReviewernameorContact(String term);

    List<Review> findByReviewernameAndAgeAndReviewtextAndContact(String reviewername, int age, String reviewtext,
            String contact);

    // @Query(value = "SELECT max(id) FROM Practice")
    // long getMaxId();
}
