package com.devstack.SmartDine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}

/*
==========================================================
 REDISCONFIG.JAVA - STEP BY STEP EXPLANATION
==========================================================
*/

/*
STEP 1 - WHY CREATE RedisConfig AT ALL
----------------------------------------------------------
Redis is a separate, very FAST in-memory database, often
used to store short-lived, temporary data - things you don't
want cluttering up your main PostgreSQL database, and things
that need to be read/written extremely quickly. This file
sets up HOW SmartDine talks to Redis, so any other class
can simply inject a ready-to-use RedisTemplate instead of
configuring a fresh connection every time.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Remember OtpConfig from earlier - Nimal's OTP code needs to
be stored SOMEWHERE temporarily (for 5 minutes) so SmartDine
can check it when he enters it. Storing something this
short-lived in PostgreSQL would be wasteful - Redis is a
much better fit, since it's built exactly for fast,
temporary, expiring data like this.
*/


/*
STEP 2 - @Configuration
----------------------------------------------------------
Tells Spring "this class has setup code to run at startup" -
same role as in every other config file you've seen
(GatewayConfig, SecurityConfig, KeycloakConfig, etc).
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
At startup, Spring runs this class, builds the RedisTemplate
bean defined inside it, and makes it available for injection
anywhere else in the SmartDine project.
*/


/*
STEP 3 - @Bean public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory)
----------------------------------------------------------
Builds and returns a RedisTemplate - the main tool used to
actually SAVE and READ data from Redis. RedisConnectionFactory
is provided automatically by Spring Boot (it already knows
how to connect to your Redis server, using settings from
application.properties like the host and port).
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Elsewhere in the project, a service could inject this
RedisTemplate and use it like:
redisTemplate.opsForValue().set("otp:nimal@gmail.com", "483920");
to SAVE Nimal's OTP code into Redis, or:
redisTemplate.opsForValue().get("otp:nimal@gmail.com");
to READ it back later when he tries to verify.
*/


/*
STEP 4 - RedisTemplate<String,Object> template = new RedisTemplate<>();
          template.setConnectionFactory(factory);
----------------------------------------------------------
Creates a new, empty RedisTemplate object, then connects it
to your actual Redis server using the factory Spring provided.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Think of this like plugging a phone (the template) into a
phone line (the factory/connection) - without this step,
the template would exist as an object but have no actual
way to reach the real Redis server running elsewhere.
*/


/*
STEP 5 - template.setKeySerializer(new StringRedisSerializer());
          template.setValueSerializer(new StringRedisSerializer());
----------------------------------------------------------
Tells Redis HOW to convert your Java data into a format it
can store, and back again. Serializer = a translator between
"Java objects" and "raw bytes/text" that Redis actually
stores internally.
*/

/*
REAL EXAMPLE:
----------------------------------------------------------
Without a serializer, Redis might store keys/values as
strange, hard-to-read binary data (Java's default
serialization format). StringRedisSerializer makes sure
that when you save:
redisTemplate.opsForValue().set("otp:nimal@gmail.com", "483920");

Both the KEY ("otp:nimal@gmail.com") and the VALUE
("483920") are stored as plain, readable TEXT in Redis -
which is much easier to inspect, debug, and reason about
than raw binary data.
*/


/*
STEP 6 - PUTTING IT ALL TOGETHER (FULL FLOW)
----------------------------------------------------------
1. Spring Boot automatically creates a RedisConnectionFactory
   at startup, using Redis connection details from
   application.properties (host, port, etc)
2. RedisConfig uses that factory to build a fully working
   RedisTemplate<String,Object> bean
3. Nimal signs up on SmartDine and receives a 6-digit OTP
4. An OtpService injects this RedisTemplate and saves the
   OTP temporarily:
   redisTemplate.opsForValue().set("otp:nimal@gmail.com", "483920");
5. A few minutes later, Nimal enters his OTP to verify
6. The same service reads the stored value back from Redis
   and compares it against what Nimal typed, to confirm
   whether it matches (and whether it's still within the
   expiry time set earlier in OtpConfig)
*/