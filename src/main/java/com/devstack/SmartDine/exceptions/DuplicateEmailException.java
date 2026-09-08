package com.devstack.SmartDine.exceptions;

import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends BaseException {

    public DuplicateEmailException(String email) {
        super("Email already registered: " + email, HttpStatus.CONFLICT, "DUPLICATE_EMAIL");
    }
}


/*
==========================================================
 DUPLICATEEMAILEXCEPTION.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE DuplicateEmailException AT ALL
WHY: represents email already taken
----------------------------------------------------------
Another one of the "8 inheritors" of BaseException. Its ONLY
job is to represent ONE specific problem: someone trying to
sign up with an email that's already registered in the
system.

*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal already has an account with nimal@gmail.com. A few
months later, he forgets and tries to sign up AGAIN using
the same email. Your signup logic checks
existsByEmail("nimal@gmail.com") (from UserRepository,
explained earlier), finds it's TRUE, and throws this
exception instead of letting a duplicate account be created.
*/


/*
STEP 2 - public DuplicateEmailException(String email)
WHY: takes the duplicate email
----------------------------------------------------------
The constructor accepts the email that was already taken, so
the error message can specify EXACTLY which email caused the
conflict.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Somewhere in your signup logic:
if (userRepository.existsByEmail(email)) {
    throw new DuplicateEmailException(email);
}
*/


/*
STEP 3 - super("Email already registered: " + email, HttpStatus.CONFLICT, "DUPLICATE_EMAIL");
WHY: fills in BaseException's fields
----------------------------------------------------------
message   -> "Email already registered: nimal@gmail.com"
status    -> HttpStatus.CONFLICT (409 - "this clashes with
             something that already exists")
errorCode -> "DUPLICATE_EMAIL"
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Frontend eventually receives:
{ status: 409, errorCode: "DUPLICATE_EMAIL",
  message: "Email already registered: nimal@gmail.com" }

and can show Nimal: "This email is already in use - did you
mean to log in instead?"
*/


