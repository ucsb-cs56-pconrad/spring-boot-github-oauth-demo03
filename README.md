# <https://github.com/pconrad/try2-pac4j-sb-oauth>

Based on code from: <https://github.com/pac4j/spring-webmvc-pac4j-boot-demo>


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
