# spring-boot-oauth-demo01

This is a demo of an application that illustrates
OAuth with spring boot.

The configuration steps need to be followed VERY CAREFULLY
or it will not work.


# To run locally:

1. If you want to be able to test on localhost with https, configure the key store.  
   * This is a self-signed certificate, and does NOT offer security; it is only for testing purposes
   * When running with `https` with a self-signed certificate, you will
      likely get browser warnings that the site may be unsafe.  It is ok
      to proceed to the site in spite of these warnings.
   * Run this, noting how to respond to the prompts below:
      ```
      cd src/main/resources
      keytool -genkey -alias mydomain -keyalg RSA -keystore KeyStore.jks -keysize 2048
      ```
      * Use `password` as the password (or else change the hardcoded value `password` in the file `src/main/resources/application.properties`

      * For all the other values, you can just take the defaults (except you have to answer "yes" to the question where it asks you if the values are correct.)
   
2. Create a Github OAuth app to get the client-id and client-secret values, and put those values into `app.json`.  To create a Github OAuth app:
   * Login to Github, and go to Settings under your personal account
   * Navigate to Developer Settings (Or just go to: <https://github.com/settings/developers>)
   * Click the "New OAuth App" button
   * Give the app a name that matches your repo name, plus "test on localhost" (for example `GauchoTool test on localhost`).  It is not required that this name match exactly, but you will *want to be able to find it later* to be sure that you are debugging the settings of the correct app
   * For `Homepage URL` enter `https://127.0.0.1:8082`
   * For `Application description`, you may put in anything you want.
   * For `Authorization callback URL` you must put in this.  Be sure it matches *exactly*, including upper/lower case: `https://127.0.0.1:8082/callback?client_name=GitHubClient`
   * Click `Register Application`
   * Now you have the `Client ID` and `Client Secret` values you need for the next step.
3. Copy from `app.json.EXAMPLE` to `app.json` which is in the `.gitignore` file (or should be), and carefully edit the `Client ID` and `Client Secret` into the `app.json` version.
3. Run `. env.sh`
   * This defines the environment variable `SPRING_APPLICATION_JSON` which is
      an environment variable that can override application setting values
      in the `src/main/resources/application.properties` file.
4. Run `mvn spring-boot:run`

Ignore the errors about MongoDB... there is something that is trying to start up that we need to disable...

# Based on code from:

* <https://github.com/pac4j/spring-webmvc-pac4j-boot-demo>
* <https://github.com/pconrad/try2-pac4j-sb-oauth>


# Configuring the KeyStore

<http://pconrad-webapps.github.io/topics/spring_boot_https/>

# The Codes

## `CustomAuthorizer.java`

`CustomAuthorizer` is a class that extends `ProfileAuthorizer<CommonProfile>`.

See: [ProfileAuthorizer javadoc](http://static.javadoc.io/org.pac4j/pac4j-core/1.9.0/org/pac4j/core/authorization/authorizer/ProfileAuthorizer.html)



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
