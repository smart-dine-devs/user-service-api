package com.devstack.SmartDine.exceptions;

import org.springframework.http.HttpStatus;

public class OtpExpiredException extends BaseException {

    public OtpExpiredException(String email) {
        super("OTP has expired for: " + email, HttpStatus.BAD_REQUEST, "OTP_EXPIRED");
    }
}



/*
==========================================================
 OTPEXPIREDEXCEPTION.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE OtpExpiredException AT ALL
WHY: represents OTP timed out
----------------------------------------------------------
Connects directly back to OtpConfig.java's expiryMinutes
field from earlier. This exception represents a DIFFERENT
problem than InvalidOtpException - here, Nimal might type
the EXACT CORRECT code, but too much time has passed since
it was sent, so it's no longer considered valid.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
SmartDine sends Nimal OTP "483920" at 10:00 AM, valid for 5
minutes (expiryMinutes = 5 from OtpConfig.java). Nimal gets
distracted, and doesn't enter it until 10:08 AM - even though
he types "483920" PERFECTLY correctly, it's already expired
by then. Your verification logic checks the timestamp, sees
it's past the allowed window, and throws this exception
INSTEAD OF InvalidOtpException, since the code itself wasn't
wrong - it just ran out of time.
*/


/*
STEP 2 - public OtpExpiredException(String email)
WHY: takes the affected email
----------------------------------------------------------
Accepts the email whose OTP expired, so the message/logs can
identify EXACTLY which account this happened for - similar
pattern to DuplicateEmailException and
EmailNotVerifiedException from earlier.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Somewhere in your OTP verification logic:
if (Instant.now().isAfter(otpExpiryTime)) {
    throw new OtpExpiredException(email);
}
*/


/*
STEP 3 - super("OTP has expired for: " + email, HttpStatus.BAD_REQUEST, "OTP_EXPIRED");
WHY: fills in BaseException's fields
----------------------------------------------------------
message   -> "OTP has expired for: nimal@gmail.com"
status    -> HttpStatus.BAD_REQUEST (400 - same status as
             InvalidOtpException, but a DIFFERENT errorCode
             distinguishes "wrong code" from "expired code")
errorCode -> "OTP_EXPIRED"
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Frontend receives:
{ status: 400, errorCode: "OTP_EXPIRED",
  message: "OTP has expired for: nimal@gmail.com" }

Frontend can show a DIFFERENT, more helpful message than
InvalidOtpException would trigger: "Your code has expired -
we've sent you a new one" (possibly even auto-triggering a
fresh OTP send), rather than just "wrong code, try again" -
these are genuinely different situations needing different
guidance for Nimal.
*/


/*
STEP 4 - WHY errorCode MATTERS HERE SPECIFICALLY
WHY: same status, different meaning
----------------------------------------------------------
This is the SAME pattern flagged earlier with
DuplicateEmailException vs EmailNotVerifiedException -
InvalidOtpException and OtpExpiredException BOTH use
HttpStatus.BAD_REQUEST (400), but represent completely
different problems ("you typed it wrong" vs "you took too
long"). Without the separate errorCode field, the frontend
would have NO way to tell these two apart just from the
status number alone.
*/


/*
STEP 5 - PUTTING IT ALL TOGETHER (FULL FLOW)
WHY: shows the complete usage flow
----------------------------------------------------------
1. Nimal receives OTP "483920" at 10:00 AM, valid for 5
   minutes
2. He waits too long and submits it at 10:08 AM
3. Your verification logic checks the current time against
   the stored expiry time (tracked via Redis, from
   RedisConfig earlier)
4. It's expired, so:
   throw new OtpExpiredException("nimal@gmail.com");
5. This carries message, status (400), and errorCode
   ("OTP_EXPIRED") - inherited from BaseException
6. A global exception handler catches it, builds an
   ErrorResponse, and sends it back
7. Frontend shows: "Your code has expired, please request a
   new one" - guiding Nimal to the correct next action,
   rather than just telling him his input was wrong
*/