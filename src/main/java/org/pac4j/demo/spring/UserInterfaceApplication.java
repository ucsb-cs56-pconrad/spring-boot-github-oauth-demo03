package org.pac4j.demo.spring;



import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.springframework.annotation.ui.RequireAnyRole;
import org.pac4j.springframework.helper.UISecurityHelper;
import org.pac4j.springframework.web.LogoutController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



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
    public String root(Model model) throws HttpAction {
        return index(model);
    }

	public void setAttributes(Model model) {
		List<CommonProfile> profiles = uiSecurityHelper.getProfiles();
		model.addAttribute("profiles",profiles);
		model.addAttribute("profiles_s",profiles.toString());

		final J2EContext context = uiSecurityHelper.getJ2EContext();
		SessionStore<J2EContext> ss = context.getSessionStore();
		String sessionID = ss.getOrCreateSessionId(context);
        model.addAttribute("sessionId",sessionID);
	}
	  
	
	@RequestMapping("/index.html")
    public String index(Model model) throws HttpAction {
		setAttributes(model);
        return "index";
    }


    @RequestMapping("/github/index.html")
    public String github(Model model) {
        return protectedIndex(model);
    }


    @RequestMapping("/admin/index.html")
    @RequireAnyRole("ROLE_ADMIN")
    public String github_admin(Model model) {
        return protectedIndex(model);
    }

	
    @RequestMapping("/member/index.html")
    public String github_member(Model model) {
        return protectedIndex(model);
    }


    @RequestMapping("/protected/index.html")
    public String protect(Model model) {
        return protectedIndex(model);
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

    protected String protectedIndex(Model model) {
		setAttributes(model);
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
