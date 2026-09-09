package com.devstack.SmartDine.service;

import com.devstack.SmartDine.dtos.req.OtpVerifyRequestDto;

public interface OtpService {
    void sendOtp(String email);
    void verifyOtp(OtpVerifyRequestDto dto);
    void validateOtp(String email);
}


/*
==========================================================
 OTPSERVICE.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE OtpService AT ALL
WHY: centralizes all OTP logic
----------------------------------------------------------
This is the piece that ACTUALLY ties together OtpConfig
(expiry/length/maxAttempts rules), RedisConfig (temporary
storage), EmailService (sending the code), and the OTP-
related exceptions (InvalidOtpException, OtpExpiredException,
OtpMaxAttemptsExceededException) you've already built - all
of that logic lives in ONE place instead of being scattered
across AuthServiceImpl directly.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Whenever ANY part of SmartDine needs an OTP sent or checked
(signup verification, password reset, maybe even login 2FA
later), it calls OtpService rather than each feature
re-implementing its own OTP generation/storage/checking logic
separately.
*/


/*
STEP 2 - void sendOtp(String email);
WHY: generates and delivers a code
----------------------------------------------------------
Takes just an email, and is responsible for the WHOLE
generation process: creating a random code (using
OtpConfig's length setting), storing it temporarily (using
RedisConfig, with expiry from OtpConfig's expiryMinutes), and
sending it (via EmailService's sendOtpEmail).
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
otpService.sendOtp("nimal@gmail.com");

Internally, this single call: generates "483920" (6 digits,
from OtpConfig), saves it to Redis with a 5-minute expiry
(from OtpConfig), resets any old attempt counter, and calls
emailService.sendOtpEmail(...) to deliver it to Nimal.
*/


/*
STEP 3 - void verifyOtp(OtpVerifyRequestDto dto);
WHY: checks code and counts attempts
----------------------------------------------------------
Takes the DTO containing BOTH email and the code Nimal
entered (OtpVerifyRequestDto, from your earlier req DTOs),
and handles the FULL verification process - checking if it
matches, checking if it's expired, and checking/incrementing
the attempt counter, throwing the appropriate exception for
each specific failure case.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
otpService.verifyOtp(new OtpVerifyRequestDto("nimal@gmail.com", "483921"));

Internally, this method:
- reads the STORED code from Redis for this email
- if the stored code is missing/expired -> throws
  OtpExpiredException
- if the entered code doesn't match -> increments the
  attempt counter, throws InvalidOtpException
- if attempts now exceed maxAttempts (3) -> throws
  OtpMaxAttemptsExceededException instead
- if it matches correctly -> marks this email as verified,
  clears the Redis entry
*/


/*
STEP 4 - void validateOtp(String email);
WHY: confirms verification already happened
----------------------------------------------------------
A DIFFERENT, simpler check than verifyOtp - this doesn't take
a code at all, just an email. Likely used to confirm "has
THIS email already been successfully OTP-verified" at some
LATER point in the flow (e.g. right before finalizing signup
or completing a password reset), rather than checking a fresh
code against Redis again.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
After Nimal successfully calls verifyOtp() once, his
verification status might be marked somewhere (e.g. a
short-lived flag in Redis). Later, right before
AuthServiceImpl actually finalizes his account/status change,
it might call:
otpService.validateOtp("nimal@gmail.com");
to CONFIRM verification genuinely happened recently, rather
than trusting the frontend blindly - protecting against
someone skipping the verify step and calling a later endpoint
directly.
*/


/*
STEP 5 - PUTTING IT ALL TOGETHER (FULL FLOW)
WHY: shows how this fits the bigger picture
----------------------------------------------------------
1. Nimal signs up -> AuthServiceImpl calls
   otpService.sendOtp("nimal@gmail.com")
2. Nimal receives and enters his code -> frontend calls the
   verify endpoint -> AuthServiceImpl calls
   otpService.verifyOtp(dto)
3. If wrong/expired/too many attempts, the appropriate
   exception bubbles up, gets caught by
   GlobalExceptionHandler, and a clear error reaches Nimal
4. If correct, verification succeeds, and later in the flow
   (e.g. finalizing signup) AuthServiceImpl might call
   otpService.validateOtp("nimal@gmail.com") to confirm
   the verification is still valid/recent before proceeding
5. AuthServiceImpl NEVER touches Redis or OTP generation
   logic directly - it just calls OtpService's three simple
   methods, keeping OTP logic cleanly separated, the same
   pattern already seen with EmailService
*/