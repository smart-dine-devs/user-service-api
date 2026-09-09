package com.devstack.SmartDine.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String toName, String otp);
    void sendWelcomeEmail(String toEmail, String toName);
    void sendResetPasswordEmail(String toEmail, String toName, String otp);
}


/*
==========================================================
 EMAILSERVICE.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE EmailService AT ALL
WHY: separates email logic from auth
----------------------------------------------------------
Connects directly back to BrevoConfig.java from earlier -
that file set up the CONNECTION to Brevo (the email
service), but something still needs to decide WHEN to send
an email and WITH WHAT content. Rather than stuffing email-
sending code directly inside AuthServiceImpl, it's pulled
out into its OWN separate service - keeping "how to log
someone in" and "how to send an email" as two SEPARATE
responsibilities, even though they often work together.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
When Nimal signs up, AuthServiceImpl handles the account-
creation logic, but when it's time to actually SEND him the
OTP email, it calls out to EmailService to do THAT specific
job - rather than AuthServiceImpl trying to know Brevo's API
details itself.
*/


/*
STEP 2 - WHY THIS IS ALSO AN INTERFACE
WHY: same reasoning as AuthService
----------------------------------------------------------
Same pattern explained in AuthService.java - this defines
WHAT email actions must be possible (send OTP, send welcome,
send reset password), without containing the actual Brevo
API calls itself. A separate EmailServiceImpl (likely in an
"impl" folder, matching AuthService's structure) will contain
the real logic using the RestClient bean from BrevoConfig.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
AuthServiceImpl only needs to know EmailService exists and
what methods it offers - it never needs to know HOW the email
actually gets sent (Brevo's API structure, template IDs,
etc.) - that's entirely EmailServiceImpl's job to handle.
*/


/*
STEP 3 - void sendOtpEmail(String toEmail, String toName, String otp);
WHY: sends the verification code
----------------------------------------------------------
Sends Nimal his OTP code by email - connects to
BrevoConfig's templates.getOtp() (template ID 101, from the
earlier example) and OtpConfig's generated code together.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
emailService.sendOtpEmail("nimal@gmail.com", "Nimal", "483920");

Behind the scenes, EmailServiceImpl would use the Brevo
RestClient, template ID 101 (the OTP-styled template), and
insert "Nimal" and "483920" into the appropriate placeholders
in that template before sending.
*/


/*
STEP 4 - void sendWelcomeEmail(String toEmail, String toName);
WHY: greets new users after verification
----------------------------------------------------------
Sends a welcome email, connecting to BrevoConfig's
templates.getWelcome() (template ID 102 from the earlier
example) - typically triggered AFTER Nimal successfully
verifies his OTP, confirming his account is now fully active.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
emailService.sendWelcomeEmail("nimal@gmail.com", "Nimal");

Nimal receives an email like "Welcome to SmartDine, Nimal!"
right after his status changes from PENDING_VERIFICATION to
ACTIVE (from UserStatus.java, explained much earlier).
*/


/*
STEP 5 - void sendResetPasswordEmail(String toEmail, String toName, String otp);
WHY: helps user recover access
----------------------------------------------------------
Sends a password-reset OTP, connecting to BrevoConfig's
templates.getResetPassword() (template ID 103 from earlier)
and ForgotPasswordRequestDto/ResetPasswordRequestDto from
your earlier request DTOs.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal forgets his password and requests a reset. Backend
generates a new OTP and calls:
emailService.sendResetPasswordEmail("nimal@gmail.com", "Nimal", "719234");

Nimal receives an email with THIS new code, which he'll enter
alongside his new password using ResetPasswordRequestDto.
*/


/*
STEP 6 - PUTTING IT ALL TOGETHER (FULL FLOW)
WHY: shows how this fits the bigger picture
----------------------------------------------------------
1. Nimal signs up -> AuthServiceImpl generates an OTP, then
   calls emailService.sendOtpEmail(...) to deliver it
2. Nimal verifies his OTP correctly -> his status becomes
   ACTIVE, and AuthServiceImpl calls
   emailService.sendWelcomeEmail(...)
3. Weeks later, Nimal forgets his password -> he requests a
   reset, AuthServiceImpl generates a new OTP, and calls
   emailService.sendResetPasswordEmail(...)
4. In ALL three cases, AuthServiceImpl never touches Brevo's
   API directly - it just calls EmailService's simple
   methods, keeping email-sending logic cleanly separated
   from authentication logic
*/