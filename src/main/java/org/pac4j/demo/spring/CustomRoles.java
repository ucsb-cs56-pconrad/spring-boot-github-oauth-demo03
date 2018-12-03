package org.pac4j.demo.spring;

import org.springframework.core.env.Environment;

import org.pac4j.core.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Organization;
import com.jcabi.github.RtGithub;
import com.jcabi.github.User;
import com.jcabi.github.wire.RetryCarefulWire;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
   This class defines two custom roles, <code>adminRoleName</code>
   and <code>memberRoleName</code>.

 */


public class CustomRoles {

	private String	github_org = Pac4jConfig.DEFAULT.getGithubOrg();
	
	private Logger logger = LoggerFactory.getLogger(CustomRoles.class);
	
	public static final String adminRoleName = "ROLE_ADMIN";
	public static final String memberRoleName = "ROLE_MEMBER";


	/**
	   isAdmin checks whether user is an admin in the
	   designated github organization.
	*/
		
	public boolean isAdmin(CommonProfile profile) {

		String oauth_token = (String) profile.getAttribute("access_token");
		String user = (String) profile.getAttribute("login");

		logger.info("oauth_token="+oauth_token);
		logger.info("user="+user);
		for (int i=0; i<10; i++) {
			logger.info("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
		}
		logger.info("github_org="+github_org);
			
		Github github=null;
			
		try {

			// I forget why we have Github wrapped like this
			// TODO: find the tutorial that explains it
			// I think it has something to do with respecting rate limits
			github = new RtGithub(new RtGithub(oauth_token)
								  .entry()
								  .through(RetryCarefulWire.class, 50));
			
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

			Organization org = github.organizations().get(github_org);

			logger.info("org ="+ org);
				
			JsonResponse jr = github.entry()
				.uri().path("/user/memberships/orgs/" + github_org)
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

	// TODO: Refactor common code from isAdmin and isMember

	/**
	   Is the user a member of the organization? (But not an admin)
	 */
	
	public boolean isMember(CommonProfile profile) {
		String oauth_token = (String) profile.getAttribute("access_token");
		String user = (String) profile.getAttribute("login");

		logger.info("oauth_token="+oauth_token);
		logger.info("user="+user);
			
		Github github=null;
			
		try {

			// I forget why we have Github wrapped like this
			// TODO: find the tutorial that explains it
			// I think it has something to do with respecting rate limits
			github = new RtGithub(new RtGithub(oauth_token)
								  .entry()
								  .through(RetryCarefulWire.class, 50));
			
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

			Organization org = github.organizations().get(github_org);

			logger.info("org ="+ org);
				
			JsonResponse jr = github.entry()
				.uri().path("/user/memberships/orgs/" + github_org)
				.back()
				.method(Request.GET)
				.fetch()
				.as(JsonResponse.class);

			String role = jr.json().readObject().getString("role");
				
			logger.info("jr ="+ jr);				
			logger.info("role ="+ role);

			if (role.equals("member") || role.equals("admin") ) {
				return true;
			}
				
		} catch (Exception e) {
			logger.warn("Exception happened while trying to determine membership in github org");
			logger.warn(e.toString());
		} 
		return false;
	}


}
