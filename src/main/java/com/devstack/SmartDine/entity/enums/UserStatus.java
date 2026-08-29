package com.devstack.SmartDine.entity.enums;

public enum UserStatus {
    PENDING_VERIFICATION,
    ACTIVE
}



/*
==========================================================
 USERSTATUS.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - PURPOSE OF THIS ENUM
----------------------------------------------------------
UserStatus restricts what STAGE a user's account is in.
Same reasoning as AuthProvider - only fixed, allowed values,
no typos, no inconsistent text.

PENDING_VERIFICATION -> user signed up but hasn't confirmed
                         email/phone yet
ACTIVE                -> user is verified and can fully use the app
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal signs up on the SmartDine app.
status = UserStatus.PENDING_VERIFICATION   (default)

He clicks the verification link sent to his email.
status = UserStatus.ACTIVE

Now he is allowed to place food orders on SmartDine.
*/


/*
STEP 2 - WHY NOT JUST USE A PLAIN STRING
----------------------------------------------------------
Without enum:
status = "pending"       (lowercase)
status = "Pending"       (mixed case)
status = "PENDING"       (uppercase, but not matching the real value)
Different text values that all mean the same intent, but the
database would treat them as different, causing search/logic bugs.

With enum:
status = UserStatus.PENDING_VERIFICATION
Only one correct value, IDE autocompletes it, no typos possible.
*/