package net.wendal.nutzbook.oauth.service;

import org.brickred.socialauth.SocialAuthConfig;

public interface OauthService {

    SocialAuthConfig getSocialAuthConfig() throws Exception;
    
    void reload() throws Exception;
}
