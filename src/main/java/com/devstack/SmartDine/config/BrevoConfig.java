package com.devstack.SmartDine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(BrevoConfig.BrevoProperties.class)
public class BrevoConfig {
    private static final String BREVO_BASE_URL="https://api.brevo.com/v3";

    @Bean
    public RestClient brevoRestClient(BrevoProperties bravo){
        return RestClient.builder()
                .baseUrl(BREVO_BASE_URL)
                .defaultHeader("api-key", bravo.getApiKey())
                .defaultHeader("Content-Type","application/json")
                .defaultHeader("Accept","application/json")
                .build();
    }
    @Getter
    @Setter
    @ConfigurationProperties(prefix = "brevo")
    public static class BrevoProperties{
        private String apiKey;
        private Sender sender = new Sender();
        private Templates templates = new Templates();

        @Getter
        @Setter
        public static class Sender{
            private String email;
            private String name;
        }

        @Getter
        @Setter
        public static class Templates{
            private Long otp;
            private Long welcome;
            private Long resetPassword;
        }
    }
}


/*
==========================================================
 BREVOCONFIG.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE BrevoConfig AT ALL
----------------------------------------------------------
Brevo is an external EMAIL SENDING service (used for things
like OTP codes, welcome emails, password reset emails).
SmartDine doesn't send emails itself - it delegates that job
to Brevo. This file tells Spring HOW to reach Brevo's API
and WHAT settings/credentials to use when talking to it -
same overall idea as KeycloakConfig, just for a different
external service.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
When Nimal signs up on SmartDine, the app needs to email
him an OTP code to verify his account. Instead of writing
raw email-sending logic, SmartDine calls Brevo's API, and
BrevoConfig is what sets up the connection to make that
possible.
*/


/*
STEP 2 - private static final String BREVO_BASE_URL
----------------------------------------------------------
A fixed, hardcoded constant holding Brevo's API base URL.
Unlike Keycloak's URL (which can change per environment/
per developer machine), Brevo's API address is the SAME
for everyone, everywhere, so it's safe to hardcode directly
in the code rather than pulling it from application.properties.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
BREVO_BASE_URL = "https://api.brevo.com/v3"

Every request SmartDine sends to Brevo (send OTP, send
welcome email, etc.) starts with this same address,
whether it's running on your laptop or a live server.
*/


/*
STEP 3 - @Configuration and @EnableConfigurationProperties(BrevoConfig.BrevoProperties.class)
----------------------------------------------------------
@Configuration tells Spring "this class has setup code to
run at startup." @EnableConfigurationProperties here points
DIRECTLY at BrevoProperties.class, telling Spring exactly
which properties class to activate for this config file
(slightly different from KeycloakConfig, which left the
class unspecified, relying on component scanning to find it).
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
This ensures BrevoProperties gets loaded from
application.properties BEFORE the brevoRestClient() bean
tries to use it - order matters, since the RestClient
needs the API key already loaded to build itself correctly.
*/


/*
STEP 4 - BrevoProperties + @ConfigurationProperties(prefix = "brevo")
----------------------------------------------------------
Automatically grabs every property in application.properties
that starts with "brevo." and maps it onto matching fields -
apiKey, sender details, and template IDs - with no manual
parsing code needed.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
application.properties contains:
brevo.api-key=xkeysib-abc123...

Spring automatically fills:
bravo.getApiKey() -> "xkeysib-abc123..."
*/


/*
STEP 5 - NESTED Sender CLASS (email, name)
----------------------------------------------------------
Holds the "from" address and display name that emails
appear to come FROM when Brevo sends them on SmartDine's
behalf.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
application.properties contains:
brevo.sender.email=noreply@smartdine.com
brevo.sender.name=SmartDine

When Nimal receives his OTP email, his inbox shows it
coming from: "SmartDine <noreply@smartdine.com>"
*/


/*
STEP 6 - NESTED Templates CLASS (otp, welcome, resetPassword)
----------------------------------------------------------
Brevo lets you design email templates (layout, styling,
wording) directly on Brevo's own dashboard, and each
template gets a numeric ID. This class stores WHICH
template ID to use for each type of email SmartDine sends,
so your code can reference a template by NAME instead of
a random number scattered everywhere in your codebase.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
application.properties contains:
brevo.templates.otp=101
brevo.templates.welcome=102
brevo.templates.reset-password=103

When Nimal signs up, your code calls:
templates.getOtp() -> 101
telling Brevo "send email using template #101"
(the OTP-styled template), rather than hardcoding
the number 101 directly in your email-sending logic.
*/


/*
STEP 7 - @Bean brevoRestClient(BrevoProperties bravo)
----------------------------------------------------------
Builds a ready-to-use HTTP client, pre-configured with
Brevo's base URL AND the required headers Brevo expects
on every request - so anywhere else in your code you need
to send an email, you inject this ONE RestClient bean
instead of rebuilding headers/URL manually every time.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
.baseUrl(BREVO_BASE_URL)
   -> every call automatically starts at Brevo's API address

.defaultHeader("api-key", bravo.getApiKey())
   -> proves to Brevo that THIS request is from SmartDine's
      account (like a password for the API itself)

.defaultHeader("Content-Type","application/json")
   -> tells Brevo "the data I'm sending you is JSON format"

.defaultHeader("Accept","application/json")
   -> tells Brevo "send your response back to me in JSON too"

Later, in an EmailService.java, you could inject this
RestClient and call:
brevoRestClient.post()
    .uri("/smtp/email")
    .body(otpEmailPayload)
    .retrieve();
to actually send Nimal his OTP email.
*/


/*
STEP 8 - PUTTING IT ALL TOGETHER (FULL FLOW)
----------------------------------------------------------
1. application.properties holds Brevo's real values
   (api key, sender email/name, template IDs)
2. BrevoProperties automatically loads all of them into
   one clean Java object at startup
3. The RestClient bean is built with Brevo's fixed base URL
   plus the required headers (api-key, content-type, accept)
4. Nimal signs up on SmartDine
5. Your signup logic uses the RestClient + templates.getOtp()
   to send Nimal an OTP verification email through Brevo
6. Nimal receives the email from "SmartDine <noreply@...>"
   and enters the OTP to verify his account
*/