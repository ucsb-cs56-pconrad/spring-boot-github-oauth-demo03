package org.pac4j.demo.spring;



import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.client.GitHubClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

@Configuration
public class Pac4jConfig {

    @Value("${salt}")
    private String salt;
    
    @Value("${baseUrl}")
    private String baseUrl;

    @Value("${github_client_id}")
    private String github_client_id;

    @Value("${github_client_secret}")
    private String github_client_secret;

    @Value("${facebook_client_id}")
    private String facebook_client_id;

    @Value("${facebook_client_secret}")
    private String facebook_client_secret;

    @Bean
    public Config config() {

        final FacebookClient facebookClient =
	    new FacebookClient(facebook_client_id, facebook_client_secret);

	// Facebook Scopes:
	// https://stackoverflow.com/questions/30094382/facebook-invalid-scope-error
	// https://developers.facebook.com/docs/facebook-login/permissions/
	// To avoid triggering app review, use "default" or "email" only.
	// The default value in the FacebookClient class is not "default",
	// unfortunately, and includes invalid scopes.
	
	facebookClient.setScope("default"); // (or "email")
	
        final GitHubClient ghClient =
	    new GitHubClient(github_client_id, github_client_secret);

        final Clients clients =
	    new Clients(baseUrl + "/callback",
			facebookClient,
			ghClient);

	// In that case, the callback URL of the FacebookClient is
	// baseUrl + "/callback?client_name=FacebookClient
	// and the callback URL of the GitHubClient is
	// baseUrl + "/callback?client_name=GitHubClient.
	
        final Config config = new Config(clients);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
        config.addAuthorizer("custom", new CustomAuthorizer());
        return config;
    }
}
