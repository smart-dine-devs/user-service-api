package com.devstack.SmartDine.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {

  private final HttpStatus status;
  private final String errorCode;

  protected BaseException(String message, HttpStatus status, String errorCode) {
    super(message);
    this.status = status;
    this.errorCode = errorCode;
  }
}


/*
==========================================================
 BASEEXCEPTION.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE AN "exceptions" FOLDER AT ALL
WHY: groups all custom errors together
----------------------------------------------------------
As SmartDine grows, MANY different specific problems can
happen - wrong OTP, expired OTP, duplicate email, account
suspended, wrong password, and more (you can already see
this in your project tree: AccountSuspendedException,
DuplicateEmailException, EmailNotVerifiedException,
InvalidOtpException, KeyCloakIntegrationException,
OtpExpiredException, OtpMaxAttemptsExceeded,
PasswordMismatchException...). Rather than scattering these
custom error classes randomly across the project, they're
grouped in ONE dedicated "exceptions" folder, making them
easy to find and keeping error-handling logic clearly
separated from entities, DTOs, and config.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
When Nimal tries to log in with an already-suspended
account, SmartDine needs to throw a SPECIFIC, meaningful
error - not just a generic Java exception. Instead of
digging through the whole codebase to find where errors are
defined, any developer knows to look in ONE place: the
exceptions folder.
*/


/*
STEP 2 - WHY CREATE BaseException AT ALL
WHY: shared blueprint for all errors
----------------------------------------------------------
Notice ALL those exception classes in your project tree
(AccountSuspendedException, DuplicateEmailException, etc.)
likely need the SAME basic ingredients - an HTTP status code
and an error code, alongside the message. Rather than
repeating this same status/errorCode setup in EVERY single
exception class, BaseException is a shared PARENT class that
all the others extend from, giving them this common
structure for free.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Instead of writing status and errorCode fields, a
constructor, and getters SEPARATELY inside
AccountSuspendedException, DuplicateEmailException,
InvalidOtpException, and every other exception class
individually, they ALL extend BaseException and just supply
their OWN specific message/status/errorCode values - the
image confirms this: "8 inheritors" shown next to
BaseException, meaning 8 other exception classes already
extend it.
*/


/*
STEP 3 - public abstract class BaseException extends RuntimeException
WHY: cannot be used directly
----------------------------------------------------------
"extends RuntimeException" means this is a Java exception
that can be thrown like any built-in exception.
"abstract" means BaseException itself can NEVER be directly
created/thrown on its own - it MUST be extended by a more
specific child class first. This makes sense, since "Base
Exception" alone doesn't describe any REAL problem - it's
just a shared template.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
You could NEVER write:
throw new BaseException("some error", HttpStatus.BAD_REQUEST, "SOME_CODE");
(this would fail to compile, since it's abstract)

But you CAN write:
throw new AccountSuspendedException(...)
(a real, specific child class extending BaseException)
*/


/*
STEP 4 - @Getter
WHY: auto-creates getStatus/getErrorCode
----------------------------------------------------------
Lombok shortcut - automatically generates getStatus() and
getErrorCode() methods, so whatever code eventually CATCHES
one of these exceptions can read back the status/errorCode
values without writing those getter methods manually.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Somewhere else in the project (likely a global exception
handler), code can catch a thrown exception and call:
exception.getStatus()     -> e.g. HttpStatus.FORBIDDEN
exception.getErrorCode()  -> e.g. "ACCOUNT_SUSPENDED"

to build a proper ErrorResponse (from your earlier file)
to send back to the frontend.
*/


/*
STEP 5 - private final HttpStatus status;
WHY: tells which HTTP status to return
----------------------------------------------------------
Stores WHICH HTTP status code this specific error should
result in when sent back to the frontend - matches the
"status" field explained earlier in ErrorResponse.java.
"final" means once set in the constructor, it can never
change afterward.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
If Nimal's account is suspended and he tries to log in,
AccountSuspendedException (extending BaseException) would
set status = HttpStatus.FORBIDDEN (403) - telling the
frontend exactly what kind of HTTP error this is.
*/


/*
STEP 6 - private final String errorCode;
WHY: tells specific error name
----------------------------------------------------------
Stores the SmartDine-specific error code identifying exactly
WHICH problem occurred - same concept as errorCode explained
in ErrorResponse.java earlier.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
errorCode = "ACCOUNT_SUSPENDED"       (from AccountSuspendedException)
errorCode = "DUPLICATE_EMAIL"         (from DuplicateEmailException)
errorCode = "OTP_EXPIRED"             (from OtpExpiredException)

Each specific exception class supplies its OWN unique code
when calling BaseException's constructor.
*/


/*
STEP 7 - protected BaseException(String message, HttpStatus status, String errorCode)
WHY: sets up error details once
----------------------------------------------------------
The constructor - "protected" means it can only be called by
BaseException itself or by its CHILD classes (like
AccountSuspendedException), never directly from unrelated
code elsewhere in the project. This matches the "abstract"
rule from Step 3 - reinforcing that only child classes are
allowed to actually create one of these.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Inside AccountSuspendedException.java, you'd likely see
something like:
public class AccountSuspendedException extends BaseException {
    public AccountSuspendedException() {
        super("Your account has been suspended", HttpStatus.FORBIDDEN, "ACCOUNT_SUSPENDED");
    }
}
This calls BaseException's protected constructor, supplying
its own specific message/status/errorCode values.
*/


/*
STEP 8 - super(message);
WHY: passes message to RuntimeException
----------------------------------------------------------
Passes the message UP to RuntimeException itself (the
built-in Java class BaseException extends), so standard
Java exception behavior (like getMessage(), stack traces,
etc.) still works normally, exactly like any other exception.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
If this exception ever gets logged or printed, calling
exception.getMessage() would correctly return:
"Your account has been suspended"
just like it would for any standard Java exception.
*/


/*
STEP 9 - this.status = status; / this.errorCode = errorCode;
WHY: stores the extra error details
----------------------------------------------------------
Saves the status and errorCode values (passed in by whichever
child class called this constructor) into BaseException's own
fields, so they can later be read back via the @Getter methods
from Step 4.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
After AccountSuspendedException calls
super("...", HttpStatus.FORBIDDEN, "ACCOUNT_SUSPENDED"),
these two lines store FORBIDDEN and "ACCOUNT_SUSPENDED"
inside the exception object, ready to be retrieved later.
*/


/*
STEP 10 - PUTTING IT ALL TOGETHER (FULL FLOW)
WHY: shows the complete error-throwing flow
----------------------------------------------------------
1. Nimal tries to log in, but his account was previously
   suspended
2. Somewhere in your login logic:
   throw new AccountSuspendedException();
3. AccountSuspendedException's constructor calls
   super("Your account has been suspended", HttpStatus.FORBIDDEN, "ACCOUNT_SUSPENDED")
   which runs BaseException's constructor
4. This exception now carries a message, status (403), and
   errorCode ("ACCOUNT_SUSPENDED") all bundled together
5. A global exception handler elsewhere in the project
   catches this exception, reads exception.getStatus() and
   exception.getErrorCode() using the @Getter methods, and
   builds a proper ErrorResponse to send back to Nimal's
   frontend - consistent, structured, and reusing the SAME
   pattern for all 8+ different exception types shown in
   your project tree
*/