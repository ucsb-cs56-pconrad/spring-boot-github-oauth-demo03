package org.pac4j.demo.spring;

import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.springframework.annotation.ui.RequireAnyRole;
import org.pac4j.springframework.helper.UISecurityHelper;
import org.pac4j.springframework.web.LogoutController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

@Controller
public class UserInterfaceApplication {

    @Value("${pac4j.centralLogout.defaultUrl:#{null}}")
    private String defaultUrl;

    @Value("${pac4j.centralLogout.logoutUrlPattern:#{null}}")
    private String logoutUrlPattern;

    @Autowired
    private Config config;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private UISecurityHelper uiSecurityHelper;

    private LogoutController logoutController;

    @PostConstruct
    protected void afterPropertiesSet() {
        logoutController = new LogoutController();
        logoutController.setDefaultUrl(defaultUrl);
        logoutController.setLogoutUrlPattern(logoutUrlPattern);
        logoutController.setLocalLogout(false);
        logoutController.setCentralLogout(true);
        logoutController.setConfig(config);
        logoutController.setDestroySession(false);
    }

    @RequestMapping("/")
    public String root(final Map<String, Object> map) throws HttpAction {
        return index(map);
    }

    @RequestMapping("/index.html")
    public String index(final Map<String, Object> map) throws HttpAction {
        map.put("profiles", uiSecurityHelper.getProfiles());
        final J2EContext context = uiSecurityHelper.getJ2EContext();
        map.put("sessionId", context.getSessionStore().getOrCreateSessionId(context));
        return "index";
    }

    @RequestMapping("/github/index.html")
    public String github(final Map<String, Object> map) {
        return protectedIndex(map);
    }


    @RequestMapping("/admin/index.html")
    @RequireAnyRole("ROLE_ADMIN")
    public String github_admin(final Map<String, Object> map) {
        return protectedIndex(map);
    }

	
    @RequestMapping("/custom/index.html")
    public String github_custom(final Map<String, Object> map) {
        return protectedIndex(map);
    }


    @RequestMapping("/protected/index.html")
    public String protect(final Map<String, Object> map) {
        return protectedIndex(map);
    }

    @RequestMapping("/forceLogin")
    @ResponseBody
    public void forceLogin() {
        final Client client = config.getClients().findClient(request.getParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER));
        try {
            client.redirect(uiSecurityHelper.getJ2EContext());
        } catch (final HttpAction e) {
        }
    }

    protected String protectedIndex(final Map<String, Object> map) {
        map.put("profiles", uiSecurityHelper.getProfiles());
        return "protectedIndex";
    }

    @RequestMapping("/centralLogout")
    public void centralLogout() {
        logoutController.logout(request, response);
    }

    @ExceptionHandler(HttpAction.class)
    public void httpAction() {
        // do nothing
    }
}
