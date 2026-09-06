package com.devstack.SmartDine.dtos.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {

    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType="Bearer";
    private long expiresIn;
    private UserResponseDto user;

}


/*
==========================================================
 AUTHRESPONSEDTO.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE AuthResponseDto AT ALL
WHY: bundle login response together
----------------------------------------------------------
When Nimal successfully logs in, SmartDine needs to send
back everything his frontend needs to STAY logged in and
know WHO he is - his tokens (proof of login) and his basic
profile info. Rather than scattering these across multiple
separate responses, this DTO bundles everything the frontend
needs after a successful login into ONE clean object.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal logs in successfully. Instead of the backend sending
back JUST a token, or JUST his user info separately, it
sends back ONE object containing everything at once - his
frontend can then store the tokens AND display his name,
all from a single response.
*/


/*
STEP 2 - private String accessToken;
WHY: proves user is logged in
----------------------------------------------------------
The actual JWT token Nimal's frontend must attach to every
future request to prove he's logged in - matches the
concept from JwtConfig.java earlier (expirationMs controls
how long THIS token stays valid).
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

Nimal's frontend stores this and sends it in every future
request header:
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
*/


/*
STEP 3 - private String refreshToken;
WHY: gets new token without login
----------------------------------------------------------
A separate, LONGER-lived token used to get a brand new
accessToken once the current one expires - matches
refreshExpirationMs from JwtConfig.java earlier.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal's accessToken expires after 10 hours. Instead of
forcing him to log in again, his frontend quietly sends
this refreshToken to a "refresh" endpoint to get a fresh
accessToken, keeping him logged in smoothly.
*/


/*
STEP 4 - @Builder.Default private String tokenType = "Bearer";
WHY: tells frontend token format
----------------------------------------------------------
Tells the frontend HOW to format the token when sending it -
"Bearer" is the standard prefix used in the Authorization
header for JWT-based authentication. @Builder.Default
ensures this default value is actually applied when using
.builder(), since Lombok would otherwise leave it null.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
tokenType = "Bearer"

Nimal's frontend combines tokenType and accessToken like this:
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Without knowing the tokenType, the frontend might not know
whether to write "Bearer", "Token", or something else before
the actual token value.
*/


/*
STEP 5 - private long expiresIn;
WHY: lets frontend plan refresh
----------------------------------------------------------
Tells the frontend HOW LONG (usually in seconds) the
accessToken remains valid, so the frontend can plan WHEN
to refresh it, rather than waiting for a request to fail
first.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
expiresIn = 36000  (10 hours, matching JwtConfig's
                     expirationMs = 36000000 ms)

Nimal's frontend can set an internal timer: "refresh the
token proactively a bit before 36000 seconds pass," instead
of only reacting after a request gets rejected for being
expired.
*/


/*
STEP 6 - private UserResponseDto user;
WHY: avoids second API call
----------------------------------------------------------
Nimal's basic profile info, bundled directly into this same
response - so the frontend doesn't need to make a SECOND
separate request just to find out who logged in.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
user = { "firstName": "Nimal", "lastName": "Perera",
          "email": "nimal@gmail.com", ... }

Immediately after login, the frontend can display
"Welcome, Nimal!" on the home screen, using data already
included in THIS SAME response - no extra API call needed.

(UserResponseDto itself is a separate DTO you haven't shared
yet - likely a safe, public-facing version of User.java,
leaving out sensitive fields like passwordHash.)
*/


/*
STEP 7 - PUTTING IT ALL TOGETHER (FULL FLOW)
WHY: shows the complete login flow
----------------------------------------------------------
1. Nimal submits correct email/password to POST /api/auth/login
2. Backend generates a new accessToken + refreshToken using
   JwtConfig's secret and expiration settings
3. Backend builds an AuthResponseDto containing both tokens,
   tokenType ("Bearer"), expiresIn, and Nimal's user info
4. This gets wrapped inside ApiResponseDto as the "data" field:
   ApiResponseDto.success("Login successful", authResponseDto)
5. Nimal's frontend receives the full response, stores
   accessToken + refreshToken, and immediately displays his
   name/profile using the included user object - all from
   ONE single login response
*/