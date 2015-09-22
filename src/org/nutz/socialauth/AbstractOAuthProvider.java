package org.nutz.socialauth;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.brickred.socialauth.AbstractProvider;
import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.Contact;
import org.brickred.socialauth.Permission;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.exception.SocialAuthException;
import org.brickred.socialauth.exception.UserDeniedPermissionException;
import org.brickred.socialauth.oauthstrategy.OAuthStrategyBase;
import org.brickred.socialauth.util.AccessGrant;
import org.brickred.socialauth.util.OAuthConfig;
import org.brickred.socialauth.util.Response;
import org.brickred.socialauth.util.SocialAuthUtil;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@SuppressWarnings("serial")
public abstract class AbstractOAuthProvider extends AbstractProvider implements AuthProvider  {
	
	private static final Log log = Logs.get();

	protected Permission scope;
	protected OAuthConfig config;
	protected Profile userProfile;
	protected AccessGrant accessGrant;
	protected OAuthStrategyBase authenticationStrategy;

	protected String[] AllPerms;
	protected String[] AuthPerms;
	protected String PROFILE_URL;

	protected Map<String, String> ENDPOINTS = new HashMap<String, String>();

	public AbstractOAuthProvider(final OAuthConfig providerConfig) throws Exception {
		this.config = providerConfig;
		//下面几个参数都是必须初始化的
		//ENDPOINTS.put(Constants.OAUTH_AUTHORIZATION_URL,"https://graph.qq.com/oauth2.0/authorize");
		//ENDPOINTS.put(Constants.OAUTH_ACCESS_TOKEN_URL,"https://graph.qq.com/oauth2.0/token");
		//authenticationStrategy = new OAuth2(config, ENDPOINTS);
		//authenticationStrategy.setPermission(scope);
		//authenticationStrategy.setScope(getScope());
		//AllPerms = new String[] { "get_user_info","get_info" };
		//AuthPerms = new String[] { "get_user_info", "get_info" };
		
		//这个可选
		//PROFILE_URL = "https://graph.qq.com/user/get_info";
	}

	public String getLoginRedirectURL(final String successUrl) throws Exception {
		return authenticationStrategy.getLoginRedirectURL(successUrl);
	}

	public Profile verifyResponse(HttpServletRequest httpReq) throws Exception {
		Map<String, String> params = SocialAuthUtil
				.getRequestParametersMap(httpReq);
		return doVerifyResponse(params);
	}

	public Profile verifyResponse(Map<String, String> params) throws Exception {
		return doVerifyResponse(params);
	}

	protected Profile doVerifyResponse(final Map<String, String> requestParams)
			throws Exception {
        log.info("Retrieving Access Token in verify response function");
        if (requestParams.get("error_reason") != null
                        && "user_denied".equals(requestParams.get("error_reason"))) {
                throw new UserDeniedPermissionException();
        }
        accessGrant = authenticationStrategy.verifyResponse(requestParams, verifyResponseMethod());

        if (accessGrant != null) {
                log.debug("Obtaining user profile");
                return authLogin();
        } else {
                throw new SocialAuthException("Access token not found");
        }
	}

    protected abstract Profile authLogin() throws Exception ;
	
	public Response api(String arg0, String arg1, Map<String, String> arg2,
			Map<String, String> arg3, String arg4) throws Exception {
		return null;
	}

	public List<Contact> getContactList() throws Exception {
		return null;
	}

	public void logout() {
	}

	public void setAccessGrant(AccessGrant accessGrant) {
		this.accessGrant = accessGrant;
	}

	@Override
	public void setPermission(Permission permission) {
		this.scope = permission;
		authenticationStrategy.setPermission(this.scope);
		authenticationStrategy.setScope(getScope());
	}

	public Response updateStatus(String status) throws Exception {
		return null;
	}

	public Profile getUserProfile() throws Exception {
		return userProfile;
	}

	public AccessGrant getAccessGrant() {
		return accessGrant;
	}

	public String getProviderId() {
		return config.getId();
	}

	protected String getScope() {
		StringBuffer result = new StringBuffer();
		String arr[] = null;
		if (Permission.AUTHENTICATE_ONLY.equals(scope)) {
			arr = AuthPerms;
		} else if (Permission.CUSTOM.equals(scope)
				&& config.getCustomPermissions() != null) {
			arr = config.getCustomPermissions().split(",");
		} else {
			arr = AllPerms;
		}
		if (arr.length > 0)
			result.append(arr[0]);
		for (int i = 1; i < arr.length; i++) {
			result.append(",").append(arr[i]);
		}
		return result.toString();
	}
	
	protected String verifyResponseMethod() {
		return "GET";
	}
	
	protected List<String> getPluginsList() {
		List<String> list = new ArrayList<String>();
		if (config.getRegisteredPlugins() != null
				&& config.getRegisteredPlugins().length > 0) {
			list.addAll(Arrays.asList(config.getRegisteredPlugins()));
		}
		return list;
	}
	
	protected OAuthStrategyBase getOauthStrategy() {
		return authenticationStrategy;
	}
	
	public Response uploadImage(String message, String fileName, InputStream inputStream) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}