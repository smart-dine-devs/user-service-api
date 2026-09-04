package com.devstack.SmartDine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

/*1*/    private static final String[] PUBLIC_PATHS = {
            "/api/auth/signup",
            "/api/auth/login",
            "/api/auth/login/google",
            "/api/auth/login/github",
            "/api/auth/otp/verify",
            "/api/auth/password/forgot",
            "/api/auth/password/reset",
            "/actuator/health",
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
/*2*/                http.csrf(csrf -> csrf.disable())
/*3*/              .sessionManagement(
                        sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
/*4*/                .authorizeHttpRequests(auth -> auth.requestMatchers(PUBLIC_PATHS).permitAll()
                        .anyRequest().authenticated()
                )
/*5*/                .oauth2ResourceServer(oath2 -> oath2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter())));
        return http.build();

    }

/*6*/    private JwtAuthenticationConverter keycloakJwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
        return converter;
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || realmAccess.get("roles") == null) {
            return List.of();
        }
        List<String> roles = (List<String>) realmAccess.get("roles");
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(("ROLE_" + role.toLowerCase())))
                .collect(Collectors.toList());

    }

}


/*

Without this file, EVERY endpoint in your app is either:
- completely open (anyone can call anything), or
- Spring Security blocks EVERYTHING by default with
  a generic login popup, breaking your API

SecurityConfig.java = the RULEBOOK that tells Spring Security:
"these specific paths are public, everything else
 needs a valid login token (JWT) to access"

1 - Think of your SmartDine app like a restaurant building.

Most rooms inside need a KEYCARD to enter
(these are your PROTECTED endpoints, like /api/orders)

But the FRONT DOOR and the RECEPTION DESK
must be open to EVERYONE, even people who don't
have a keycard yet - because that's literally
WHERE you go to GET a keycard in the first place.

If the front door itself needed a keycard,
NO ONE could ever enter the building to get one.
You'd be permanently locked out, forever.

PUBLIC_PATHS = the "front door" list -
               endpoints ANYONE can access,
               even without being logged in yet

/api/auth/login   = the "front door"
                     (you go HERE to prove who you are
                      and GET your access token)

/api/auth/signup  = the "reception desk"
                     (you go HERE to CREATE an account
                      in the first place)

                      Imagine /api/auth/login required you to be logged in first.

Nimal opens the SmartDine app for the very first time.
He has no account, no token, nothing.
He tries to call /api/auth/login to log in.
Spring Security blocks him: "you're not logged in!"

But... the ONLY way to log in IS by calling
/api/auth/login. He can never get past this.
He's stuck forever, unable to log in OR sign up.

This is exactly why login/signup MUST be in PUBLIC_PATHS -
otherwise your app breaks itself, permanently.

/api/auth/login   -> should be OPEN (you're not logged in yet,
                      that's the whole point of calling it)

/api/orders        -> should be PROTECTED (only logged-in
                      customers can place/view their orders)

Without SecurityConfig telling Spring which is which,
Spring Security's default behavior would block EVERYTHING,
including your login endpoint itself - locking everyone out.

2. CSRF attack scenario (session-based apps):

1. Nimal logs into SmartDine in his browser.
   Browser AUTOMATICALLY stores a session cookie.

2. Without logging out, Nimal visits a MALICIOUS website
   in another tab.

3. That malicious site secretly sends a request to
   SmartDine's server, like "transfer money" or
   "place a fake order" - and because BROWSERS
   automatically attach cookies to ANY request going
   to that domain, SmartDine's server sees Nimal's
   valid session cookie and thinks: "oh, this is Nimal,
   let it through" - even though NIMAL never actually
   clicked anything on SmartDine himself.

This is CSRF - tricking the BROWSER into automatically
sending your existing login proof (cookie) to a request
you never intended to make.

Why this attack doesn't apply to your setup:

SmartDine doesn't use cookies for login at all.
It uses JWT tokens, which:

- are NOT automatically attached by the browser
- must be MANUALLY included by your frontend code,
  typically in a header like:
  Authorization: Bearer <token>

A malicious website CANNOT secretly make your browser
attach this header - it simply doesn't have access
to Nimal's token (unlike cookies, which browsers
attach automatically without asking).

So the entire ATTACK METHOD that CSRF protection
defends against doesn't even exist in a JWT-based
system. Turning CSRF protection ON here would just
add unnecessary friction with zero actual benefit.

3. STATELESS  meaning
OLD WAY (Sessions) - like a theater that STAMPS your hand

You buy a ticket at the counter (login).
Staff stamps your hand with invisible ink.
Theater staff KEEPS A LIST at the door:
  "hand-stamp #5 = Nimal, allowed inside"

Every time you walk in/out, they check YOUR HAND
against THEIR LIST to know who you are.

Problem: the THEATER has to remember everyone's
stamp. If this theater has multiple branches
(microservices), EVERY branch needs to know
about EVERY stamp - hard to keep in sync.

NEW WAY (JWT / Stateless) - like a theater that gives
                             you a PRINTED TICKET

You buy a ticket at the counter (login).
Staff gives you a PHYSICAL TICKET with your name,
seat number, and a security hologram printed on it.

You CARRY this ticket yourself.
Every time you enter a new room/branch, you just
SHOW your ticket. Staff there don't need to look
you up in some master list - they just check if
the ticket itself LOOKS VALID (checking the hologram),
and read your name straight off it.

The theater staff remembers NOTHING about you
between visits - the ticket carries all the proof.

Session (old way)  = server stores "Nimal is logged in"
                      in its own memory

JWT (SessionCreationPolicy.STATELESS) = server stores
                      NOTHING. Nimal's frontend app holds
                      the JWT token itself, and sends it
                      with EVERY request. Server just
                      checks "is this token valid?" fresh,
                      every single time, then forgets it again.

Why good for SmartDine specifically:
You have gateway + multiple microservices.
With sessions, ALL of them would need to share/sync
who's logged in - complicated.
With JWT, EACH service can independently just check
the token itself - no shared memory needed between them.


4. requestMatchers(PUBLIC_PATHS).permitAll()
  -> "anyone can call these specific URLs, no token needed"

anyRequest().authenticated()
  -> "every OTHER URL requires a valid, logged-in user"

Real example:
/api/auth/login       -> permitAll (open)
/api/orders/my-orders -> authenticated (needs valid JWT)

5. This tells Spring: "when someone sends a request with a
JWT token, VALIDATE it and figure out who they are and
what roles/permissions they have."

oauth2ResourceServer = Spring's built-in support for
apps that accept JWT tokens (issued by an external
auth provider - here, Keycloak) instead of managing
login/passwords itself directly in this service.

6. Purpose: Keycloak stores a user's ROLES inside the JWT
token in its OWN custom format. Spring Security doesn't
understand that format out of the box - this method
TRANSLATES Keycloak's format into something Spring
Security understands.


7. Step by step:

1. jwt.getClaim("realm_access")
   -> Keycloak puts roles inside a nested field called
      "realm_access" in the token, e.g.:
      { "realm_access": { "roles": ["admin", "customer"] } }

2. Extract the "roles" list from inside it: ["admin", "customer"]

3. For each role, convert it into something Spring
   Security understands: SimpleGrantedAuthority
   Also prefixes with "ROLE_" and lowercases it,
   because Spring Security's convention expects
   role names formatted like "ROLE_ADMIN" -> here
   becomes "ROLE_admin" (lowercase - worth noting,
   Spring's convention is usually uppercase after
   ROLE_, so this might need adjusting later depending
   on how you check roles elsewhere)

Real example: Nimal's JWT contains role "customer".
After this conversion: "ROLE_customer"
Later in your code, you could restrict an endpoint like:
@PreAuthorize("hasRole('customer')")

*/




