package com.devstack.SmartDine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OtpConfig.OtpProperties.class)
public class OtpConfig {

    @Getter
    @Setter@ConfigurationProperties(prefix = "otp")
    public static class OtpProperties{
        private int expiryMinutes=5;
        private int length=6;
        private int maxAttempts=3;
    }
}



/*
==========================================================
 OTPCONFIG.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE OtpConfig AT ALL
----------------------------------------------------------
OTP (One-Time Password) codes need certain RULES to behave
consistently across the app - how long a code stays valid,
how many digits it has, and how many wrong tries are allowed
before blocking further attempts. Rather than hardcoding
these numbers directly inside your verification code, this
file loads them from application.properties into one clean,
reusable Java object, so they're easy to find and change
in one place.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
When Nimal signs up, SmartDine sends him a 6-digit OTP code
by SMS or email (using NotifyConfig or BrevoConfig from
earlier). The code that GENERATES and CHECKS that OTP needs
to know: how many digits should it have, how long is it
valid, and how many wrong guesses is Nimal allowed - OtpConfig
is where those answers come from.

Notice this file is SIMPLER than the others (no @Bean method)
- it only holds SETTINGS, with no external service (like
Brevo or Notify.lk) to connect to directly. It's purely
configuration values used by OTHER classes elsewhere in the
project (like an OtpService.java).
*/


/*
STEP 2 - @Configuration and @EnableConfigurationProperties(OtpConfig.OtpProperties.class)
----------------------------------------------------------
@Configuration tells Spring "this class has setup code to
run at startup." @EnableConfigurationProperties activates
OtpProperties so its values load from application.properties
automatically at startup.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Same pattern as JwtConfig and NotifyConfig - Spring reads
application.properties once and fills OtpProperties with
real values (or falls back to the defaults already written
in the code, if nothing is set).
*/


/*
STEP 3 - OtpProperties + @ConfigurationProperties(prefix = "otp")
----------------------------------------------------------
Grabs any property starting with "otp." from
application.properties and maps it onto matching fields:
expiryMinutes, length, maxAttempts.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
application.properties could contain:
otp.expiry-minutes=10
otp.length=4
otp.max-attempts=5

Spring automatically fills:
otpProperties.getExpiryMinutes()  -> 10
otpProperties.getLength()          -> 4
otpProperties.getMaxAttempts()     -> 5

If nothing is set in application.properties, the DEFAULTS
already written directly in the code are used instead
(5 minutes, 6 digits, 3 attempts).
*/


/*
STEP 4 - private int expiryMinutes = 5;
----------------------------------------------------------
How long (in MINUTES) an OTP code stays valid before it
expires and a new one must be requested.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal receives his OTP at 10:00 AM. With the default value
of 5, that code only works until 10:05 AM. If he tries to
enter it at 10:07 AM, SmartDine rejects it as expired, even
if he typed the correct digits.
*/


/*
STEP 5 - private int length = 6;
----------------------------------------------------------
How many DIGITS the generated OTP code has.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
With length = 6, Nimal might receive a code like: 483920
(6 digits total). If this were changed to 4, he'd instead
receive something like: 4839 (shorter, less secure, but
quicker to type).
*/


/*
STEP 6 - private int maxAttempts = 3;
----------------------------------------------------------
How many WRONG guesses are allowed before SmartDine blocks
further attempts for that OTP, forcing the user to request
a brand new code.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal receives OTP 483920, but mistypes it twice by
accident. On his THIRD wrong attempt (maxAttempts = 3),
SmartDine locks further tries with this same code -
he must request a new OTP to try again, preventing someone
from endlessly guessing random codes.
*/


/*
STEP 7 - PUTTING IT ALL TOGETHER (FULL FLOW)
----------------------------------------------------------
1. application.properties holds the real OTP rules (or
   nothing, falling back to the defaults: 5 min, 6 digits,
   3 attempts)
2. OtpProperties automatically loads these into one clean
   Java object at startup
3. Nimal signs up on SmartDine
4. An OtpService (elsewhere in the project) uses
   otpProperties.getLength() to generate a 6-digit code,
   sends it via NotifyConfig/BrevoConfig, and remembers
   when it expires using getExpiryMinutes()
5. Nimal enters the code on the verification screen
6. If he mistypes it, the service tracks his attempt count
   against getMaxAttempts() - blocking him after too many
   wrong tries, until he requests a fresh OTP
*/