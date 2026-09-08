package com.devstack.SmartDine.exceptions;

import org.springframework.http.HttpStatus;

public class OtpMaxAttemptsExceededException extends BaseException {

    public OtpMaxAttemptsExceededException(String email) {
        super("Maximum OTP verification attempts exceeded for: " + email, HttpStatus.TOO_MANY_REQUESTS, "OTP_MAX_ATTEMPTS_EXCEEDED");
    }
}


/*
==========================================================
 OTPMAXATTEMPTSEXCEEDEDEXCEPTION.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE OtpMaxAttemptsExceededException AT ALL
WHY: represents too many wrong tries
----------------------------------------------------------
Connects directly back to OtpConfig.java's maxAttempts field
from earlier (default = 3). This is the exception that
InvalidOtpException hinted at previously - it's what gets
thrown once Nimal has ALREADY guessed wrong TOO MANY times,
protecting against someone endlessly trying random codes to
guess the correct OTP.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal's real OTP is "483920". He mistypes it wrong on
attempt 1, wrong again on attempt 2, and wrong a THIRD time
(matching maxAttempts = 3 from OtpConfig.java). Instead of
letting him try a 4th time, SmartDine now blocks him
entirely with THIS exception - forcing him to request a
brand new OTP rather than keep guessing against the old one.
*/


/*
STEP 2 - public OtpMaxAttemptsExceededException(String email)
WHY: takes the blocked email
----------------------------------------------------------
Accepts the email that hit the attempt limit, so the
message/logs identify exactly WHOSE verification got
blocked.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Somewhere in your OTP verification logic, likely tracking a
counter in Redis alongside the OTP itself:
if (attemptCount >= otpProperties.getMaxAttempts()) {
    throw new OtpMaxAttemptsExceededException(email);
}
*/


/*
STEP 3 - super("Maximum OTP verification attempts exceeded for: " + email, HttpStatus.TOO_MANY_REQUESTS, "OTP_MAX_ATTEMPTS_EXCEEDED");
WHY: fills in BaseException's fields
----------------------------------------------------------
message   -> "Maximum OTP verification attempts exceeded for: nimal@gmail.com"
status    -> HttpStatus.TOO_MANY_REQUESTS (429 - a status
             SPECIFICALLY meant for "you've done this too
             many times, slow down" - a perfect match here,
             and DIFFERENT from the 400 used by
             InvalidOtpException/OtpExpiredException)
errorCode -> "OTP_MAX_ATTEMPTS_EXCEEDED"
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Frontend receives:
{ status: 429, errorCode: "OTP_MAX_ATTEMPTS_EXCEEDED",
  message: "Maximum OTP verification attempts exceeded for: nimal@gmail.com" }

Frontend shows: "Too many incorrect attempts - please request
a new code," and would typically DISABLE the OTP input box
entirely, only allowing a "resend OTP" action instead of
letting Nimal keep typing guesses.
*/


/*
STEP 4 - WHY THIS STATUS IS DIFFERENT (429 vs 400)
WHY: rate-limit vs bad input
----------------------------------------------------------
InvalidOtpException and OtpExpiredException both used 400
BAD_REQUEST, because each SINGLE attempt itself was invalid
in some way (wrong code, or expired code). This exception is
different - the INDIVIDUAL code entered might even be
correct or incorrect, it doesn't matter anymore; the real
problem is the PATTERN of behavior (too many tries total).
429 TOO_MANY_REQUESTS exists specifically for this kind of
"rate limiting" situation, distinct from a single bad request.
*/


/*
STEP 5 - PUTTING IT ALL TOGETHER (FULL FLOW)
WHY: shows the complete usage flow
----------------------------------------------------------
1. Nimal receives OTP "483920"
2. He enters it wrong on attempt 1 -> InvalidOtpException,
   attempt counter increments to 1
3. Wrong again on attempt 2 -> InvalidOtpException, counter
   increments to 2
4. Wrong a 3rd time -> counter reaches maxAttempts (3), so
   INSTEAD of InvalidOtpException again, this throws:
   throw new OtpMaxAttemptsExceededException("nimal@gmail.com");
5. This carries message, status (429), and errorCode
   ("OTP_MAX_ATTEMPTS_EXCEEDED") - inherited from
   BaseException
6. A global exception handler catches it, builds an
   ErrorResponse, and sends it back
7. Frontend locks the OTP input and shows: "Too many
   attempts - please request a new code," protecting the
   account from further guessing until Nimal starts a fresh
   OTP request
*/