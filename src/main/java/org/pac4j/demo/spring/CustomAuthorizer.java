package org.pac4j.demo.spring;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.authorization.authorizer.ProfileAuthorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CustomAuthorizer extends ProfileAuthorizer<CommonProfile> {


	private Logger logger = LoggerFactory.getLogger(CustomAuthorizer.class);
	
    @Override
    public boolean isAuthorized(final WebContext context,
				final List<CommonProfile> profiles) throws HttpAction {
        return isAnyAuthorized(context, profiles);
    }

    @Override
    public boolean isProfileAuthorized(final WebContext context,
				       final CommonProfile profile) {

		logger.trace("A TRACE Message");
        logger.debug("A DEBUG Message");
        logger.info("An INFO Message... profile.getUsername()="+profile.getUsername());
		
        if (profile == null) {
            return false;
        }
		
		// NOTE: THIS IS WHERE YOU HARD CODE A PARTICULAR USERNAME...
        return ( profile.getUsername().equals("bnieder") || Pac4jConfig.isAdmin(profile) );
    }
}
