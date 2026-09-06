package com.devstack.SmartDine.dtos.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final Instant timestamp;
    private final int status;
    private final String errorCode;
    private final String message;
    private final Map<String, String> fieldErrors;

    public static ErrorResponse of(HttpStatus httpStatus, String errorCode, String message) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(httpStatus.value())
                .errorCode(errorCode)
                .message(message)
                .build();
    }
}

/*
Whenever ANYTHING goes wrong in SmartDine, ErrorResponse
tells the frontend:

        - WHEN it happened     -> timestamp
- WHAT kind of error    -> status (401, 404, 500, etc.)
- WHICH specific error  -> errorCode (e.g. "INVALID_CREDENTIALS")
- WHY, in plain words   -> message (e.g. "Invalid email or password")
- WHICH FIELDS, if any  -> fieldErrors (only for signup/validation
        style errors with multiple bad fields)

/*
==========================================================
 ERRORRESPONSE.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE ErrorResponse AT ALL
WHY: standard shape for errors
----------------------------------------------------------
When something goes wrong (validation failure, wrong login,
server crash), the frontend needs a CONSISTENT, DETAILED
error shape to work with - not just a plain string. This
class is a dedicated, structured error format, separate from
ApiResponseDto, specifically built to carry rich error
details like status codes and field-specific validation
errors.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal submits the signup form with an invalid email. Instead
of just getting back a vague "something went wrong," the
backend returns a structured ErrorResponse telling the
frontend EXACTLY what field failed and why.
*/


/*
STEP 2 - @JsonInclude(JsonInclude.Include.NON_NULL)
WHY: hides empty fields
----------------------------------------------------------
Tells Jackson (the library that converts Java objects to
JSON) to SKIP any field that is null when building the JSON
response - keeping the output clean instead of cluttering it
with fields that have nothing useful in them.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
If fieldErrors is null (not a validation error, just a
generic error), the JSON response simply OMITS that field
entirely:
{ "timestamp": "...", "status": 401, "errorCode": "...", "message": "..." }

instead of cluttering it with:
{ ..., "fieldErrors": null }
*/


/*
STEP 3 - private final Instant timestamp;
WHY: records exact error time
----------------------------------------------------------
Records the EXACT moment this error occurred. "final" means
once set, it can never be changed afterward - fitting for a
timestamp, since an error's occurrence time shouldn't ever
be modified after the fact.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal's login fails at 10:15:30 AM.
timestamp = "2026-08-27T10:15:30Z"

Useful for matching a user's bug report against server logs
from that exact moment, to investigate what actually
happened.
*/


/*
STEP 4 - private final int status;
WHY: standard HTTP status number
----------------------------------------------------------
The numeric HTTP status code representing what kind of error
this is - a widely recognized standard across ALL web APIs,
not just SmartDine's.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
status = 400  -> bad request (e.g. invalid signup data)
status = 401  -> unauthorized (e.g. wrong password)
status = 404  -> not found (e.g. order ID doesn't exist)
status = 500  -> internal server error (e.g. unexpected crash)

Frontend code (and browser dev tools) can immediately
recognize these standard numbers, regardless of which
specific SmartDine endpoint failed.
*/


/*
STEP 5 - private final String errorCode;
WHY: app-specific error label
----------------------------------------------------------
A custom, SmartDine-specific label identifying the error
more precisely than a generic HTTP status alone - useful
since multiple different problems can share the SAME status
code (e.g. many different issues can all be "400 Bad
Request").
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
errorCode = "INVALID_CREDENTIALS"   (status 401)
errorCode = "EMAIL_ALREADY_EXISTS"  (status 400)
errorCode = "OTP_EXPIRED"           (status 400)

The frontend can check THIS specific code to decide exactly
which error message or behavior to trigger, rather than just
knowing "some 400 error happened."
*/


/*
STEP 6 - private final String message;
WHY: human-readable explanation
----------------------------------------------------------
A readable explanation of what went wrong, meant to be shown
directly to the user or logged for debugging - same role as
"message" in ApiResponseDto.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
message = "Invalid email or password"
message = "This email is already registered"

Frontend displays this text directly in an error banner
for Nimal to read.
*/


/*
STEP 7 - private final Map<String, String> fieldErrors;
WHY: pinpoints exact bad field
----------------------------------------------------------
A KEY-VALUE map used ONLY for validation errors, where MORE
THAN ONE field might be wrong at once - each key is the
field name, each value is what's wrong with it specifically.
This is the piece a plain ApiResponseDto.error(message)
alone couldn't handle well.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal submits signup with an empty firstName AND an invalid
email at the same time. fieldErrors becomes:
{
  "firstName": "FirstName is required",
  "email": "Email must be a valid email address"
}

The frontend can show BOTH errors directly under their
matching form fields, instead of just one vague top-level
message.
*/


/*
STEP 8 - public static ErrorResponse of(HttpStatus httpStatus, String errorCode, String message)
WHY: quick shortcut to build error
----------------------------------------------------------
A shortcut factory method (same pattern as
ApiResponseDto.success()/.error()) that builds a simple
ErrorResponse in one line, automatically filling in the
current timestamp and converting the HttpStatus enum into
its numeric value.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Instead of manually writing:
ErrorResponse.builder()
    .timestamp(Instant.now())
    .status(401)
    .errorCode("INVALID_CREDENTIALS")
    .message("Invalid email or password")
    .build();

You simply write:
ErrorResponse.of(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid email or password");

Notice this shortcut does NOT include fieldErrors - it's
meant for simple, single-message errors (like wrong login),
while validation errors with MULTIPLE field problems would
be built manually using .builder() with fieldErrors included.
*/


/*
STEP 9 - PUTTING IT ALL TOGETHER (FULL FLOW)
WHY: shows the complete error flow
----------------------------------------------------------
1. Nimal submits the signup form with an invalid email and
   a missing firstName
2. Validation fails on SignupRequestDto (the @NotBlank and
   @Email rules from earlier)
3. Backend builds an ErrorResponse manually, filling
   fieldErrors with BOTH problems at once
4. Frontend receives:
   { "timestamp": "...", "status": 400, "errorCode": "VALIDATION_FAILED",
     "message": "Validation failed", "fieldErrors": { "firstName": "...", "email": "..." } }
5. Frontend displays each field error directly under its
   matching input box on the signup form

Separately, for a simple login failure (no specific field to
blame):
1. Nimal enters the wrong password
2. Backend calls: ErrorResponse.of(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid email or password")
3. Frontend receives a clean error with NO fieldErrors key at
   all (thanks to @JsonInclude(NON_NULL)), and simply shows
   the message as a general error banner
*/