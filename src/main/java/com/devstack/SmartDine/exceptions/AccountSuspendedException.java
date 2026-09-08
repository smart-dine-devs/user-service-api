package com.devstack.SmartDine.exceptions;

public class AccountSuspendedException extends RuntimeException {
    public AccountSuspendedException(String email) {
        super("Account suspended: " + email, HttpStatus.FORBIDDEN, "ACCOUNT_SUSPENDED");
    }
}



/*
==========================================================
 ACCOUNTSUSPENDEDEXCEPTION.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE AccountSuspendedException AT ALL
WHY: represents one specific error
----------------------------------------------------------
This is a CHILD class - one of the "8 inheritors" seen
extending BaseException in your project tree. Its ONLY job
is to represent ONE specific, real-world problem: a user
trying to do something while their account is suspended. It
supplies BaseException's shared blueprint with THIS error's
specific message, status, and errorCode.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal's account gets suspended by an admin (maybe for
violating terms of service). Later, Nimal tries to log in.
Your login logic detects this and throws
AccountSuspendedException, carrying his email inside the
message for clarity/logging purposes.
*/


/*
STEP 2 - public AccountSuspendedException(String email)
WHY: takes email as input
----------------------------------------------------------
The constructor accepts Nimal's email as a parameter, so the
error message can be PERSONALIZED/specific to exactly WHICH
account was suspended, rather than a generic message with no
details.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Somewhere in your login logic:
throw new AccountSuspendedException("nimal@gmail.com");

This single line creates a fully-formed exception, carrying
Nimal's email baked directly into its message.
*/


/*
STEP 3 - super("Account suspended: " + email, HttpStatus.FORBIDDEN, "ACCOUNT_SUSPENDED");
WHY: fills in BaseException's fields
----------------------------------------------------------
Calls the PARENT class's constructor, supplying:
message   -> "Account suspended: nimal@gmail.com"
status    -> HttpStatus.FORBIDDEN (403 - "you're not allowed")
errorCode -> "ACCOUNT_SUSPENDED" (SmartDine's specific label)
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
When this exception is eventually caught by a global handler,
it will have:
getMessage()   -> "Account suspended: nimal@gmail.com"
getStatus()    -> 403 FORBIDDEN
getErrorCode() -> "ACCOUNT_SUSPENDED"

All three get turned into a structured ErrorResponse sent
back to the frontend, which can show Nimal a clear "your
account is suspended" message.
*/


/*
==========================================================
 BUG IN THIS CODE - WILL NOT COMPILE
==========================================================

Problem 1:
"extends RuntimeException" should be "extends BaseException"

This class extends the WRONG parent. RuntimeException (Java's
built-in class) does NOT have a constructor that accepts
(String, HttpStatus, String) - only BaseException does (the
one YOU created earlier, with exactly this 3-argument
constructor).

Problem 2 (caused by Problem 1):
super("Account suspended: " + email, HttpStatus.FORBIDDEN, "ACCOUNT_SUSPENDED");

This tries to call RuntimeException's constructor with 3
arguments, but RuntimeException only accepts a single String
message. This line will fail to compile as written.

CORRECTED VERSION:
----------------------------------------------------------
public class AccountSuspendedException extends BaseException {
    public AccountSuspendedException(String email) {
        super("Account suspended: " + email, HttpStatus.FORBIDDEN, "ACCOUNT_SUSPENDED");
    }
}

Just changing "extends RuntimeException" to
"extends BaseException" fixes it - now super(...) correctly
calls BaseException's constructor, which DOES accept exactly
these 3 arguments (message, status, errorCode), matching what
you built earlier.

Also double check: you'll need
import org.springframework.http.HttpStatus;
at the top of this file, since it's used directly here.
*/


/*
STEP 4 - PUTTING IT ALL TOGETHER (FULL FLOW, once fixed)
WHY: shows the complete usage flow
----------------------------------------------------------
1. Nimal's account gets suspended (maybe by an admin action)
2. Nimal tries to log in with nimal@gmail.com
3. Your login logic detects the suspension and throws:
   throw new AccountSuspendedException("nimal@gmail.com");
4. This exception carries message, status (403), and
   errorCode ("ACCOUNT_SUSPENDED") - all inherited from
   BaseException
5. A global exception handler catches it, builds an
   ErrorResponse using ErrorResponse.of(exception.getStatus(),
   exception.getErrorCode(), exception.getMessage())
6. Frontend receives a clean, structured 403 error and shows
   Nimal a message explaining his account is suspended
*/