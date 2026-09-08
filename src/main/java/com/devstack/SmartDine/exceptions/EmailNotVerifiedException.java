package com.devstack.SmartDine.exceptions;

import org.springframework.http.HttpStatus;

public class EmailNotVerifiedException extends BaseException {

    public EmailNotVerifiedException(String email) {
        super("Email not verified for account: " + email, HttpStatus.FORBIDDEN, "EMAIL_NOT_VERIFIED");
    }
}



/*
==========================================================
 EMAILNOTVERIFIEDEXCEPTION.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE EmailNotVerifiedException AT ALL
WHY: represents unverified email login
----------------------------------------------------------
Represents a DIFFERENT specific problem: someone trying to
log in (or access something) BEFORE they've verified their
email - connecting directly back to UserStatus.java from
much earlier, where PENDING_VERIFICATION vs ACTIVE was
explained.

Also written correctly - properly extends BaseException.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal signs up, but never clicks the verification link sent
to his email. His account status stays
UserStatus.PENDING_VERIFICATION. A week later, he tries to
log in anyway. Your login logic checks his status, sees it's
NOT active yet, and throws this exception instead of letting
him fully log in.
*/


/*
STEP 2 - public EmailNotVerifiedException(String email)
WHY: takes the unverified email
----------------------------------------------------------
Accepts the email in question, so the message clearly states
WHICH account still needs verification.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Somewhere in your login logic:
if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
    throw new EmailNotVerifiedException(user.getEmail());
}
*/


/*
STEP 3 - super("Email not verified for account: " + email, HttpStatus.FORBIDDEN, "EMAIL_NOT_VERIFIED");
WHY: fills in BaseException's fields
----------------------------------------------------------
message   -> "Email not verified for account: nimal@gmail.com"
status    -> HttpStatus.FORBIDDEN (403 - same status used by
             AccountSuspendedException, but a DIFFERENT
             errorCode distinguishes the two situations)
errorCode -> "EMAIL_NOT_VERIFIED"
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Frontend receives:
{ status: 403, errorCode: "EMAIL_NOT_VERIFIED",
  message: "Email not verified for account: nimal@gmail.com" }

and can show Nimal specifically: "Please verify your email
before logging in," with maybe a "resend verification email"
button - a DIFFERENT message and action than what
AccountSuspendedException would trigger, even though both
use the same 403 status underneath.
*/


/*
==========================================================
 PUTTING BOTH TOGETHER - WHY errorCode MATTERS SO MUCH
WHY: distinguishes same-status errors
==========================================================
Notice both exceptions here use HttpStatus.FORBIDDEN (403),
same as AccountSuspendedException from before. If the
frontend only checked the STATUS number, it couldn't tell
these three DIFFERENT problems apart - this is EXACTLY why
errorCode exists as a separate field: "EMAIL_NOT_VERIFIED"
vs "ACCOUNT_SUSPENDED" vs any other 403 case all need
DIFFERENT frontend messages/actions, even though they share
the identical HTTP status number.
*/
