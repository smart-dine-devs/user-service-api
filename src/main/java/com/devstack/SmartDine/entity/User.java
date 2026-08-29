package com.devstack.SmartDine.entity;


import com.devstack.SmartDine.entity.enums.AuthProvider;
import com.devstack.SmartDine.entity.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "keycloak_id" , unique = true)
    private String keycloakId;

    @Column(name="first_name",nullable= false,length=100)
    private String firstName;

    @Column(name="last_name",nullable= false,length=100)
    private String lastName;

    @Column(name="email",nullable= false, length=255 ,unique=true)
    private String email;

    @Column(name="password_Hash")
    private String passwordHash;

    @Column(name="phone",length = 20)
    private String phoneNumber;

    @Column(name="profile_picture",columnDefinition = "TEXT")
    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status" , nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider" , nullable = false)
    @Builder.Default
    private AuthProvider provider = AuthProvider.LOCAL;

    @Column(name="provider_id")
    private String providerId;

    @Column(name="email_Verified" , nullable =false)
    @Builder.Default
    private boolean emailVerified = false;

    @Column(name="phone_Verified" , nullable =false)
    @Builder.Default
    private boolean phoneVerified = false;

    @Column(name="two_factor_enabled" , nullable =false)
    @Builder.Default
    private boolean twoFactorEnabled = false;

    @Column(name="last_login_at" , nullable =false)
    private LocalDateTime lastLoginAt;

    @ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(
            name= "user_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )



    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @CreatedDate
    @Column(name= "created_at" , nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name= "updated_at" , nullable = false)
    private LocalTime updatedAt;

    @Column(name= "deleted_at" )
    private LocalTime deletedAt;

    public void addRole(Role role){
        this.roles.add(role);
    }

    public void removeRole(Role role){
        this.roles.remove(role);
    }

    public String getFullName(){
        return firstName+" - "+lastName;
    }
    public void safeDelete(){

    }
}



/*
==========================================================
 USER.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - PURPOSE OF THIS CLASS
----------------------------------------------------------
user.java is a BLUEPRINT for what a "user" looks like
in the SmartDine database.

@Entity tells Spring: this class = a database table
@Table(name="users") = the actual table name in PostgreSQL

Every field below becomes a COLUMN in that table.
Every object created from this class = ONE ROW (one real user).
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
A customer named Nimal Perera registers on the SmartDine app.
Spring creates ONE ROW in the "users" table representing him.
*/


/*
STEP 2 - @EntityListeners(AuditingEntityListener.class)
----------------------------------------------------------
This attaches a built-in Spring "watcher" to the entity.
It can automatically fill in fields like createdAt/updatedAt
WITHOUT you writing manual code for it.

Note: it needs createdAt/updatedAt fields marked with
@CreatedDate and @LastModifiedDate to actually do anything.
Those fields are not in this version yet.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
The moment Nimal's row is inserted, createdAt could fill in
automatically. Any time his row is updated, updatedAt could
refresh automatically - once those fields are added.
*/


/*
STEP 3 - LOMBOK ANNOTATIONS
----------------------------------------------------------
@Getter / @Setter   -> auto-creates getFirstName(), setEmail(), etc.
@NoArgsConstructor  -> lets you write: new user()
@AllArgsConstructor -> lets you write: new user(id, keycloakId, ...)
@Builder            -> lets you build an object field by field
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
user newUser = user.builder()
    .firstName("Nimal")
    .lastName("Perera")
    .email("nimal@gmail.com")
    .phoneNumber("+94771234567")
    .status(UserStatus.PENDING_VERIFICATION)
    .provider(AuthProvider.LOCAL)
    .build();
*/


/*
STEP 4 - FIELD BY FIELD MEANING
----------------------------------------------------------
id                 -> unique auto-generated ID (UUID, not guessable)
keycloakId         -> ID from Keycloak, if used for login
firstName          -> user's first name
lastName           -> user's last name
email              -> login/contact email (no format validation yet)
passwordHash       -> scrambled/encrypted password, never the real one
phoneNumber        -> contact number
profilePictureUrl  -> link to their profile photo
status             -> current account stage (see UserStatus.java)
provider           -> how they signed up (see AuthProvider.java)
providerId         -> ID from Google/Facebook if signed up that way
emailVerified      -> true/false, did they confirm their email
phoneVerified      -> true/false, did they confirm their phone
twoFactorEnabled   -> true/false, extra login security on/off
lastLoginAt        -> timestamp of their most recent login
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
id                 = "a1b2c3d4-5678-..."
firstName          = "Nimal"
lastName            = "Perera"
email              = "nimal@gmail.com"
passwordHash       = "$2a$10$X8kJ9..."
phoneNumber        = "+94771234567"
status             = PENDING_VERIFICATION  (at signup)
                     -> becomes ACTIVE after email verification
provider           = LOCAL  (signed up directly, not via Google)
emailVerified      = false  -> true after clicking verify link
lastLoginAt        = updated automatically every time he logs in
*/


/*
STEP 5 - PUTTING IT ALL TOGETHER (SIGNUP FLOW)
----------------------------------------------------------
1. Nimal fills the signup form (name, email, password, phone)
2. Controller creates a user object using .builder()
3. status defaults to PENDING_VERIFICATION
4. provider defaults to LOCAL
5. This user object gets saved -> becomes 1 row in "users" table
6. Nimal clicks the verification email link
7. Code updates: status = UserStatus.ACTIVE
8. Nimal can now log in and place food orders on SmartDine
*/

