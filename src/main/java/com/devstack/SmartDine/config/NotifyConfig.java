package com.devstack.SmartDine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(NotifyConfig.NotifyProperties.class)
public class NotifyConfig {

    @Bean
    public RestClient notifyRestClient(NotifyProperties properties){
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "notifylk")
    public static class NotifyProperties{
        private String userId;
        private String apiKey;
        private String senderId;
        private String baseUrl;
    }
}


/*
==========================================================
 NOTIFYCONFIG.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE NotifyConfig AT ALL
----------------------------------------------------------
Notify.lk is an external SMS-SENDING service (used for
things like OTP codes, order updates, delivery alerts sent
as text messages instead of email). SmartDine doesn't send
SMS itself - it delegates that job to Notify.lk. This file
tells Spring HOW to reach Notify.lk's API and WHAT
credentials to use - the same overall pattern as
KeycloakConfig (login) and BrevoConfig (email), just for
SMS this time.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
When Nimal places an order, SmartDine may want to text him:
"Your order #45 has been confirmed!" - instead of writing
raw SMS-sending logic, SmartDine calls Notify.lk's API, and
NotifyConfig sets up the connection to make that possible.
*/


/*
STEP 2 - @Configuration and @EnableConfigurationProperties(NotifyConfig.NotifyProperties.class)
----------------------------------------------------------
@Configuration tells Spring "this class has setup code to
run at startup." @EnableConfigurationProperties activates
NotifyProperties so its values load from
application.properties before the bean below uses them.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Same pattern as BrevoConfig and JwtConfig - Spring reads
application.properties once at startup and fills
NotifyProperties with real values, ready to be used when
notifyRestClient() builds its HTTP client.
*/


/*
STEP 3 - NotifyProperties + @ConfigurationProperties(prefix = "notifylk")
----------------------------------------------------------
Grabs any property starting with "notifylk." from
application.properties and maps it onto matching fields:
userId, apiKey, senderId, baseUrl.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
application.properties could contain:
notifylk.user-id=nim12345
notifylk.api-key=xyz987apikey
notifylk.sender-id=SmartDine
notifylk.base-url=https://app.notify.lk/api/v1

Spring automatically fills:
properties.getUserId()    -> "nim12345"
properties.getApiKey()    -> "xyz987apikey"
properties.getSenderId()  -> "SmartDine"
properties.getBaseUrl()   -> "https://app.notify.lk/api/v1"
*/


/*
STEP 4 - private String userId;
----------------------------------------------------------
Your account ID on Notify.lk's platform - identifies WHOSE
account is sending the SMS (like a username for the SMS
service).
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Every SMS SmartDine sends through Notify.lk is billed and
tracked under this specific userId - it's how Notify.lk
knows which customer account (SmartDine's) to charge and
report on.
*/


/*
STEP 5 - private String apiKey;
----------------------------------------------------------
A secret key that proves requests are genuinely coming from
SmartDine's authorized account - similar in purpose to
Brevo's apiKey or Keycloak's secretId.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Without a valid apiKey attached to the request, Notify.lk
would reject SmartDine's attempt to send an SMS to Nimal,
even if everything else (message text, phone number) was
correct - this key is what authorizes the request.
*/


/*
STEP 6 - private String senderId;
----------------------------------------------------------
The NAME that appears as the SENDER on Nimal's phone when
he receives the text message, instead of a random phone
number.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal receives a text message, and instead of it coming
from an unrecognizable number, his phone shows the sender
as: "SmartDine" - making it clearly recognizable and
trustworthy.
*/


/*
STEP 7 - private String baseUrl;
----------------------------------------------------------
The web address of Notify.lk's API that all requests get
sent to. Unlike Brevo (which hardcoded its base URL directly
in code), here it's loaded from application.properties
instead, making it easy to change without touching the
Java code itself.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
baseUrl = "https://app.notify.lk/api/v1"

Every SMS-sending request SmartDine makes starts at this
address, e.g. sending a POST request to
https://app.notify.lk/api/v1/send to actually deliver
Nimal's order confirmation text.
*/


/*
STEP 8 - @Bean notifyRestClient(NotifyProperties properties)
----------------------------------------------------------
Builds a ready-to-use HTTP client, pre-configured with
Notify.lk's base URL (read from properties, not hardcoded),
so anywhere else in your code you need to send an SMS, you
inject this ONE RestClient bean instead of rebuilding the
connection manually every time.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Later, in an SmsService.java, you could inject this
RestClient and call something like:
notifyRestClient.post()
    .uri("/send?user_id={id}&api_key={key}&sender_id={sender}&to={phone}&message={msg}",
         properties.getUserId(), properties.getApiKey(),
         properties.getSenderId(), nimalPhoneNumber, orderMessage)
    .retrieve();
to actually send Nimal his order confirmation SMS.

Note: unlike BrevoConfig, this class doesn't attach any
defaultHeader() calls - meaning userId/apiKey/senderId are
likely meant to be passed as URL parameters per-request
(as shown above) rather than as fixed headers on every call.
*/


/*
STEP 9 - PUTTING IT ALL TOGETHER (FULL FLOW)
----------------------------------------------------------
1. application.properties holds the real Notify.lk values
   (userId, apiKey, senderId, baseUrl)
2. NotifyProperties automatically loads all of them into
   one clean Java object at startup
3. The RestClient bean is built using
   properties.getBaseUrl() as its base URL
4. Nimal places an order on SmartDine
5. Your order logic uses the RestClient + userId/apiKey/
   senderId to call Notify.lk's API and send Nimal an SMS
6. Nimal's phone shows a text from "SmartDine" confirming
   his order
*/