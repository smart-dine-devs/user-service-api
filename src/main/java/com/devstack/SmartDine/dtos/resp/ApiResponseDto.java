package com.devstack.SmartDine.dtos.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponseDto<T> {
    private boolean success;
    private String message;
    private T data;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResponseDto<T> success(String message, T data) {
        return ApiResponseDto.<T>builder()
                .success(true)
                .message(message)
                .data(data).build();
    }


    public static <T> ApiResponseDto<T> success(String message) {
        return ApiResponseDto.<T>builder()
                .success(true)
                .message(message).build();
    }

    public static <T> ApiResponseDto<T> error(String message) {
        return ApiResponseDto.<T>builder()
                .success(false)
                .message(message).build();
    }

}


/*
==========================================================
 APIRESPONSEDTO.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE ApiResponseDto AT ALL
----------------------------------------------------------
Without this class, every controller in SmartDine might
return DIFFERENT shaped responses - one endpoint returns
just a User object, another returns just a String message,
another returns nothing on error. This makes the frontend's
job harder, since it has to guess the shape of every
response differently. ApiResponseDto creates ONE CONSISTENT
WRAPPER used by EVERY endpoint, so the frontend always knows
exactly where to look for success/failure, the message, and
the actual data.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Instead of a login endpoint returning just a raw User object,
and a signup endpoint returning just a String, BOTH now
return the SAME wrapper shape:
{
  "success": true,
  "message": "Login successful",
  "data": { ...user info... },
  "timestamp": "2026-08-27T10:15:30"
}
The frontend can always check response.success first,
regardless of WHICH endpoint it called.
*/


/*
STEP 2 - public class ApiResponseDto<T>
----------------------------------------------------------
The <T> makes this a GENERIC class - meaning "data" can hold
ANY type of object, decided at the moment you actually use
this class, not fixed in advance.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
ApiResponseDto<User>          -> data holds a User object
                                  (e.g. after login)
ApiResponseDto<List<Order>>   -> data holds a LIST of orders
                                  (e.g. Nimal's order history)
ApiResponseDto<String>        -> data holds just plain text

Same wrapper class, reused everywhere, just holding
DIFFERENT types of data depending on the endpoint.
*/


/*
STEP 3 - private boolean success;
----------------------------------------------------------
A simple TRUE/FALSE flag telling the frontend whether the
request worked or failed - the FIRST thing a frontend
typically checks before doing anything else with the response.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal enters the wrong password on the login screen.
Backend responds with: success = false
Frontend checks this FIRST, sees it's false, and shows an
error message instead of trying to log him in.
*/


/*
STEP 4 - private String message;
----------------------------------------------------------
A human-readable message describing what happened - shown
directly to the user, or logged for debugging.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
success = true,  message = "Login successful"
success = false, message = "Invalid email or password"

The frontend can display this message directly in a toast/
alert box without needing to know any technical details
about WHY it failed.
*/


/*
STEP 5 - private T data;
----------------------------------------------------------
The ACTUAL payload/result of the request - using the
generic <T> type, so it can be a User, an Order, a List,
a String, or literally anything, depending on which
endpoint built this response.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
After Nimal successfully logs in:
data = { "id": "...", "firstName": "Nimal", "email": "nimal@gmail.com" }

After a FAILED request (like wrong password), data is often
left EMPTY/null entirely - there's nothing meaningful to send
back, only the error message matters in that case.
*/


/*
STEP 6 - @Builder.Default private LocalDateTime timestamp = LocalDateTime.now();
----------------------------------------------------------
Automatically records the EXACT moment this response object
was created, unless a builder call explicitly overrides it.
@Builder.Default ensures this default value is actually used
when building via .builder(), since Lombok's @Builder would
otherwise ignore field defaults and leave it null.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal calls the login endpoint at exactly 10:15:30 AM.
The response includes: "timestamp": "2026-08-27T10:15:30"
Useful for debugging (e.g. matching a user's bug report
against server logs from that exact moment), and for
the frontend to display "Last updated at..." style info.
*/


