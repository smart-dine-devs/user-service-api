package com.devstack.SmartDine.dtos.resp;

import com.devstack.SmartDine.entity.Role;
import com.devstack.SmartDine.entity.enums.AuthProvider;
import com.devstack.SmartDine.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private UUID id;
    private String firstName;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String profilePictureUrl;
    private UserStatus status;
    private AuthProvider provider;
    private boolean emailVerified;
    private boolean phoneVerified;
    private boolean twoFactorEnabled;
    private Set<String> roles;
}


/*
==========================================================
 USERRESPONSEDTO.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE UserResponseDto AT ALL
WHY: safe version of User for frontend
----------------------------------------------------------
User.java (the entity) contains SENSITIVE fields like
passwordHash, keycloakId, and providerId - none of which
should EVER be sent to the frontend. UserResponseDto is a
SAFE, public-facing copy of User, containing only what the
frontend actually needs to display, with all sensitive
internal fields deliberately left out.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
If SmartDine accidentally sent the raw User entity back to
the frontend, Nimal's passwordHash (even though encrypted)
would be exposed in the API response - a real security risk.
UserResponseDto prevents this by only including safe fields
from the very start, referenced earlier as exactly what
"user" inside AuthResponseDto actually holds.
*/


/*
STEP 2 - private UUID id;
WHY: identifies exact user
----------------------------------------------------------
Same unique ID as in User.java - lets the frontend reference
this SPECIFIC user in later requests (e.g. fetching their
orders).
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
id = "a1b2c3d4-5678-..."

Frontend could later call GET /api/orders?userId=a1b2c3d4-...
using this exact ID to fetch Nimal's order history.
*/


/*
STEP 3 - private String firstName; / private String lastname;
WHY: displays user's name
----------------------------------------------------------
Nimal's name, shown directly in the UI (e.g. "Welcome,
Nimal!"). Note: "lastname" here is lowercase "n" - slightly
inconsistent with User.java's "lastName" (capital N) - worth
fixing to match exactly, otherwise JSON serialization will
output "lastname" instead of the expected "lastName" key,
which could confuse frontend code expecting camelCase
consistency.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
firstName = "Nimal"
lastname  = "Perera"    (note: outputs as "lastname" in
                          JSON, not "lastName")
*/


/*
STEP 4 - private String email; / private String phoneNumber;
WHY: shows contact details
----------------------------------------------------------
Nimal's contact info, safe to display back to HIM (his own
account details page, for example).
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
email = "nimal@gmail.com"
phoneNumber = "+94771234567"

Shown on Nimal's own profile/settings screen so he can
review or edit them.
*/


/*
STEP 5 - private String profilePictureUrl;
WHY: shows profile photo
----------------------------------------------------------
Link to Nimal's profile photo, so the frontend can display
his avatar directly using this URL.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
profilePictureUrl = "https://cdn.smartdine.com/nimal.jpg"

Frontend uses this directly as an <img> source to show
Nimal's photo in the top corner of the app.
*/


/*
STEP 6 - private UserStatus status; / private AuthProvider provider;
WHY: shows account stage and login type
----------------------------------------------------------
Same enums explained in earlier files (UserStatus.java,
AuthProvider.java) - included here so the frontend can adjust
its behavior based on Nimal's current account stage or how
he signed up.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
status = "ACTIVE"    -> frontend allows full access
status = "PENDING_VERIFICATION" -> frontend shows a banner:
                                    "please verify your email"

provider = "LOCAL"   -> frontend shows a normal "change
                         password" option
provider = "GOOGLE"  -> frontend might HIDE the "change
                         password" option, since Google
                         manages his password, not SmartDine
*/


/*
STEP 7 - private boolean emailVerified; / phoneVerified; / twoFactorEnabled;
WHY: shows verification status flags
----------------------------------------------------------
True/false flags letting the frontend show relevant prompts
or settings toggles based on Nimal's current verification
state.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
emailVerified = true, phoneVerified = false
-> frontend shows: "Please verify your phone number" banner,
   but does NOT show an email verification prompt, since
   that's already done

twoFactorEnabled = false
-> frontend shows a toggle: "Enable Two-Factor Authentication"
   in Nimal's security settings
*/


/*
STEP 8 - private Set<String> roles;
WHY: shows permissions/access level
----------------------------------------------------------
A list of Nimal's roles, converted to simple Strings for the
frontend (rather than sending the full Role entity objects,
which may carry extra internal data not meant for the
frontend).
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
roles = ["customer"]

Frontend checks: if roles contains "admin", show the admin
dashboard link; otherwise, hide it. For Nimal (a regular
customer), only the normal ordering screens are shown.
*/


/*
STEP 9 - PUTTING IT ALL TOGETHER (FULL FLOW)
WHY: shows the complete usage flow
----------------------------------------------------------
1. Nimal logs in successfully
2. Backend takes his full User entity (with passwordHash,
   keycloakId, etc.) and CONVERTS it into a UserResponseDto,
   deliberately leaving out sensitive fields
3. This UserResponseDto becomes the "user" field inside
   AuthResponseDto, from the earlier file
4. Frontend receives ONLY the safe fields - displays his
   name, photo, verification status, and adjusts UI behavior
   based on his role and provider - without ever seeing his
   passwordHash or other internal-only data



   SignupRequestDto  = Frontend -> Backend
                     ("req" folder = REQUEST, data coming IN
                      from the frontend, like Nimal's signup
                      form submission)

UserResponseDto   = Backend -> Frontend
                     ("resp" folder = RESPONSE, data going OUT
                      to the frontend, like Nimal's safe
                      profile info after login)


   Real example, both directions in one flow:

Nimal fills the signup form and hits submit
-> Frontend sends SignupRequestDto to backend (REQUEST, incoming)

Backend creates his account, logs him in
-> Backend sends UserResponseDto (wrapped inside
   AuthResponseDto/ApiResponseDto) back to frontend (RESPONSE, outgoing)
*/