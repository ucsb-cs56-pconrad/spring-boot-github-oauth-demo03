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


import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;


import com.jcabi.github.RtGithub;
import com.jcabi.github.Github;
import com.jcabi.github.User;
import com.jcabi.github.wire.RetryCarefulWire;
import com.jcabi.github.Organization;
import com.jcabi.github.Coordinates;

import org.pac4j.core.profile.CommonProfile;

@Configuration
public class Pac4jConfig {

	private static Logger logger = LoggerFactory.getLogger(Pac4jConfig.class);
	
    @Value("${salt}")
    private String salt;
    
    @Value("${baseUrl}")
    private String baseUrl;

    @Value("${github_client_id}")
    private String github_client_id;

    @Value("${github_client_secret}")
    private String github_client_secret;


	public static boolean isAdmin(CommonProfile profile) {
		String oauth_token = (String) profile.getAttribute("access_token");
		String user = (String) profile.getAttribute("login");

		logger.info("oauth_token="+oauth_token);
		logger.info("user="+user);
			
		Github github=null;
			
		try {
			String orgname = "ucsb-cs56-f18";
			github = new RtGithub(
								  new RtGithub(oauth_token).entry().through(RetryCarefulWire.class, 50)
								  );
			
			logger.info("github="+github);
			User ghuser = github.users().get(user);
			logger.info("ghuser="+ghuser);
				
			JsonResponse jruser = github.entry()
				.uri().path("/user")
				.back()
				.method(Request.GET)
				.fetch()
				.as(JsonResponse.class);
				
			logger.info("jruser ="+ jruser);

			Organization org = github.organizations().get(orgname);

			logger.info("org ="+ org);
				
			JsonResponse jr = github.entry()
				.uri().path("/user/memberships/orgs/" + orgname)
				.back()
				.method(Request.GET)
				.fetch()
				.as(JsonResponse.class);

			String role = jr.json().readObject().getString("role");
				
			logger.info("jr ="+ jr);				
			logger.info("role ="+ role);

			if (role.equals("admin")) {
				return true;
			}
				
		} catch (Exception e) {
			logger.warn("Exception happened while trying to determine membership in github org");
			logger.warn(e.toString());
		} 
		return false;
	}
	
    @Bean
    public Config config() {

        final GitHubClient ghClient =
			new GitHubClient(github_client_id, github_client_secret);

		// https://developer.github.com/apps/building-oauth-apps/understanding-scopes-for-oauth-apps/
		ghClient.setScope("read:org");

		AuthorizationGenerator authGen = (ctx, profile) -> {
			if (isAdmin(profile)) {
				profile.addRole("ROLE_ADMIN");
				logger.info("added role ROLE_ADMIN to pac4j profile");
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