/*
STEP 7 - public static <T> ApiResponseDto<T> success(String message, T data)
----------------------------------------------------------
A SHORTCUT method (a "static factory method") that builds a
SUCCESSFUL response in one clean line, instead of manually
writing out .builder().success(true).message(...).data(...)
.build() every single time in every controller.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Inside your login controller, instead of writing:
ApiResponseDto.<User>builder()
    .success(true)
    .message("Login successful")
    .data(nimalUser)
    .build();

You simply write:
ApiResponseDto.success("Login successful", nimalUser);

Much shorter, and used the exact same way everywhere in
the project.
*/


/*
STEP 8 - public static <T> ApiResponseDto<T> success(String message)
----------------------------------------------------------
A second shortcut for successful responses that have NO data
to return - just a success message alone (data stays null).
This is a METHOD OVERLOAD - same method name, different
parameters, Java picks the right one based on how many
arguments you pass.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
When Nimal successfully changes his password, there's no
meaningful "data" to send back - just confirmation it worked:
ApiResponseDto.success("Password changed successfully");

Response becomes:
{
  "success": true,
  "message": "Password changed successfully",
  "data": null,
  "timestamp": "..."
}
*/


/*
STEP 9 - public static <T> ApiResponseDto<T> error(String message)
----------------------------------------------------------
A shortcut for building a FAILED response - sets
success = false automatically, with just a message
explaining what went wrong (data stays null).
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal enters the wrong OTP code during verification:
ApiResponseDto.error("Invalid or expired OTP code");

Response becomes:
{
  "success": false,
  "message": "Invalid or expired OTP code",
  "data": null,
  "timestamp": "..."
}
*/


/*
STEP 10 - PUTTING IT ALL TOGETHER (FULL FLOW)
----------------------------------------------------------
1. Nimal calls POST /api/auth/login with his email/password
2. If his credentials are correct, the controller returns:
   ApiResponseDto.success("Login successful", nimalUser)
3. If his password is wrong, the controller instead returns:
   ApiResponseDto.error("Invalid email or password")
4. EITHER WAY, the frontend receives the exact SAME wrapper
   shape (success, message, data, timestamp), and can write
   ONE consistent piece of logic to handle ALL API responses
   across the entire SmartDine app, regardless of which
   endpoint was called
*/



/*
==========================================================
 APIRESPONSEDTO - WITHOUT vs WITH (REAL EXAMPLE ONLY)
==========================================================
*/

/*
WITHOUT ApiResponseDto:
----------------------------------------------------------
Nimal taps "Login" on the app.
Frontend calls POST /api/auth/login.

Case A - correct password:
Backend returns JUST: { "id": "...", "firstName": "Nimal", "email": "..." }
(a raw User object, nothing else)

Frontend has NO WAY to know if this "worked" except by
guessing - maybe check if the object exists?

Case B - wrong password:
Backend returns JUST: "Invalid credentials"
(a plain error string, TOTALLY DIFFERENT SHAPE than Case A)

Frontend needs SEPARATE, SPECIAL logic:
"if response looks like a User object -> success"
"if response looks like a string -> error"

This guessing game has to be repeated DIFFERENTLY
for every single endpoint, since none of them agree
on a shape.
*/


/*
WITH ApiResponseDto:
----------------------------------------------------------
Nimal taps "Login" on the app.
Frontend calls POST /api/auth/login.

Case A - correct password:
Backend returns:
{ "success": true, "message": "Login successful", "data": {nimal's info} }

Frontend checks: response.success === true
-> takes response.data, logs Nimal in, shows home screen

Case B - wrong password:
Backend returns:
{ "success": false, "message": "Invalid email or password", "data": null }

Frontend checks: response.success === false
-> shows "Invalid email or password" as a red error message

SAME frontend code handles BOTH cases - always the same
shape, always check "success" first, no guessing needed.
*/