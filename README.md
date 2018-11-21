# spring-boot-oauth-demo01
 
# To run locally:

1. If you want to be able to test on localhost with https, configure the key store.  
   * This is a self-signed certificate, and does NOT offer security; it is only for testing purposes
   * Run this:
      ```
      cd src/main/resources
      keytool -genkey -alias mydomain -keyalg RSA -keystore KeyStore.jks -keysize 2048
      ```
   * Use `password` as the password (or else change the hardcoded value `password` in the file `src/main/resources/application.properties`
<<<<<<< Updated upstream
   * For all the other values, you can just take the defaults (except you have to answer "yes" to the question 
       where it asks you if the values are correct.)
   
2. Create a Github OAuth app to get the client-id and client-secret values, and put those values into `app.json`.  This
   guide may be of help, except that the callback url needs to be  `https://localhost:8082`
=======

2. Copy from `app.json.EXAMPLE` to `app.json` which is in the `.gitignore` file (or should be).
2. Create a Github OAuth app to get the client-id and client-secret values, and put those values into `app.json`
   * For details, see: <>
>>>>>>> Stashed changes
3. Run `. env.sh`
4. Run `mvn spring-boot:run`

Ignore the errors about MongoDB... there is something that is trying to start up that we need to disable...

# Based on code from:

* <https://github.com/pac4j/spring-webmvc-pac4j-boot-demo>
* <https://github.com/pconrad/try2-pac4j-sb-oauth>


# Configuring the KeyStore

<http://pconrad-webapps.github.io/topics/spring_boot_https/>

# Useful resources:

* <https://www.pac4j.org/docs/clients.html#3-the-callback-url>


The callback url should apparently be defined this way:

```
FacebookClient facebookClient = new FacebookClient(fbKey, fbSecret);
TwitterClient twitterClient = new TwitterClient(twKey, twSecret);
Config config = new Config("http://localhost:8080/callback", facebookClient, twitterClient);
```

> In that case, the callback URL of the FacebookClient is
> `http://localhost:8080/callback?client_name=FacebookClient` and the
> callback URL of the TwitterClient is
> `http://localhost:8080/callback?client_name=TwitterClient`.


# Original README contents:

<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-spring-webmvc.png" width="300" />
</p>

This `spring-webmvc-pac4j-boot-demo` project is a Spring Boot application secured by the [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j) security library with various authentication mechanisms: Facebook, Twitter, form, basic auth, CAS, SAML, OpenID Connect, JWT...

## Run and test

You can build the project and run it on [http://localhost:8080](http://localhost:8080) using the following commands:

    cd spring-webmvc-pac4j-boot-demo
    mvn clean compile exec:java

For your tests, click on the "Protected url by **xxx**" link to start the login process with the **xxx** identity provider...
