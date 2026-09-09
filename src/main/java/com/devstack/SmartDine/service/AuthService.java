package com.devstack.SmartDine.service;

import com.devstack.SmartDine.dtos.req.*;
import com.devstack.SmartDine.dtos.resp.AuthResponseDto;
import com.devstack.SmartDine.dtos.resp.TokenRefreshResponseDto;

public interface AuthService {
    void signup(SignupRequestDto dto);
    AuthResponseDto login(LoginRequestDto dto);
    AuthResponseDto loginWithGoogle(GoogleLoginRequestDto dto);
    AuthResponseDto loginWithGitHub(GitHubLoginRequestDto dto);
    TokenRefreshResponseDto refreshToken(TokenRefreshRequestDto dto);
    void logout(String refreshToken);
}


/*
==========================================================
 AUTHSERVICE.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE A "service" LAYER AT ALL
WHY: holds the actual business logic
----------------------------------------------------------
Remember way back when comparing MERN vs Spring Boot -
Repository = data access ONLY, Controller = HTTP request/
response ONLY. The SERVICE layer is the missing middle piece:
it holds the actual BUSINESS LOGIC/RULES (checking passwords
match, verifying OTPs, deciding when to throw
UserNotFoundException, calling Keycloak, etc.) - separate
from both the raw database access AND the HTTP handling.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
When Nimal signs up, MANY things need to happen in order:
check email isn't a duplicate, check passwords match, create
his account in Keycloak, save his User row, send an OTP.
None of this belongs in the Controller (which should just
handle HTTP) or the Repository (which only knows "save" and
"find") - this is exactly SERVICE-layer work.
*/


/*
STEP 2 - WHY AuthService IS AN INTERFACE (not a class)
WHY: defines contract, not logic itself
----------------------------------------------------------
This file only DECLARES what methods exist (signup, login,
etc.) and what they accept/return - it contains NO actual
logic itself. This is a "contract" - a promise that SOME
class, somewhere, will implement these methods. Notice the
"impl" folder visible in your project tree, sitting right
next to AuthService - that's EXACTLY where the real
implementation (AuthServiceImpl, presumably) will live,
separate from this interface.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Think of this like a job description: "whoever takes THIS
job must be able to handle signup, login, Google login,
GitHub login, refresh tokens, and logout." The interface
doesn't do the work itself - it just defines WHAT must be
doable. AuthServiceImpl (in the impl folder) is the actual
"employee" that does the real work matching this description.
*/


/*
STEP 3 - WHY USE AN INTERFACE INSTEAD OF JUST A REGULAR CLASS
WHY: allows swapping implementations later
----------------------------------------------------------
Using an interface means your CONTROLLER only needs to know
about AuthService (the contract), not the specific
implementation details. This makes it possible to swap in a
DIFFERENT implementation later (e.g. for testing, using a
FAKE AuthService that doesn't actually call Keycloak) without
changing any code that USES AuthService elsewhere.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Your AuthController would inject AuthService (the interface),
never AuthServiceImpl directly. Spring automatically provides
the real implementation behind the scenes - if you ever wrote
a test and wanted a fake version that doesn't really call
Keycloak/Redis, you could swap it in without touching the
controller code at all.
*/


/*
STEP 4 - void signup(SignupRequestDto dto);
WHY: handles new account creation
----------------------------------------------------------
Takes the validated signup data (from SignupRequestDto,
explained much earlier) and is responsible for the ENTIRE
signup process - checking for duplicates, verifying
passwords match, creating the Keycloak account, saving the
User row, and triggering an OTP.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal submits his signup form. Controller receives it,
validates it (via SignupRequestDto's annotations), then
simply calls:
authService.signup(dto);
All the actual heavy lifting happens inside
AuthServiceImpl's version of this method.
*/


