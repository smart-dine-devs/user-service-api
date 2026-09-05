package com.devstack.SmartDine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtConfig.JwtProperties.class)
public class JwtConfig {
    @Getter
    @Setter
    @ConfigurationProperties(prefix = "jwt")
    public static class JwtProperties{
        private String secret;
        private long expirationMs=36000000L;
        private long refreshExpirationMs=72000000L;
    }

}


/*
==========================================================
 JWTCONFIG.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE JwtConfig AT ALL
----------------------------------------------------------
JWT tokens need certain SETTINGS to be created and checked
correctly - a SECRET key (used to sign/verify tokens so
nobody can fake one) and EXPIRATION TIMES (how long a token
stays valid before it's rejected). Rather than hardcoding
these values directly inside your login/token code, this
file loads them from application.properties into one clean,
reusable Java object.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
When Nimal logs in, SmartDine needs to generate a JWT token
for him. The code that creates that token needs to know:
"what secret key do I sign this with?" and "how long until
this token expires?" - JwtConfig is where those answers
come from.
*/


/*
STEP 2 - @Configuration and @EnableConfigurationProperties(JwtConfig.JwtProperties.class)
----------------------------------------------------------
@Configuration tells Spring "this class has setup code to
run at startup." @EnableConfigurationProperties activates
JwtProperties so its values get loaded from
application.properties automatically.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Same pattern as BrevoConfig and CorsConfig - Spring reads
application.properties once at startup and fills
JwtProperties with real values, ready to be injected
wherever needed (e.g. in a JwtService.java that actually
builds tokens).
*/


/*
STEP 3 - JwtProperties + @ConfigurationProperties(prefix = "jwt")
----------------------------------------------------------
Grabs any property starting with "jwt." from
application.properties and maps it onto matching fields:
secret, expirationMs, refreshExpirationMs.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
application.properties could contain:
jwt.secret=my-super-secret-signing-key
jwt.expiration-ms=1800000
jwt.refresh-expiration-ms=604800000

Spring automatically fills:
jwtProperties.getSecret()             -> "my-super-secret-signing-key"
jwtProperties.getExpirationMs()       -> 1800000
jwtProperties.getRefreshExpirationMs() -> 604800000
*/


/*
STEP 4 - private String secret;
----------------------------------------------------------
The SECRET KEY used to SIGN and VERIFY JWT tokens. This is
like a password only your backend knows - it proves a token
was genuinely created by SmartDine's server and hasn't been
tampered with.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
When Nimal logs in, the server signs his token using this
secret. Later, when Nimal sends that token back with a
request (like GET /api/orders/my-orders), the server checks
the signature using the SAME secret - if someone tried to
fake a token without knowing this secret, the signature check
would fail and the request would be rejected.

IMPORTANT: this value should be long, random, and kept OUT
of version control (e.g. in an environment variable), never
hardcoded or committed to GitHub directly.
*/


/*
STEP 5 - private long expirationMs = 36000000L;
----------------------------------------------------------
How long (in MILLISECONDS) a normal access token stays
valid before it expires and Nimal must log in again or
use a refresh token to get a new one.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
36000000 ms = 36,000 seconds = 10 hours

Nimal logs in at 9 AM. His access token stays valid until
roughly 7 PM that same day. After that, requests using
this expired token get rejected, even if the token was
otherwise perfectly valid.
*/


/*
STEP 6 - private long refreshExpirationMs = 72000000L;
----------------------------------------------------------
How long a REFRESH token stays valid. A refresh token is a
longer-lived, separate token whose only job is to get Nimal
a brand NEW access token without forcing him to re-enter his
password.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
72000000 ms = 72,000 seconds = 20 hours

Nimal's 10-hour access token expires at 7 PM. Instead of
logging in again from scratch, his frontend app quietly
uses his still-valid refresh token to get a NEW 10-hour
access token, as long as it's within 20 hours of his
original login - keeping him logged in smoothly without
repeated password prompts.
*/


/*
STEP 7 - PUTTING IT ALL TOGETHER (FULL FLOW)
----------------------------------------------------------
1. application.properties holds the real secret and
   expiration times for JWT tokens
2. JwtProperties automatically loads all of them into
   one clean Java object at startup
3. Nimal logs in successfully
4. A JwtService (elsewhere in the project) uses
   jwtProperties.getSecret() to SIGN a new access token,
   and jwtProperties.getExpirationMs() to set how long
   it lasts
5. A separate, longer-lived refresh token is also created,
   using getRefreshExpirationMs()
6. Nimal's frontend stores both tokens and uses the access
   token for normal requests, quietly refreshing it using
   the refresh token once it expires - without Nimal ever
   needing to log in again mid-session
*/