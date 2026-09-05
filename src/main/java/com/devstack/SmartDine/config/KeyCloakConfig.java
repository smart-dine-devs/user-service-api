package com.devstack.SmartDine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties

public class KeyCloakConfig {
      @Bean
    public RestClient KeyCloakRestClient(KeyCloakProperties properties){
          return RestClient.builder()
                  .baseUrl(properties.getServerUrl())
                  .build();
      }

      @Getter
      @Setter
      @ConfigurationProperties(prefix = "keycloak")
      public static class KeyCloakProperties {
          private String serverUrl;
          private String realm;
          private String clientId;
          private String secretId;
          private Admin admin = new Admin();

          @Getter
          @Setter
          public static class Admin {
              private String username;
              private String password;
          }

      }
}

/*
==========================================================
 KEYCLOAKCONFIG.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY A "config" FOLDER
----------------------------------------------------------
Config classes hold setup code for talking to external
tools/systems (like GatewayConfig for routing, SecurityConfig
for security rules). Keeping them in their own "config"
package separates SETUP code from entities, controllers,
and repositories, so the project stays organized as it grows.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
GatewayConfig.java, SecurityConfig.java, and KeycloakConfig.java
all sit together inside com.devstack.SmartDine.config -
each one configures a DIFFERENT external concern (routing,
security rules, Keycloak connection).
*/


/*
STEP 2 - WHY CREATE KeycloakConfig AT ALL
----------------------------------------------------------
SmartDine doesn't handle logins itself - it delegates that
job to Keycloak, an external identity/login server. This
file tells Spring HOW to reach Keycloak and WHAT settings
to use when talking to it.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
When Nimal signs up on SmartDine, the app doesn't store
his password itself - it needs to talk to Keycloak (running
on its own server) to create and manage his login. This
config file is what makes that connection possible.
*/


/*
STEP 3 - @Configuration and @EnableConfigurationProperties
----------------------------------------------------------
@Configuration tells Spring "this class has setup code to
run at startup." @EnableConfigurationProperties turns on
support for reading settings from application.properties
directly into Java objects, instead of manually calling
environment.getProperty(...) everywhere in your code.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Without this, you'd write repetitive code like:
String url = environment.getProperty("keycloak.server-url");
everywhere you needed it. With it, Spring loads the value
once into a clean Java object automatically at startup.
*/


/*
STEP 4 - KeyCloakProperties + @ConfigurationProperties(prefix = "keycloak")
----------------------------------------------------------
This nested class automatically grabs every property in
application.properties that starts with "keycloak." and
maps it onto matching fields - serverUrl, realm, clientId,
secretId - with no manual parsing code needed.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
application.properties contains:
keycloak.server-url=http://localhost:8080
keycloak.realm=smartdine
keycloak.client-id=smartdine-app
keycloak.secret-id=abc123secret

Spring automatically fills:
properties.getServerUrl()  -> "http://localhost:8080"
properties.getRealm()      -> "smartdine"
properties.getClientId()   -> "smartdine-app"
properties.getSecretId()   -> "abc123secret"
*/


/*
STEP 5 - NESTED Admin CLASS (username, password)
----------------------------------------------------------
Keycloak needs SEPARATE admin credentials (different from
a normal user's login) so your backend can call Keycloak's
ADMIN API directly - for example, to create new user
accounts inside Keycloak programmatically, rather than
making users register through Keycloak's own UI.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
application.properties contains:
keycloak.admin.username=admin
keycloak.admin.password=adminpass123

When Nimal signs up on the SmartDine app, your signup
controller uses THESE admin credentials
(properties.getAdmin().getUsername() / getPassword())
to call Keycloak's admin API and create Nimal's account
there directly, behind the scenes - Nimal never sees
Keycloak's interface himself.
*/


/*
STEP 6 - @Bean KeyCloakRestClient(KeyCloakProperties properties)
----------------------------------------------------------
Builds a ready-to-use HTTP client, pre-configured with
Keycloak's base URL, so anywhere else in your code you
need to talk to Keycloak, you inject this ONE RestClient
bean instead of rebuilding a fresh connection with the
URL hardcoded every time.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Later, in a KeycloakService.java, you could inject this
RestClient and call:
restClient.post()
    .uri("/admin/realms/smartdine/users")
    .body(nimalUserPayload)
    .retrieve();

to actually create Nimal's account inside Keycloak,
using the base URL already configured by this bean.
*/


/*
STEP 7 - PUTTING IT ALL TOGETHER (FULL FLOW)
----------------------------------------------------------
1. application.properties holds the real Keycloak values
   (server URL, realm, client ID, secret, admin credentials)
2. KeyCloakProperties automatically loads all of them into
   one clean Java object at startup
3. The RestClient bean is built using
   properties.getServerUrl() as its base URL
4. Nimal signs up on SmartDine
5. Your signup logic uses the RestClient + admin credentials
   to call Keycloak's admin API and create his account there
6. Nimal can now log in through Keycloak, and SmartDine's
   SecurityConfig later validates his JWT token from there
*/
