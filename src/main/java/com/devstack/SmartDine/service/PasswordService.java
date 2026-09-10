package com.devstack.SmartDine.service;

import com.devstack.SmartDine.dtos.req.ForgotPasswordRequestDto;
import com.devstack.SmartDine.dtos.req.ResetPasswordRequestDto;

public interface PasswordService {
    void forgotPassword(ForgotPasswordRequestDto dto);
    void resetPassword(ResetPasswordRequestDto dto);
}



/*
==========================================================
 PASSWORDSERVICE.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE PasswordService AT ALL
WHY: separates password recovery logic
----------------------------------------------------------
"Forgot my password" is its OWN distinct flow, different from
normal login/signup - it involves OTP verification (via
OtpService), sending a special email (via EmailService's
sendResetPasswordEmail), and updating the stored password
hash. Pulling this into its OWN service keeps
AuthServiceImpl focused on login/signup, rather than growing
into one giant class handling every possible account action.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal forgets his password months after signing up. This
entire "I forgot my password" journey - request a reset code,
then actually set a new password - is handled by
PasswordService, completely separate from his original
signup/login logic in AuthService.
*/


/*
STEP 2 - void forgotPassword(ForgotPasswordRequestDto dto);
WHY: starts the recovery process
----------------------------------------------------------
Takes just Nimal's email (from ForgotPasswordRequestDto,
one of your earlier req DTOs) and kicks off the recovery
process - checking the account exists, generating an OTP
(likely via OtpService.sendOtp), and sending it specifically
through EmailService.sendResetPasswordEmail rather than the
regular OTP email.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
passwordService.forgotPassword(new ForgotPasswordRequestDto("nimal@gmail.com"));

Internally, this likely:
- checks a user with this email actually exists (throwing
  UserNotFoundException if not)
- generates a fresh OTP code
- calls emailService.sendResetPasswordEmail("nimal@gmail.com", "Nimal", otpCode)
  so Nimal receives a reset-specific email, using template
  ID 103 from BrevoConfig, rather than the generic OTP
  template
*/


/*
STEP 3 - void resetPassword(ResetPasswordRequestDto dto);
WHY: completes the recovery process
----------------------------------------------------------
Takes the FULL reset details (ResetPasswordRequestDto,
containing email, otpCode, newPassword, and
confirmPassword, from your earlier req DTOs) and finishes the
job - verifying the OTP is correct, checking the new
passwords match (throwing PasswordMismatchException if not),
and actually UPDATING Nimal's stored password hash.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
passwordService.resetPassword(ResetPasswordRequestDto.builder()
    .email("nimal@gmail.com")
    .otpCode("719234")
    .newPassword("newSecurePass123")
    .confirmPassword("newSecurePass123")
    .build());

Internally, this likely:
- calls otpService.verifyOtp(...) using the email + otpCode,
  which can throw InvalidOtpException/OtpExpiredException/
  OtpMaxAttemptsExceededException if something's wrong
- checks newPassword.equals(confirmPassword), throwing
  PasswordMismatchException if they don't match (the SAME
  exception explained earlier for signup, reused here too)
- hashes the new password and saves it to Nimal's User row
  via UserRepository
*/


/*
STEP 4 - WHY THIS METHOD REUSES OtpService AND EmailService
WHY: avoids duplicating OTP/email logic
----------------------------------------------------------
Notice PasswordService doesn't generate its OWN OTP logic or
talk to Redis/Brevo directly - it calls INTO OtpService and
EmailService instead, the same services AuthServiceImpl also
relies on. This is the whole point of splitting things into
separate, focused services: password reset REUSES the exact
same OTP-checking rules (length, expiry, max attempts) that
signup verification uses, instead of duplicating that logic
separately here.
*/


/*
STEP 5 - PUTTING IT ALL TOGETHER (FULL FLOW)
WHY: shows how this fits the bigger picture
----------------------------------------------------------
1. Nimal forgets his password, taps "Forgot Password" and
   enters his email
2. Frontend calls forgotPassword endpoint ->
   passwordService.forgotPassword(dto)
3. Backend verifies his account exists, generates an OTP,
   and emails it via EmailService's reset-specific template
4. Nimal receives the code, enters it along with his new
   password on the reset screen
5. Frontend calls resetPassword endpoint ->
   passwordService.resetPassword(dto)
6. Backend verifies the OTP (reusing OtpService's rules),
   checks the new passwords match, and updates his password
7. Nimal can now log in again using his NEW password
*/