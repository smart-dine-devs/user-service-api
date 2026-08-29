package com.devstack.SmartDine.entity.enums;

public enum AuthProvider {
    LOCAL,GOOGLE,GITHUB,KEYCLOAK;
}



/*
==========================================================
 AUTHPROVIDER.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - PURPOSE OF THIS ENUM
----------------------------------------------------------
AuthProvider restricts HOW a user is allowed to sign up.
Without this enum, the "provider" field could be typed as
ANY string, causing inconsistent/messy data.

Without enum:
provider = "local"   (lowercase)
provider = "Local"   (mixed case)
provider = "LOCAL"   (uppercase)
provider = "loacl"   (typo)
All 4 are DIFFERENT text values to the database, even though
they were meant to mean the same thing. This causes bugs.

With enum:
provider = AuthProvider.LOCAL   -> only ONE possible value.
No typos possible. Java will not even compile a wrong one.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal signs up using his email + password on the SmartDine app.
provider = AuthProvider.LOCAL
(means: he signed up directly, not through Google/Facebook)
*/


/*
STEP 2 - IMPORTANT CLARIFICATION
----------------------------------------------------------
AuthProvider.LOCAL has NOTHING to do with the "email" field
or how it is typed/capitalized.

email     -> Nimal's actual email address, e.g. "nimal@gmail.com"
             (plain text, no rule from AuthProvider at all)

provider  -> separate field entirely, just labels HOW he signed up
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
email    = "nimal@gmail.com"     <- normal, lowercase, untouched
provider = AuthProvider.LOCAL    <- separate field, ALL CAPS is
                                     just a Java naming convention
                                     for enum constants
*/


/*
STEP 3 - FUTURE USE
----------------------------------------------------------
Right now only one option exists: LOCAL
Later, more options could be added, such as:
GOOGLE, FACEBOOK
(for "Sign in with Google" style buttons)
*/