/*
==========================================================
 SECURITYCONFIG.JAVA - EXPLAINED IN ONE FLOW
 (SmartDine example: Nimal Perera)
==========================================================

PUBLIC_PATHS is the list of URLs that don't require a login
token, so when Nimal opens the SmartDine app for the very
first time with no account and no token, he can still call
POST /api/auth/signup to create his account and POST
/api/auth/login to get his JWT token, because both are listed
in PUBLIC_PATHS - if they weren't, Nimal could never log in
at all, since logging in would require already being logged
in, a permanent lockout. Then http.csrf(csrf -> csrf.disable())
turns off CSRF protection, which is safe here because CSRF
attacks only work through browser session cookies, and since
Nimal's SmartDine app uses JWT tokens sent manually in a
header instead of cookies, a shady website he opens in another
tab has no way to secretly attach his token to a fake request,
so this protection is simply unnecessary rather than risky to
turn off. Next, .sessionManagement(sm ->
sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) tells
Spring not to remember who's logged in using server memory, so
once Nimal logs in and receives his JWT token, his frontend app
attaches that token to every request header
(Authorization: Bearer <token>) when he places an order or
views the menu, and the server simply checks whether that token
is valid each time instead of keeping a login list, which makes
it easy for SmartDine's gateway and multiple microservices to
each verify him independently. After that,
.authorizeHttpRequests(auth ->
auth.requestMatchers(PUBLIC_PATHS).permitAll().anyRequest()
.authenticated()) enforces the actual rule: Nimal's earlier call
to POST /api/auth/login goes through freely since it's a public
path, but when he later calls GET /api/orders/my-orders, that
path is NOT public, so his request must include a valid JWT
token or the server responds with 401 Unauthorized. Then
.oauth2ResourceServer(oath2 -> oath2.jwt(jwt ->
jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter())))
tells Spring that whenever a request like Nimal's order request
arrives with a token attached, it should validate that the token
was genuinely signed by Keycloak, confirm it hasn't expired, and
read his identity and roles from inside it before allowing the
request through to the controller. To actually read those roles,
keycloakJwtAuthenticationConverter() and extractAuthorities(Jwt jwt)
come in, because Keycloak stores roles inside the token in its own
nested format such as "realm_access": { "roles": ["customer"] },
and extractAuthorities() pulls out "customer" from inside Nimal's
token and converts it into "ROLE_customer" so Spring Security can
understand it, which could later be used to restrict an endpoint
with @PreAuthorize("hasRole('customer')") to block Nimal from
reaching admin-only endpoints like /api/admin/reports. Put together
end to end: Nimal signs up through the public signup endpoint, logs
in through the public login endpoint and receives a JWT token
containing his roles from Keycloak, his frontend stores that token
and attaches it when calling GET /api/orders/my-orders, SecurityConfig
sees this path is not public so it validates the token, extractAuthorities()
reads his "customer" role from inside it, and Spring finally allows
the request through to the controller, which returns his order
history back to him.
*/