package com.devstack.SmartDine.repository;

import com.devstack.SmartDine.entity.User;
import com.devstack.SmartDine.entity.enums.AuthProvider;
import com.devstack.SmartDine.entity.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email); /*1*/
    Optional<User> findByKeycloakId(String keycloakId);  /*2*/
    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);  /*3*/
    boolean existsByEmail(String email);      /*4*/
    boolean existsByPhone(String phone);        /*5*/
    Page<User> findAllByStatus(UserStatus status, Pageable pageable);  /*6*/

    @Query("SELECT u FROM User u WHERE u.email=:email AND u.deletedAt IS NULL")    /*7*/
    Optional<User> findActiveByEmail(@Param("email") String email);

    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt=:loginAt WHERE u.id=:userId")    /*8*/
    Optional<User> findActiveByEmail(@Param("userId") UUID userId, @Param("loginAt")LocalDateTime loginAt);

    // must reconsider
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >=:since")    /*9*/
    long countNewUserSince(@Param("since") LocalDateTime since);
}

/*
1. Method NAME itself generates the query - no SQL needed.
Spring reads "findByEmail" and automatically builds:
SELECT * FROM users WHERE email = ?

Real example: login page checks
userRepository.findByEmail("nimal@gmail.com")
to find Nimal's account and verify his password.

Optional<User> = might find a user, might find NONE
(e.g. wrong email typed) - Optional forces you to
handle the "not found" case safely, avoiding crashes.

 2.Same pattern - finds a user by their Keycloak ID instead.
Used if SmartDine uses Keycloak as an external login system.

3. Finds a user by combining TWO conditions - the enum
AND an ID from that provider.

Real example: Nimal logs in with Google.
Google gives back providerId = "1029384756"
You look up: findByProviderAndProviderId(GOOGLE, "1029384756")
to find his existing SmartDine account, if he has one.

4. Just checks TRUE/FALSE - does this email already exist?
Doesn't return the full user object, just yes/no.

Real example: during signup, before creating Nimal's
account, check existsByEmail("nimal@gmail.com") to
prevent duplicate accounts with the same email.

5.Purpose: checks TRUE/FALSE whether a user with this
phone number already exists in the database.

Spring reads the method name "existsByPhone" and
automatically builds a query like:
SELECT COUNT(*) > 0 FROM users WHERE phone = ?

Returns true/false only - not the full user object,
similar to existsByEmail, just for phone instead.

Nimal signs up with phone number "+94771234567".
Before creating his account, you'd call:
userRepository.existsByPhone("+94771234567")

If true  -> reject signup: "phone already registered"
If false -> allow signup to continue

6.Finds users filtered by status, WITH pagination built in
(so you don't load 10,000 users at once - just a "page" of them).

Real example: an admin dashboard listing
"show me all PENDING_VERIFICATION users, 20 per page"

7. Intent: find a user by email, but ONLY if they haven't
been soft-deleted (deletedAt is empty/null).

Real example: Nimal deactivates his account (soft delete -
deletedAt gets a timestamp, row isn't actually removed).
Later, login should NOT find his account anymore -
this query enforces that by checking deletedAt IS NULL.

8.@Modifying tells Spring "this isn't a SELECT, it CHANGES data"
(needed for UPDATE/DELETE queries written with @Query).

Real example: every time Nimal logs in, this updates his
lastLoginAt field to the current timestamp, without you
loading the whole User object, changing it, and saving it
manually - this does it directly in the database, faster.

9. Counts how many users were created after a given date/time.

Real example: admin dashboard shows
"150 new users joined SmartDine this week" -
you'd call this with since = 7 days ago.
*/