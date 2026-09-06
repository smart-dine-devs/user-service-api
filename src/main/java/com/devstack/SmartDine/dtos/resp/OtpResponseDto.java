package com.devstack.SmartDine.dtos.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpResponseDto {

    private boolean success;
    private String message;
    private String email;

}


/*
==========================================================
 OTPRESPONSEDTO.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE OtpResponseDto AT ALL
WHY: confirms OTP action result
----------------------------------------------------------
After Nimal requests or verifies an OTP, the frontend needs
a small, focused response confirming what happened - did it
succeed, what's the message, and WHICH email this OTP action
was for. This is a lightweight, purpose-specific response,
much simpler than AuthResponseDto since OTP actions don't
need to return tokens or full user profiles.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal requests an OTP to verify his new account. Backend
responds with THIS small object, confirming the OTP was
sent, and to which email - not his whole profile, not any
tokens, just the essentials for this ONE specific action.
*/


/*
STEP 2 - private boolean success;
WHY: tells if OTP action worked
----------------------------------------------------------
Same role as "success" in ApiResponseDto - a simple flag
telling the frontend whether this specific OTP request or
verification worked.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal requests a new OTP successfully:
success = true

Nimal tries to verify an OTP that's already expired:
success = false
*/


/*
STEP 3 - private String message;
WHY: explains OTP result plainly
----------------------------------------------------------
A readable explanation specific to the OTP action, shown
directly to Nimal on the verification screen.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
success = true,  message = "OTP sent successfully"
success = false, message = "OTP has expired, please request a new one"
*/


/*
STEP 4 - private String email;
WHY: shows which email OTP sent
----------------------------------------------------------
Confirms EXACTLY which email address this OTP action relates
to - useful so the frontend can display it back to the user
for confirmation (e.g. "check your inbox at ___").
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal requests an OTP using nimal@gmail.com.
Response includes: email = "nimal@gmail.com"

Frontend shows: "We've sent a code to nimal@gmail.com" -
reassuring Nimal he entered the right address, without the
backend needing to send back his entire profile just to
confirm this one detail.
*/


/*
STEP 5 - PUTTING IT ALL TOGETHER (FULL FLOW)
WHY: shows the complete OTP flow
----------------------------------------------------------
1. Nimal submits his email requesting an OTP (using
   ForgotPasswordRequestDto or similar, from earlier)
2. Backend generates and sends the OTP (via NotifyConfig/
   BrevoConfig), and stores it temporarily in Redis
3. Backend responds with an OtpResponseDto:
   { success: true, message: "OTP sent successfully", email: "nimal@gmail.com" }
4. Nimal enters the code he received
5. Backend checks it against Redis, and responds again with
   an OtpResponseDto reflecting whether verification
   succeeded or failed
*/