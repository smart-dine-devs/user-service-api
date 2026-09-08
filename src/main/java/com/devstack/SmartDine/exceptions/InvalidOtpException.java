package com.devstack.SmartDine.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidOtpException extends BaseException {

    public InvalidOtpException() {
        super("Invalid OTP code provided", HttpStatus.BAD_REQUEST, "INVALID_OTP");
    }
}


/*
==========================================================
 INVALIDOTPEXCEPTION.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE InvalidOtpException AT ALL
WHY: represents wrong OTP entered
----------------------------------------------------------
Another one of BaseException's inheritors, connecting
directly back to OtpConfig.java and OtpResponseDto.java from
earlier. Its ONLY job is to represent ONE specific problem:
Nimal typing an OTP code that simply doesn't match what was
actually sent to him.

Written correctly - properly extends BaseException.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
SmartDine sends Nimal the OTP "483920" (matching the length
setting from OtpConfig.java). Nimal mistypes it as "483921".
Your verification logic compares what he entered against
what's stored in Redis, finds they DON'T match, and throws
this exception.
*/


/*
STEP 2 - public InvalidOtpException()
WHY: no details needed here
----------------------------------------------------------
Unlike DuplicateEmailException or EmailNotVerifiedException
(which both took an email parameter), this constructor takes
NO parameters at all. That's because the message doesn't
need to be personalized with any extra detail - "the code you
entered is wrong" is the same message regardless of WHICH
user or WHICH email triggered it.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Somewhere in your OTP verification logic:
if (!enteredOtp.equals(storedOtp)) {
    throw new InvalidOtpException();
}

No email or extra info needs to be passed in - the exception
is thrown exactly the same way regardless of who triggered
it.
*/


/*
STEP 3 - super("Invalid OTP code provided", HttpStatus.BAD_REQUEST, "INVALID_OTP");
WHY: fills in BaseException's fields
----------------------------------------------------------
message   -> "Invalid OTP code provided" (fixed, generic text)
status    -> HttpStatus.BAD_REQUEST (400 - "what you sent me
             doesn't work")
errorCode -> "INVALID_OTP"
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Frontend receives:
{ status: 400, errorCode: "INVALID_OTP",
  message: "Invalid OTP code provided" }

and shows Nimal directly on the verification screen: "The
code you entered is incorrect - please try again," letting
him re-enter it (up to whatever maxAttempts limit was set in
OtpConfig.java earlier, before OtpMaxAttemptsExceeded - one
of the OTHER exceptions visible in your project tree - would
presumably get thrown instead).
*/


/*
STEP 4 - PUTTING IT ALL TOGETHER (FULL FLOW)
WHY: shows the complete usage flow
----------------------------------------------------------
1. Nimal receives OTP "483920" via SMS/email
2. He types "483921" by mistake and submits it (using
   OtpVerifyRequestDto from earlier)
3. Your verification logic compares his input against the
   value stored in Redis (from RedisConfig, explained
   earlier)
4. They don't match, so:
   throw new InvalidOtpException();
5. This carries message, status (400), and errorCode
   ("INVALID_OTP") - inherited from BaseException
6. A global exception handler catches it, builds an
   ErrorResponse, and sends it back
7. Frontend shows: "The code you entered is incorrect,"
   letting Nimal try again
*/