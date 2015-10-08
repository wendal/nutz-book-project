package net.wendal.nutzbook.module.u2f;

import static net.wendal.nutzbook.util.RedisInterceptor.jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.crossscreen.CrossScreenUserToken;
import net.wendal.nutzbook.module.BaseModule;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import com.yubico.u2f.U2F;
import com.yubico.u2f.data.DeviceRegistration;
import com.yubico.u2f.data.messages.AuthenticateRequestData;
import com.yubico.u2f.data.messages.AuthenticateResponse;
import com.yubico.u2f.data.messages.RegisterRequestData;
import com.yubico.u2f.data.messages.RegisterResponse;
import com.yubico.u2f.exceptions.DeviceCompromisedException;
import com.yubico.u2f.exceptions.NoEligableDevicesException;

@At("/u2f")
@IocBean(create="init")
@Ok("json")
public class U2FModule extends BaseModule {

    public static final String APP_ID = "https://nutz.cn";

    protected final Map<String, String> requestStorage = new ConcurrentHashMap<String, String>();
    
    protected final U2F u2f = U2F.withoutAppIdValidation();

    @At("/startRegistration")
    @GET
    public Object startRegistration(@Attr(scope=Scope.SESSION, value="me")int userId) {
    	if (userId < 1)
    		return ajaxFail("请先登录");
    	User user = dao.fetch(User.class, userId);
        RegisterRequestData registerRequestData = u2f.startRegistration(APP_ID, getRegistrations(user.getName()));
        requestStorage.put(registerRequestData.getRequestId(), registerRequestData.toJson());
        return ajaxOk(registerRequestData.getRegisterRequests().get(0).getChallenge());
    }

    @At("/finishRegistration")
    @POST
    public Object finishRegistration(@Param("tokenResponse") String response, @Attr(scope=Scope.SESSION, value="me")int userId) {
    	if (userId < 1)
    		return ajaxFail("请先登录");
    	User user = dao.fetch(User.class, userId);
        RegisterResponse registerResponse = RegisterResponse.fromJson(response);
        RegisterRequestData registerRequestData = RegisterRequestData.fromJson(requestStorage.remove(registerResponse.getRequestId()));
        DeviceRegistration registration = u2f.finishRegistration(registerRequestData, registerResponse);
        addRegistration(user.getName(), registration);
        return ajaxOk(null);
    }

    @At("/startAuthentication")
    @GET
    public Object startAuthentication(@Param("username")String username) throws NoEligableDevicesException {
        AuthenticateRequestData authenticateRequestData = u2f.startAuthentication(APP_ID, getRegistrations(username));
        requestStorage.put(authenticateRequestData.getRequestId(), authenticateRequestData.toJson());
        return ajaxOk(authenticateRequestData.toJson());
    }

    @At("/finishAuthentication")
    @POST
    public Object finishAuthentication(@Param("tokenResponse") String response, @Param("username")String username) {
        AuthenticateResponse authenticateResponse = AuthenticateResponse.fromJson(response);
        AuthenticateRequestData authenticateRequest = AuthenticateRequestData.fromJson(requestStorage.remove(authenticateResponse.getRequestId()));
        DeviceRegistration registration = null;
        try {
            registration = u2f.finishAuthentication(authenticateRequest, authenticateResponse, getRegistrations(username));
        } catch (DeviceCompromisedException e) {
            registration = e.getDeviceRegistration();
            return ajaxFail(registration.toJson());
        }
        CrossScreenUserToken cs = new CrossScreenUserToken(dao.fetch(User.class, username).getId());
        Subject subject = SecurityUtils.getSubject();
		subject.login(cs);
		subject.getSession().setAttribute(NutShiro.SessionKey, subject.getPrincipal());
        return ajaxOk(null);
    }

    @Aop("redis")
    protected Iterable<DeviceRegistration> getRegistrations(String userId) {
        List<DeviceRegistration> registrations = new ArrayList<DeviceRegistration>();
        String auth = jedis().hget(RKEY_U2F_AUTH, userId+"");
        if (auth != null) {
        	registrations.add(DeviceRegistration.fromJson(auth));
        }
        return registrations;
    }

    @Aop("redis")
    protected void addRegistration(String userId, DeviceRegistration registration) {
    	jedis().hset(RKEY_U2F_AUTH, userId+"", registration.toJson());
    }
}