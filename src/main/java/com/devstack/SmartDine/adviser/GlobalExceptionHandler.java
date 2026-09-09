package com.devstack.SmartDine.adviser;

import com.devstack.SmartDine.dtos.resp.ErrorResponse;
import com.devstack.SmartDine.exceptions.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        log.warn("Business exception [{}]: {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.getStatus())
                .body(ErrorResponse.of(ex.getStatus(), ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("VALIDATION_FAILED")
                .message("Request validation failed")
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred"));
    }
}



/*
==========================================================
 GLOBALEXCEPTIONHANDLER.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE AN "adviser" FOLDER AT ALL
WHY: holds app-wide error catching
----------------------------------------------------------
This folder holds classes that "advise" (watch over) your
ENTIRE application's controllers at once, rather than being
tied to one specific feature. Kept separate from config,
entity, dtos, and exceptions folders, since its job is
uniquely about CATCHING problems AFTER they happen anywhere
in the app, not defining data or settings.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Every single controller in SmartDine (auth, orders, menu,
etc.) can throw any of the exceptions you've built so far
(UserNotFoundException, InvalidOtpException,
DuplicateEmailException, and more) - rather than writing
try/catch blocks manually in EVERY controller method, ONE
GlobalExceptionHandler catches them ALL, everywhere, in one
place.
*/


/*
STEP 2 - WHY CREATE GlobalExceptionHandler AT ALL
WHY: turns exceptions into responses
----------------------------------------------------------
This is the FINAL piece connecting everything you've learned
about exceptions - it's the class that actually CATCHES every
thrown exception (BaseException and its children, validation
failures, and anything else unexpected) and converts them into
proper ErrorResponse objects sent back to the frontend,
matching the exact status/errorCode/message each exception
carries.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Without this class, if InvalidOtpException got thrown
somewhere and nothing caught it, Spring Boot would return a
generic, ugly, default error page/response - NOT the clean,
structured ErrorResponse format you built earlier. This class
is what makes sure EVERY exception, no matter which one,
results in the SAME consistent, well-formatted error shape.
*/


/*
STEP 3 - @Slf4j
WHY: gives you a logger tool
----------------------------------------------------------
Lombok shortcut that automatically creates a "log" object you
can use to write log messages (like log.warn(...) and
log.error(...) seen below), without manually declaring
Logger logger = LoggerFactory.getLogger(...) yourself.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Instead of writing:
private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

@Slf4j generates this automatically behind the scenes -
you just use "log" directly, as shown in this class.
*/


/*
STEP 4 - @RestControllerAdvice
WHY: applies to all controllers
----------------------------------------------------------
Tells Spring: "this class contains exception-handling logic
that should apply GLOBALLY, across EVERY @RestController in
the entire application" - not just one specific controller.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Whether InvalidOtpException gets thrown inside your AUTH
controller, or a completely different exception gets thrown
inside your ORDERS controller later, THIS SAME class catches
BOTH - you don't need a separate exception handler written
per controller.
*/


/*
STEP 5 - @ExceptionHandler(BaseException.class) handleBaseException(BaseException ex)
WHY: catches all your custom errors
----------------------------------------------------------
This ONE method catches EVERY exception that extends
BaseException - meaning ALL 9 of the specific exceptions
you've built so far (AccountSuspendedException,
DuplicateEmailException, EmailNotVerifiedException,
InvalidOtpException, KeyCloakIntegrationException,
OtpExpiredException, OtpMaxAttemptsExceededException,
PasswordMismatchException, UserNotFoundException) are ALL
caught by this SINGLE method, since they all share the SAME
parent class.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal tries to log in with an unverified email. Somewhere
deep in your service layer:
throw new EmailNotVerifiedException(email);

This method catches it, reads ex.getStatus() (403),
ex.getErrorCode() ("EMAIL_NOT_VERIFIED"), and ex.getMessage()
("Email not verified for account: nimal@gmail.com") - values
that were set back when EmailNotVerifiedException called
BaseException's constructor - and builds the final response
Nimal's frontend actually receives.
*/


/*
STEP 6 - log.warn("Business exception [{}]: {}", ex.getErrorCode(), ex.getMessage());
WHY: records error for developers
----------------------------------------------------------
Writes a log entry BEFORE responding, so your team can see
in server logs that this specific business error occurred -
useful for monitoring how often certain errors happen (e.g.
"how many people are hitting OTP_MAX_ATTEMPTS_EXCEEDED?").
"warn" level is used here (not "error") since these are
EXPECTED, normal business situations - not actual system
bugs/crashes.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Server log shows:
WARN: Business exception [EMAIL_NOT_VERIFIED]: Email not verified for account: nimal@gmail.com

Your team can scan logs like this to spot patterns, e.g.
noticing MANY users hitting EMAIL_NOT_VERIFIED might suggest
your verification email isn't reliably reaching people.
*/


/*
STEP 7 - return ResponseEntity.status(ex.getStatus()).body(ErrorResponse.of(ex.getStatus(), ex.getErrorCode(), ex.getMessage()));
WHY: builds the actual HTTP response
----------------------------------------------------------
Takes the exception's OWN status/errorCode/message (whichever
specific exception it was) and builds the ACTUAL HTTP
response sent back to the frontend, using the .of() shortcut
from ErrorResponse.java built earlier.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
For EmailNotVerifiedException specifically, the frontend
receives:
HTTP status: 403
Body: { "status": 403, "errorCode": "EMAIL_NOT_VERIFIED",
        "message": "Email not verified for account: nimal@gmail.com" }

Notice this ONE method correctly handles ALL 9 different
exceptions, since it just reads WHATEVER status/errorCode/
message each individual exception happens to carry.
*/


/*
STEP 8 - @ExceptionHandler(MethodArgumentNotValidException.class) handleValidationException(...)
WHY: catches DTO validation failures
----------------------------------------------------------
This catches a DIFFERENT kind of problem - not your custom
BaseException children, but Spring's OWN built-in exception
that fires automatically when @NotBlank/@Size/@Email rules
(from SignupRequestDto.java, LoginRequestDto.java, etc.) FAIL
during validation.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal submits signup with an empty firstName AND an invalid
email at the same time. Spring automatically throws
MethodArgumentNotValidException BEFORE your controller code
even runs - this method catches THAT specific Spring
exception, separate from your own custom exceptions.
*/


/*
STEP 9 - Map<String, String> fieldErrors = new HashMap<>(); for (FieldError fe : ex.getBindingResult().getFieldErrors()) { fieldErrors.put(fe.getField(), fe.getDefaultMessage()); }
WHY: collects every field's error message
----------------------------------------------------------
Loops through EVERY field that failed validation, and builds
a Map connecting each field NAME to its SPECIFIC error
message - this is exactly what fills the "fieldErrors" field
in ErrorResponse.java from earlier, used for multi-field
validation problems.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
If Nimal's firstName was empty AND his email was invalid,
this loop builds:
fieldErrors = {
  "firstName": "FirstName is required",
  "email": "Email must be a valid email address"
}

exactly matching the custom messages you wrote earlier in
SignupRequestDto.java's @NotBlank(message = "...") and
@Email(message = "...") annotations.
*/


/*
STEP 10 - ErrorResponse.builder()...fieldErrors(fieldErrors).build(); return ResponseEntity.badRequest().body(body);
WHY: sends structured validation errors
----------------------------------------------------------
Builds the final ErrorResponse MANUALLY (using .builder(),
not the simpler .of() shortcut), specifically because this
case NEEDS to include fieldErrors - exactly matching what was
explained back in ErrorResponse.java's Step 8, where .of()
was shown to NOT support fieldErrors, requiring manual
building instead for validation-style errors.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Frontend receives:
{ "status": 400, "errorCode": "VALIDATION_FAILED",
  "message": "Request validation failed",
  "fieldErrors": { "firstName": "...", "email": "..." } }

Frontend can now display EACH error message directly under
its matching input box on the signup form, exactly as
described when ErrorResponse.java's fieldErrors field was
first explained.
*/


/*
STEP 11 - @ExceptionHandler(Exception.class) handleGenericException(Exception ex)
WHY: safety net for anything unexpected
----------------------------------------------------------
This is the FINAL fallback/safety net - catches ANY exception
that ISN'T a BaseException and ISN'T a validation failure -
essentially any COMPLETELY UNEXPECTED error, like a genuine
bug, a null pointer exception, a database connection failure,
or anything else nobody planned for specifically.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Imagine a genuine bug causes a NullPointerException somewhere
deep in your code, completely unrelated to any of your custom
exceptions. Without this handler, Spring Boot would show a
raw, ugly default error page. WITH this handler, it still
gets converted into your clean, structured ErrorResponse
format instead.
*/


/*
STEP 12 - log.error("Unhandled exception", ex);
WHY: logs full crash details
----------------------------------------------------------
Uses "error" level (not "warn," unlike Step 6) since this
represents a genuine, UNEXPECTED problem - something worth
your team's immediate attention, not a normal expected
business situation. Passing "ex" as the second argument logs
the FULL stack trace too, giving developers everything needed
to actually debug what went wrong.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Server log shows the COMPLETE technical crash details
(exception type, message, and every line of code involved),
letting your team investigate a real bug - while the
frontend, in contrast, only ever sees the safe, generic
message from the next step.
*/


/*
STEP 13 - return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred"));
WHY: hides internal details from user
----------------------------------------------------------
Deliberately sends back a VAGUE, generic message to the
frontend ("An unexpected error occurred") - NOT the real
technical exception details, since exposing raw internal
error messages/stack traces to users would be both confusing
AND a security risk (it could reveal internal system details
to potential attackers).
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Frontend receives:
{ "status": 500, "errorCode": "INTERNAL_ERROR",
  "message": "An unexpected error occurred" }

Nimal sees a generic "something went wrong, please try
again" message - while your team's LOGS (from Step 12) have
the FULL real details needed to actually fix the bug.
*/


/*
STEP 14 - PUTTING IT ALL TOGETHER (FULL FLOW)
WHY: shows how all 3 handlers connect
----------------------------------------------------------
1. ANY controller in SmartDine throws ANY exception
2. If it's one of your 9 custom exceptions (extending
   BaseException) -> handleBaseException() catches it,
   logs a warning, and responds using THAT exception's own
   status/errorCode/message
3. If it's a DTO validation failure (like SignupRequestDto's
   rules failing) -> handleValidationException() catches it,
   builds a fieldErrors map, and responds with status 400 +
   VALIDATION_FAILED + all individual field problems
4. If it's ANYTHING else completely unexpected (a real bug)
   -> handleGenericException() catches it, logs the FULL
   crash details for your team, and responds with a safe,
   generic 500 error to the frontend
5. In ALL THREE cases, the frontend ALWAYS receives the SAME
   consistent ErrorResponse shape (status, errorCode,
   message, optional fieldErrors) - regardless of what
   actually went wrong anywhere in the entire SmartDine
   backend
*/