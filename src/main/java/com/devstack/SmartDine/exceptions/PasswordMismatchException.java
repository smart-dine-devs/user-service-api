package com.devstack.SmartDine.exceptions;

import org.springframework.http.HttpStatus;

public class PasswordMismatchException extends BaseException {

    public PasswordMismatchException() {
        super("Password and confirm password do not match", HttpStatus.BAD_REQUEST, "PASSWORD_MISMATCH");
    }
}



/*
==========================================================
 PASSWORDMISMATCHEXCEPTION.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE PasswordMismatchException AT ALL
WHY: closes the gap flagged earlier
----------------------------------------------------------
This is the exact fix for the GAP flagged much earlier in
SignupRequestDto.java - remember, that DTO only checked
password and confirmPassword were "not blank," but never
verified they actually MATCHED each other. This exception is
what gets thrown when your service layer does that missing
comparison manually, and finds they don't match.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal fills the signup form, but accidentally types his
password slightly differently the second time:
password = "mypassword123"
confirmPassword = "mypassword124"

Both individually pass SignupRequestDto's @NotBlank checks
(neither is empty), so validation lets the request through to
your service layer - which is exactly where THIS exception
now catches the mismatch.
*/


/*
STEP 2 - public PasswordMismatchException()
WHY: no details needed here
----------------------------------------------------------
Takes no parameters, same pattern as InvalidOtpException -
the message doesn't need personalizing with an email or any
other detail, since "your two passwords don't match" means
the same thing regardless of who triggered it.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Somewhere in your signup SERVICE (not the DTO itself, since
the DTO alone can't do this check, as flagged earlier):
if (!request.getPassword().equals(request.getConfirmPassword())) {
    throw new PasswordMismatchException();
}
*/


/*
STEP 3 - super("Password and confirm password do not match", HttpStatus.BAD_REQUEST, "PASSWORD_MISMATCH");
WHY: fills in BaseException's fields
----------------------------------------------------------
message   -> "Password and confirm password do not match"
status    -> HttpStatus.BAD_REQUEST (400 - the input itself
             is the problem, same category as
             InvalidOtpException/OtpExpiredException)
errorCode -> "PASSWORD_MISMATCH"
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Frontend receives:
{ status: 400, errorCode: "PASSWORD_MISMATCH",
  message: "Password and confirm password do not match" }

Frontend shows this directly under the confirmPassword field
on the signup form: "Passwords do not match" - letting Nimal
immediately spot and fix his typo before resubmitting.
*/


/*
STEP 4 - WHY THIS CHECK LIVES IN THE SERVICE, NOT THE DTO
WHY: DTO validates fields, not relationships
----------------------------------------------------------
Bean validation annotations like @NotBlank/@Size/@Email
(used in SignupRequestDto) check ONE field at a time in
isolation - they have no built-in way to compare TWO
different fields against each other. That's exactly why this
comparison couldn't be handled with a simple annotation, and
instead needs actual code in your service layer, using this
exception to report the failure once found.
*/


/*
STEP 5 - PUTTING IT ALL TOGETHER (FULL FLOW)
WHY: shows the complete usage flow
----------------------------------------------------------
1. Nimal submits the signup form with mismatched passwords
2. SignupRequestDto's field-level validation PASSES (both
   fields are non-blank individually)
3. Request reaches your signup service
4. Service manually compares password vs confirmPassword
5. They don't match, so:
   throw new PasswordMismatchException();
6. This carries message, status (400), and errorCode
   ("PASSWORD_MISMATCH") - inherited from BaseException
7. A global exception handler catches it, builds an
   ErrorResponse, and sends it back
8. Frontend shows "Passwords do not match" under the
   confirmPassword field, and Nimal corrects it before
   trying again
*/