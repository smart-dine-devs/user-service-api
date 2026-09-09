package com.devstack.SmartDine.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {

    public UserNotFoundException(String identifier) {
        super("User not found: " + identifier, HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
    }
}


/*
==========================================================
 USERNOTFOUNDEXCEPTION.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE UserNotFoundException AT ALL
WHY: represents user that doesn't exist
----------------------------------------------------------
Connects directly back to UserRepository.java's
findByEmail(), findById(), and findByKeycloakId() methods
from earlier - ALL of these return an Optional<User>,
meaning "might find someone, might find NO ONE." This
exception is what gets thrown whenever your code calls
.orElseThrow(...) on one of those Optionals and finds it
empty - representing "I looked for this user, and they
simply don't exist."
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Someone tries to log in using an email that was NEVER
registered on SmartDine at all - maybe a typo, or maybe they
never signed up in the first place. Your login logic calls
userRepository.findByEmail(email), gets back an EMPTY
Optional, and throws this exception instead of letting a
null/missing user silently cause a crash elsewhere.
*/


/*
STEP 2 - public UserNotFoundException(String identifier)
WHY: flexible - accepts any identifier
----------------------------------------------------------
Accepts a generic "identifier" String, rather than
specifically "email" - this is DELIBERATE, since a user
could be looked up by DIFFERENT things (email, ID, or
keycloakId, based on UserRepository's multiple find methods
from earlier), and this ONE exception class can represent
"not found" regardless of WHICH kind of identifier was used
to search.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Somewhere in your service layer, THIS SAME exception class
gets reused across different lookup scenarios:

userRepository.findByEmail(email)
    .orElseThrow(() -> new UserNotFoundException(email));

userRepository.findById(userId)
    .orElseThrow(() -> new UserNotFoundException(userId.toString()));

Both throw the SAME exception type, just with a different
identifier value passed in depending on HOW the user was
being searched for.
*/


/*
STEP 3 - super("User not found: " + identifier, HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
WHY: fills in BaseException's fields
----------------------------------------------------------
message   -> "User not found: nimal@gmail.com" (or whatever
             identifier was used)
status    -> HttpStatus.NOT_FOUND (404 - the standard,
             widely recognized status for "what you're
             looking for doesn't exist")
errorCode -> "USER_NOT_FOUND"
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Frontend receives:
{ status: 404, errorCode: "USER_NOT_FOUND",
  message: "User not found: nimal@gmail.com" }

Frontend shows: "No account found with this email" on the
login screen - though in practice, for LOGIN specifically,
many apps DELIBERATELY show a more generic message like
"Invalid email or password" instead, to avoid revealing
whether an email exists or not (a security/privacy
consideration worth being aware of, even though the
backend's actual error here is more specific).
*/


/*
STEP 4 - .orElseThrow() PATTERN - HOW THIS ACTUALLY GETS USED
WHY: connects back to UserRepository
----------------------------------------------------------
Optional<User>.orElseThrow(supplier) is a common Java pattern:
"if the Optional HAS a value, give it to me normally; if it's
EMPTY, run this supplier function instead, which throws an
exception." This is precisely how UserNotFoundException gets
triggered in real code - not by manually checking
if (result.isEmpty()) yourself every time, but by chaining
.orElseThrow() directly onto the repository call.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException(email));

If findByEmail finds Nimal -> "user" variable gets his real
User object, code continues normally.
If findByEmail finds NOBODY -> UserNotFoundException is
thrown immediately, and the rest of this line never executes.
*/


/*
STEP 5 - PUTTING IT ALL TOGETHER (FULL FLOW)
WHY: shows the complete usage flow
----------------------------------------------------------
1. Someone tries to log in with an email that was never
   registered on SmartDine
2. Your login logic calls:
   User user = userRepository.findByEmail(email)
           .orElseThrow(() -> new UserNotFoundException(email));
3. Since no matching row exists, findByEmail returns an
   EMPTY Optional
4. .orElseThrow() immediately throws UserNotFoundException,
   carrying message, status (404), and errorCode
   ("USER_NOT_FOUND") - inherited from BaseException
5. A global exception handler catches it, builds an
   ErrorResponse, and sends it back
6. Frontend shows an appropriate message on the login screen,
   letting the person know this account doesn't exist
*/