/*
STEP 5 - AuthResponseDto login(LoginRequestDto dto);
WHY: handles normal email/password login
----------------------------------------------------------
Takes Nimal's email/password (LoginRequestDto), verifies them,
and RETURNS an AuthResponseDto (from much earlier) containing
his tokens and profile info if successful.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
authService.login(dto) internally: finds Nimal via
UserRepository.findByEmail() (throwing
UserNotFoundException if missing), checks his account status
(throwing EmailNotVerifiedException or
AccountSuspendedException if relevant), verifies his
password, generates tokens using JwtConfig, and packages it
all into the AuthResponseDto that gets returned.
*/


/*
STEP 6 - AuthResponseDto loginWithGoogle(GoogleLoginRequestDto dto); / loginWithGitHub(GitHubLoginRequestDto dto);
WHY: handles social login separately
----------------------------------------------------------
Two SEPARATE methods for the two OTHER login methods
mentioned back in AuthProvider.java's enum (GOOGLE, GITHUB) -
each takes its OWN specific request DTO (an idToken for
Google, an authorization code for GitHub), since these
external providers send back completely different kinds of
proof.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
If Nimal instead chooses "Sign in with Google" on the app,
Google gives his frontend an idToken, which gets sent to:
authService.loginWithGoogle(googleLoginRequestDto);

This method would verify that token is genuinely from
Google, find or create a matching User (with
provider = AuthProvider.GOOGLE), and return the same
AuthResponseDto shape as regular login - keeping a CONSISTENT
return type regardless of HOW Nimal chose to log in.
*/


/*
STEP 7 - TokenRefreshResponseDto refreshToken(TokenRefreshRequestDto dto);
WHY: issues new tokens without login
----------------------------------------------------------
Connects directly to TokenRefreshRequestDto and
TokenRefreshResponseDto from earlier - takes Nimal's
refreshToken and returns a fresh set of tokens, matching the
FULL FLOW already explained back when TokenRefreshResponseDto
was covered.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
When Nimal's accessToken expires mid-session, his frontend
calls this method (through the controller) with his
refreshToken, and gets back a brand new
TokenRefreshResponseDto - exactly the flow described earlier.
*/


/*
STEP 8 - void logout(String refreshToken);
WHY: invalidates the session
----------------------------------------------------------
Takes just the refreshToken (not a whole DTO, since this is
simple enough to need just one value) and is responsible for
INVALIDATING it, so it can no longer be used to get new
access tokens - effectively ending Nimal's session
server-side.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal taps "Logout" on the app. Frontend sends his current
refreshToken to authService.logout(refreshToken), which
likely marks that specific token as invalid/blacklisted
(possibly stored in Redis, from RedisConfig earlier) - so
even if someone stole that OLD refreshToken later, it
wouldn't work anymore to get new access tokens.
*/


/*
STEP 9 - ABOUT THE WARNINGS SHOWN IN YOUR SCREENSHOT
WHY: expected at this stage, not bugs
----------------------------------------------------------
The "5 problems" and yellow warnings ("is never used") are
NOT actual errors - they're just IntelliJ noting that nothing
in your project currently CALLS these methods yet (no
controller or implementation exists using them). This is
COMPLETELY NORMAL at this stage of building the project - once
you create AuthServiceImpl (implementing this interface) and
an AuthController (calling these methods), these warnings will
disappear naturally.
*/


/*
STEP 10 - PUTTING IT ALL TOGETHER (FULL FLOW)
WHY: shows how this fits the bigger picture
----------------------------------------------------------
1. AuthController (not yet built) will receive HTTP requests
   and validate incoming DTOs
2. Controller calls the appropriate AuthService method
   (signup, login, loginWithGoogle, etc.) - only knowing the
   INTERFACE, not the implementation details
3. Spring automatically provides AuthServiceImpl (from the
   impl folder) as the real, working version behind this
   interface
4. AuthServiceImpl does the actual heavy lifting - talking
   to UserRepository, Keycloak, Redis, throwing exceptions
   like UserNotFoundException when needed
5. Results flow back up: AuthServiceImpl -> AuthService
   interface -> Controller -> wrapped in ApiResponseDto ->
   sent to Nimal's frontend
*/