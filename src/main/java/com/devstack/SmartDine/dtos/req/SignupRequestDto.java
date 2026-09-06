package com.devstack.SmartDine.dtos.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SignupRequestDto {
    @NotBlank(message = "FirstName is required")
    @Size(max=100, message = "first name must not exceed 100 chars")
    private String firstName;

    @NotBlank(message = "LastName is required")
    @Size(max=100, message = "last name must not exceed 100 chars")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "Number is required")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "ConfirmPassword is required")
    private String confirmPassword;

}

/*
==========================================================
 SIGNUPREQUESTDTO.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE A DTO AT ALL
----------------------------------------------------------
DTO = Data Transfer Object. This class is NOT a database
table (no @Entity here) - it's just a container that holds
exactly the fields expected in a SIGNUP request coming from
the frontend, plus VALIDATION rules attached directly to
each field. It's kept SEPARATE from User.java (the entity)
because a signup request has different needs than the
database - e.g. confirmPassword only matters during signup,
it's never actually stored in the database.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
When Nimal fills out the SmartDine signup form and hits
submit, his frontend sends a JSON body like:
{
  "firstName": "Nimal",
  "lastName": "Perera",
  "email": "nimal@gmail.com",
  "phoneNumber": "+94771234567",
  "password": "mypassword123",
  "confirmPassword": "mypassword123"
}

Spring automatically converts this JSON into a
SignupRequestDto object in your controller - this class is
the BLUEPRINT for what that incoming JSON must look like.
*/


/*
STEP 2 - THE "dtos.req" PACKAGE/FOLDER
----------------------------------------------------------
Kept in its own dtos.req package (request DTOs), separate
from entity classes and separate from any "response" DTOs
(what you send BACK to the frontend) - keeping incoming vs
outgoing data shapes clearly organized as the project grows.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
SignupRequestDto = what comes IN from the frontend during
signup. Later, you might have a UserResponseDto = what goes
OUT to the frontend after signup succeeds (probably NOT
including password/confirmPassword at all, for security).
*/


/*
STEP 3 - @Data
----------------------------------------------------------
A Lombok shortcut that automatically generates getters,
setters, toString(), equals(), and hashCode() all at once -
more compact than writing @Getter and @Setter separately
like in your entity classes.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Without @Data, you'd manually write:
getFirstName(), setFirstName(String firstName),
getEmail(), setEmail(String email), and so on for every
single field - @Data generates all of this instantly.
*/


/*
STEP 4 - @NoArgsConstructor, @AllArgsConstructor, @Builder
----------------------------------------------------------
Same meaning as in User.java earlier:
@NoArgsConstructor  -> lets you write: new SignupRequestDto()
@AllArgsConstructor -> lets you write all fields at once
@Builder            -> lets you build it field by field
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Spring itself typically uses the no-args constructor plus
setters automatically when converting incoming JSON into
this object - you rarely build this one manually with
.builder(), but it's included in case you ever need to
construct a test object manually, e.g. in a unit test.
*/


/*
STEP 5 - @NotBlank(message = "...")
----------------------------------------------------------
Validation rule: this field CANNOT be empty, null, or just
whitespace. If it fails, Spring automatically returns the
custom message you wrote, without you writing any manual
if-checks yourself.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
If Nimal's frontend accidentally sends an empty firstName
(like ""), Spring rejects the request automatically with
the response: "FirstName is required" - your controller
code never even runs, since validation fails before it gets
there.
*/


/*
STEP 6 - @Size(max = 100, message = "...")
----------------------------------------------------------
Validation rule: this field's length cannot exceed the
given number of characters.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
If someone tried to submit a firstName that's 150 characters
long (unrealistic, but possible in a malicious or buggy
request), Spring rejects it with: "first name must not
exceed 100 chars" - protecting your database column
(which is also capped at VARCHAR(100) in User.java) from
ever receiving oversized data.
*/


/*
STEP 7 - @Email(message = "...")
----------------------------------------------------------
Validation rule: checks that the value LOOKS like a real
email address (has an @, a domain, etc). This is the exact
validation that was MISSING from User.java earlier - here,
it's properly applied at the point data first enters the
system.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
If Nimal typos his email as "nimalgmail.com" (missing the
@), Spring rejects the signup attempt immediately with:
"Email must be a valid email address" - before it ever
reaches your controller or gets anywhere near the database.
*/


/*
STEP 8 - phoneNumber, password, confirmPassword (all @NotBlank only)
----------------------------------------------------------
These three fields only check that they're not empty -
no format validation (like checking password strength, or
phone number format) is applied here yet.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal could currently submit password = "1" (just one
character) and it would PASS validation, since @NotBlank
only checks it isn't empty - it says nothing about minimum
length or complexity. If stronger password rules are wanted
later, a @Size(min = 8) or a custom validator would need to
be added here.
*/


/*
STEP 9 - confirmPassword - WHY IT EXISTS, AND A GAP TO NOTICE
----------------------------------------------------------
confirmPassword exists purely to catch TYPOS - making sure
Nimal typed his intended password the same way twice before
submitting, a common UX safety net during signup.

IMPORTANT GAP: this class only checks that confirmPassword
is NOT BLANK - it does NOT actually check that password and
confirmPassword MATCH each other. That comparison logic
needs to be written separately, either with a custom
validator annotation, or manually inside your service/
controller layer (e.g. "if (!password.equals(confirmPassword))
throw an error").
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Right now, Nimal could submit:
password = "mypassword123"
confirmPassword = "somethingcompletelydifferent"

and THIS DTO'S validation alone would still PASS, since both
fields are simply "not blank." The actual matching check
must be added elsewhere in your signup logic.
*/


/*
STEP 10 - PUTTING IT ALL TOGETHER (FULL FLOW)
----------------------------------------------------------
1. Nimal fills the signup form on the frontend and submits
2. His frontend sends a JSON body matching this DTO's shape
3. Spring automatically converts that JSON into a
   SignupRequestDto object
4. Before your controller code even runs, Spring checks
   every @NotBlank, @Size, and @Email rule on this object
5. If ANY rule fails, Spring immediately responds with the
   relevant message(s) - your signup logic never executes
6. If ALL rules pass, your controller/service receives a
   clean, validated SignupRequestDto and can safely proceed
   to check password/confirmPassword match, hash the
   password, and create Nimal's account
*/
