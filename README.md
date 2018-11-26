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


# Based on code from:

* <https://github.com/pac4j/spring-webmvc-pac4j-boot-demo>
* <https://github.com/pconrad/try2-pac4j-sb-oauth>


# Configuring the KeyStore

<http://pconrad-webapps.github.io/topics/spring_boot_https/>

# The Codes

## `CustomAdmin.java`

This class has one job, which is to define a method `isAdmin` that
determines, based on a user's profile whether we should consider them
an `admin` or not.   The code here looks at whether the user has the `admin`
role in a particular Github Organization that we've hard coded
(later, we should make this come from a configuration variable).

## `CustomAuthorizer.java`

`CustomAuthorizer` is a class that extends `ProfileAuthorizer<CommonProfile>`, which is an abstract class.

The single abstract method of that class is:  

```
   protected abstract boolean
   isProfileAuthorized(WebContext context, U profile)
```

which is supposed to return true or false based on "whether a specific profile is authorized".

In this context, `CommonProfile` means the information associated with
a given user that we have authenticated; and the `CommonProfile` is
information we can get about a user regardless of which login method
that used (i.e. whether its Github, or Facebook, or Google, or
whatever.)

See:
* [`ProfileAuthorizer` javadoc](http://static.javadoc.io/org.pac4j/pac4j-core/1.9.0/org/pac4j/core/authorization/authorizer/ProfileAuthorizer.html)
* [`CommonProfile` javadoc](http://static.javadoc.io/org.pac4j/pac4j-core/1.9.0/org/pac4j/core/profile/CommonProfile.html)

One way we can define who is authorized is to check for a specific
username, or list of usernames that we could hard code.  Another way
is to base it on whether a user is an "admin", and we can define who is
an admin in a separate file, namely: xxx.

## `EmbeddedTomcat.java`


This file allows the configuration
of the "Embedded Tomcat" container (a Java Servlet Container) that is
part of the Spring framework.   The "Servlet Container" is the piece
of software that acts as the webserver when you run a Java http backend.

To be perfectly honest: I've forgotten why this file was needed for
this particular demo.
It may be the case that this is needed when we are running `https` on
localhost.   It isn't clear whether it's needed when we run on Heroku,
or even if its needed at all.  More research on that would be helpful.

## `MyErrorController.java`

This file simply allows us to customize the pages that show up when various kinds of errors occur, such as:

* `401 Unauthorized`
* `403 Forbidden`
* `404 Not Found`
* `500 Internal Server Error`

There are many other errors, and it would not be reasonable to create
custom pages for each one.  (See, for example: <https://www.restapitutorial.com/httpstatuscodes.html>.   We may not even need that here, and it might
be better to remove this from the tutorial to simplify it.  Maybe that goes
better in its own tutorial.

## `Pac4jConfig.java`

This file does a lot of work; it probably violates the Single Responsibility Principle.    Here are a few things it does:

* It gets the values of the `github_client_id and `github_client_secret` and
   sets up the `GitHubClient` object used for OAuth Authentication.
* It sets up the scope for the GitHubClient.
* It sets up two custom roles, `admin` and `custom`
* It sets up a custom function to decide what it means to be an `admin` user
   which we've defined as "being an admin user in the `ucsb-cs56-f18` github
   organization" (currently hard coded--that should be refactored)


## `SecurityConfig.java`

In this file, we configure the url patterns that need to be authenticated
before they may be accessed.  For example, in the method
`addInterceptors`, we find this code:


```
SecurityIntercepter gh_admin =
	new SecurityInterceptor(config, "GitHubClient", "admin");	    
registry.addInterceptor(gh_admin).addPathPatterns("/admin/*");
```

This sets up a security intercepter that requires us to be logged
into github in an admin role.  What "admin" role is defined in
Pac4JConfig.java (which may not be the best place for it, as
we've discussed.)

Similarly, we have a custom role defined and we can restrict
certain URL patterns to that role as well, and finally restrict
some pages to only be available to when users are logged in:

```
SecurityIntercepter gh_custom =
	new SecurityInterceptor(config, "GitHubClient", "custom");	    
registry.addInterceptor(gh_admin).addPathPatterns("/custom/*");

SecurityIntercepter gh_loggedIn =
	new SecurityInterceptor(config, "GitHubClient");	    
registry.addInterceptor(gh_admin).addPathPatterns("/github/*");
		
```

This file extends `WebMvcConfigurerAdapter`
* [`WebMvcConfigurerAdapter` javadoc](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/config/annotation/WebMvcConfigurerAdapter.html)
* import statement:
   ```
   import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
   ```

## `SpringBootPac4jDemo.java`

tbd

## `UserInterfaceApplication.java`

tbd

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
