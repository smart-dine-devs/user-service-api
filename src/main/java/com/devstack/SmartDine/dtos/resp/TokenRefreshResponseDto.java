package com.devstack.SmartDine.dtos.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRefreshResponseDto {
    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType="Bearer";
    private long expiresIn;
}

/*
==========================================================
 TOKENREFRESHRESPONSEDTO.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE TokenRefreshResponseDto AT ALL
WHY: gives new tokens after refresh
----------------------------------------------------------
When Nimal's accessToken expires, his frontend sends the
refreshToken (using TokenRefreshRequestDto, from your earlier
req DTOs) to get a NEW set of tokens - WITHOUT him having to
log in again. This DTO is what the backend sends BACK after
that refresh succeeds. It's almost identical to
AuthResponseDto, just WITHOUT the "user" field, since a
refresh doesn't need to re-send his whole profile again -
he's already logged in, only his tokens need renewing.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal's 10-hour accessToken expires while he's mid-session.
His frontend quietly sends his refreshToken to
POST /api/auth/refresh. Backend responds with a BRAND NEW
accessToken (and possibly a new refreshToken too), letting
him keep using the app with zero interruption - no login
screen, no re-entering his password.
*/


/*
STEP 2 - private String accessToken;
WHY: new token to keep using app
----------------------------------------------------------
A FRESH JWT token, replacing the expired one, which Nimal's
frontend now attaches to all future requests going forward.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Old accessToken (expired): "eyJhbGciOiJIUzI1NiIsInR5cCI6..."
New accessToken (this response): "eyJhbGciOiJIUzI1NiIsIn9k..."

Frontend immediately REPLACES the old stored token with this
new one, and uses it in the Authorization header from now on.
*/


/*
STEP 3 - private String refreshToken;
WHY: allows future refreshes again
----------------------------------------------------------
A refreshToken is included here too, since some systems
issue a BRAND NEW refresh token every time (called "refresh
token rotation") - a common security practice to reduce how
long any single refresh token stays usable.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
If SmartDine rotates refresh tokens, Nimal's OLD refreshToken
becomes invalid the moment this NEW one is issued - so his
frontend must store and use THIS new refreshToken for the
NEXT refresh cycle, not the one he used to get here.
*/


/*
STEP 4 - @Builder.Default private String tokenType = "Bearer";
WHY: tells frontend token format
----------------------------------------------------------
Same purpose as in AuthResponseDto - tells the frontend to
prefix the accessToken with "Bearer" when attaching it to
request headers.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Frontend builds the header exactly the same way as before:
Authorization: Bearer <new accessToken>
*/


/*
STEP 5 - private long expiresIn;
WHY: lets frontend plan next refresh
----------------------------------------------------------
Tells the frontend how long (in seconds) THIS new
accessToken stays valid, so it can schedule the NEXT
refresh proactively.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
expiresIn = 36000  (another 10 hours)

Frontend resets its internal timer, planning to refresh
again proactively before this new 10-hour window runs out.
*/


/*
STEP 6 - PUTTING IT ALL TOGETHER (FULL FLOW)
WHY: shows the complete refresh flow
----------------------------------------------------------
1. Nimal is using SmartDine; his accessToken quietly expires
   in the background
2. His frontend automatically sends his refreshToken to
   POST /api/auth/refresh (using TokenRefreshRequestDto)
3. Backend validates the refreshToken is still valid and not
   expired/revoked
4. Backend generates a new accessToken (and possibly a new
   refreshToken), building a TokenRefreshResponseDto
5. This gets wrapped inside ApiResponseDto:
   ApiResponseDto.success("Token refreshed", tokenRefreshResponseDto)
6. Frontend silently swaps in the new tokens - Nimal never
   even notices this happened, and his session continues
   uninterrupted
*/