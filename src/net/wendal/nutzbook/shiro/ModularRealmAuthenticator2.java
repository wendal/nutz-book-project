package net.wendal.nutzbook.shiro;

import java.util.Collection;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 改变Shiro原生的ModularRealmAuthenticator在多个Realm时的行为,如果仅一个Realm识别Token,那么行为跟单个Realm时一致
 * @author wendal
 *
 */
public class ModularRealmAuthenticator2 extends ModularRealmAuthenticator {
	
	private static final Logger log = LoggerFactory.getLogger(ModularRealmAuthenticator.class);

    /**
     * 官方实现里面, 如果有多个Realm,无论是不是只有一个Realm适合当前的AuthenticationToken,授权失败的时候都会抛出一个令人费解的异常
     */
    protected AuthenticationInfo doMultiRealmAuthentication(Collection<Realm> realms, AuthenticationToken token) {

        AuthenticationStrategy strategy = getAuthenticationStrategy();

        AuthenticationInfo aggregate = strategy.beforeAllAttempts(realms, token);

        if (log.isTraceEnabled()) {
            log.trace("Iterating through {} realms for PAM authentication", realms.size());
        }

        Throwable t = null;
        int count = 0;
        Realm _realm = null;
        for (Realm realm : realms) {

            if (realm.supports(token)) {
            	count ++;
            	_realm = realm;
                log.trace("Attempting to authenticate token [{}] using realm [{}]", token, realm);

                AuthenticationInfo info = null;
                try {
                    info = realm.getAuthenticationInfo(token);
                } catch (Throwable throwable) {
                    t = throwable;
                    if (log.isDebugEnabled()) {
                        String msg = "Realm [" + realm + "] threw an exception during a multi-realm authentication attempt:";
                        log.debug(msg, t);
                    }
                }

                aggregate = strategy.afterAttempt(realm, token, info, aggregate, t);

            } else {
                log.debug("Realm [{}] does not support token {}.  Skipping realm.", realm, token);
            }
        }
        if (count == 1 && t != null) {
        	String msg = "Realm [" + _realm + "] was unable to find account data for the " +
                    "submitted AuthenticationToken [" + token + "].";
            throw new UnknownAccountException(msg);
        }
        	
        aggregate = strategy.afterAllAttempts(token, aggregate);

        return aggregate;
    }
}
