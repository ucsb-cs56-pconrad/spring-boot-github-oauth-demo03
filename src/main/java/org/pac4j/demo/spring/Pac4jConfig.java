package org.pac4j.demo.spring;

import org.pac4j.core.authorization.generator.AuthorizationGenerator;

import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.oauth.client.GitHubClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@Configuration
public class Pac4jConfig {

	private Logger logger = LoggerFactory.getLogger(Pac4jConfig.class);
	
    @Value("${salt}")
    private String salt;
    
    @Value("${baseUrl}")
    private String baseUrl;

    @Value("${github_client_id}")
    private String github_client_id;

    @Value("${github_client_secret}")
    private String github_client_secret;


    @Bean
    public Config config() {

        final GitHubClient ghClient =
			new GitHubClient(github_client_id, github_client_secret);

		// https://developer.github.com/apps/building-oauth-apps/understanding-scopes-for-oauth-apps/
		ghClient.setScope("user");


		AuthorizationGenerator authGen = (ctx, profile) -> {
			String login = (String) profile.getAttribute("login");
			if (login.equals("pconrad")) {

				logger.info("An INFO Message... adding ROLE_ADMIN for "+login);
				
				profile.addRole("ROLE_ADMIN");
			}
			return profile;
		};
		ghClient.addAuthorizationGenerator(authGen);

		
        final Clients clients =
	    new Clients(baseUrl + "/callback",
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
