package com.devstack.SmartDine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableConfigurationProperties(CorsConfig.CorsProperties.class)
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource(CorsProperties props){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(props.getAllowedOrigins());
        configuration.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "app.cors")
    public static class CorsProperties{
        private List<String> allowedOrigins = List.of("http://127.0.0.1:3000");
    }


    /*
==========================================================
 CORSCONFIG.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE CorsConfig AT ALL
----------------------------------------------------------
CORS (Cross-Origin Resource Sharing) is a browser security
rule that BLOCKS a frontend running on one address/port from
calling a backend running on a DIFFERENT address/port, unless
the backend explicitly says "this frontend is allowed to talk
to me." Without this config, your browser will silently
REJECT requests from your frontend to your backend, even if
the backend code itself is perfectly correct.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal's SmartDine frontend runs at http://127.0.0.1:3000
Your Spring Boot backend runs at http://localhost:8080

These are technically DIFFERENT "origins" (different ports),
so without CorsConfig, when the frontend tries to call
POST /api/auth/login, the BROWSER blocks the response before
it even reaches your frontend code, showing a CORS error in
the console - even though the backend actually worked fine.
*/


/*
STEP 2 - @Configuration and @EnableConfigurationProperties(CorsConfig.CorsProperties.class)
----------------------------------------------------------
@Configuration tells Spring "this class has setup code to
run at startup." @EnableConfigurationProperties activates
CorsProperties so its values get loaded from
application.properties before the bean below uses them.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Same pattern as BrevoConfig - Spring loads CorsProperties
FIRST, so when corsConfigurationSource() runs, props already
has real values ready to use (or its default, if nothing was
set in application.properties).
*/


/*
STEP 3 - CorsProperties + @ConfigurationProperties(prefix = "app.cors")
----------------------------------------------------------
Grabs any property starting with "app.cors." from
application.properties. Here there's only ONE field:
allowedOrigins, a LIST of frontend addresses that are
allowed to call this backend.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
application.properties could contain:
app.cors.allowed-origins=http://127.0.0.1:3000,https://smartdine.com

Spring automatically fills:
props.getAllowedOrigins() -> ["http://127.0.0.1:3000", "https://smartdine.com"]

If application.properties has NOTHING set for this,
it falls back to the DEFAULT already written in the code:
private List<String> allowedOrigins = List.of("http://127.0.0.1:3000");
*/


/*
STEP 4 - CorsConfiguration configuration = new CorsConfiguration();
----------------------------------------------------------
Creates an empty rulebook object that you then fill in,
line by line, with what's allowed and what isn't.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Think of this like a guest list template - blank at first,
then you write down who's invited, what they're allowed to
bring, etc, in the following lines.
*/


/*
STEP 5 - configuration.setAllowedOrigins(props.getAllowedOrigins());
----------------------------------------------------------
Sets WHICH frontend addresses are allowed to call this
backend at all, using the list loaded from CorsProperties.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Only requests coming FROM http://127.0.0.1:3000 (Nimal's
frontend during development) are allowed through. A request
from some random other website, like http://evil-site.com,
would be BLOCKED by the browser automatically.
*/


/*
STEP 6 - configuration.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
----------------------------------------------------------
Sets WHICH HTTP methods the frontend is allowed to use when
calling the backend.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal's frontend can:
GET    /api/menu           -> view the menu
POST   /api/orders         -> place a new order
PUT    /api/orders/5       -> update order 5 entirely
PATCH  /api/orders/5       -> partially update order 5
DELETE /api/orders/5       -> cancel order 5
OPTIONS                    -> a special "preflight" check
                              browsers send automatically
                              before certain real requests,
                              to ask "are you okay with this?"
*/


/*
STEP 7 - configuration.setAllowedHeaders(List.of("*"));
----------------------------------------------------------
Sets WHICH request headers the frontend is allowed to send.
The "*" wildcard means "allow ANY header."
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Nimal's frontend sends a request with headers like:
Authorization: Bearer <his-jwt-token>
Content-Type: application/json

Because allowedHeaders is "*", both of these (and any other
header) are accepted without needing to individually list
each one by name.
*/


/*
STEP 8 - configuration.setAllowCredentials(true);
----------------------------------------------------------
Allows the frontend to send credentials (like cookies, or
the Authorization header carrying Nimal's JWT token) along
with cross-origin requests.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
When Nimal's frontend calls GET /api/orders/my-orders and
attaches his JWT token in the Authorization header, this
setting is what allows that header to actually be sent
and accepted, rather than being stripped out by the browser.

Note: setAllowCredentials(true) combined with
setAllowedOrigins("*") is NOT allowed by browsers for
security reasons - this is why SmartDine lists SPECIFIC
origins (like http://127.0.0.1:3000) instead of using "*"
for allowedOrigins.
*/


/*
STEP 9 - UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
          source.registerCorsConfiguration("/**", configuration);
----------------------------------------------------------
Applies the entire rulebook you just built to EVERY endpoint
in the app. "/**" means "all paths, no matter how deep."
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Whether Nimal's frontend calls /api/auth/login,
/api/orders/my-orders, or /api/menu/pizza - ALL of them
follow the SAME CORS rules defined above, since "/**"
covers every possible path in the application.
*/


/*
STEP 10 - PUTTING IT ALL TOGETHER (FULL FLOW)
----------------------------------------------------------
1. application.properties (optionally) lists which frontend
   addresses are allowed - otherwise defaults to
   http://127.0.0.1:3000
2. CorsProperties loads that list into a Java object
3. corsConfigurationSource() builds the actual CORS rulebook:
   which origins, which methods, which headers, credentials
   allowed or not
4. That rulebook is applied to EVERY endpoint ("/**")
5. Nimal's frontend (http://127.0.0.1:3000) calls
   POST /api/auth/login with a JSON body
6. Because his frontend's origin is in the allowed list,
   the browser lets the request through instead of blocking
   it, and SmartDine's backend responds normally
*/

}