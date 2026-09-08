package com.devstack.SmartDine.exceptions;

import org.springframework.http.HttpStatus;

public class KeyCloakIntegrationException extends BaseException {

    public KeyCloakIntegrationException(String detail) {
        super("Keycloak integration error: " + detail, HttpStatus.BAD_GATEWAY, "KEYCLOAK_ERROR");
    }
}



/*
==========================================================
 KEYCLOAKINTEGRATIONEXCEPTION.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE KeyCloakIntegrationException AT ALL
WHY: represents Keycloak communication failure
----------------------------------------------------------
Connects directly back to KeycloakConfig.java from earlier.
This exception represents a DIFFERENT KIND of problem than
the others so far - not something Nimal did wrong (like a
bad OTP or duplicate email), but something going wrong on
SmartDine's END while trying to talk to the EXTERNAL
Keycloak server itself (e.g. Keycloak is down, unreachable,
or returns an unexpected error).
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal signs up correctly, with a valid, unique email. Your
backend then tries to call Keycloak's admin API (using the
RestClient bean built in KeycloakConfig.java) to actually
create his account there - but Keycloak's server happens to
be down for maintenance at that exact moment, or rejects the
request for some unexpected reason. This exception represents
THAT failure, not a mistake Nimal made.
*/


/*
STEP 2 - public KeyCloakIntegrationException(String detail)
WHY: carries technical failure info
----------------------------------------------------------
Accepts a "detail" String - typically the raw error message
or reason returned by Keycloak itself (or caught from a
failed HTTP call), so developers debugging this later can
see exactly WHAT went wrong on Keycloak's side.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Somewhere in your KeycloakService (the class that actually
calls Keycloak's API):
try {
    restClient.post().uri("/admin/realms/smartdine/users")...
} catch (Exception e) {
    throw new KeyCloakIntegrationException(e.getMessage());
}

If Keycloak's server was unreachable, "detail" might contain
something like "Connection refused" or "503 Service
Unavailable" - the actual underlying technical reason.
*/


/*
STEP 3 - super("Keycloak integration error: " + detail, HttpStatus.BAD_GATEWAY, "KEYCLOAK_ERROR");
WHY: fills in BaseException's fields
----------------------------------------------------------
message   -> "Keycloak integration error: Connection refused"
status    -> HttpStatus.BAD_GATEWAY (502 - "I tried to reach
             ANOTHER server on your behalf, and THAT server
             failed/gave a bad response" - a very fitting
             status specifically for external service
             failures, different from the 400/401/403/409
             statuses used in the other exceptions so far)
errorCode -> "KEYCLOAK_ERROR"
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Frontend receives:
{ status: 502, errorCode: "KEYCLOAK_ERROR",
  message: "Keycloak integration error: Connection refused" }

Rather than showing Nimal the raw technical detail (which
would be confusing and unhelpful to him), the frontend would
typically show something generic and reassuring instead,
like: "Something went wrong on our end - please try again in
a few minutes," while the DETAILED message stays useful for
YOUR team's logs/monitoring tools.
*/


/*
STEP 4 - WHY THIS ONE IS DIFFERENT FROM THE OTHERS
WHY: user's fault vs system's fault
----------------------------------------------------------
Every exception explained so far (AccountSuspendedException,
DuplicateEmailException, EmailNotVerifiedException,
InvalidOtpException) represents something the USER did
(wrong password, unverified account, wrong OTP, duplicate
signup). KeyCloakIntegrationException instead represents a
SYSTEM/INFRASTRUCTURE problem - Keycloak itself failing to
respond correctly. This distinction is exactly why its status
(502 BAD_GATEWAY) is different from all the earlier ones
(400/401/403/409) - 502 specifically communicates "the
problem isn't your input, it's US failing to reach something
we depend on."
*/


/*
STEP 5 - PUTTING IT ALL TOGETHER (FULL FLOW)
WHY: shows the complete usage flow
----------------------------------------------------------
1. Nimal submits a perfectly valid signup form
2. Backend tries calling Keycloak's admin API to create his
   account there
3. Keycloak is temporarily down/unreachable
4. Your KeycloakService catches this failure and throws:
   throw new KeyCloakIntegrationException("Connection refused");
5. This carries message, status (502), and errorCode
   ("KEYCLOAK_ERROR") - inherited from BaseException
6. A global exception handler catches it, logs the detailed
   technical message for your team, and sends the frontend a
   safer, generic-facing error via ErrorResponse
7. Nimal sees: "Something went wrong - please try again
   shortly," while your team can dig into the actual
   Keycloak failure using the detailed message in your logs
*/