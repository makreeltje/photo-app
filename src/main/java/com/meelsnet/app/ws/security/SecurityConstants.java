package com.meelsnet.app.ws.security;

import com.meelsnet.app.ws.SpringApplicationContext;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 864000000; // 10 days
    public static final long PASSWORD_RESET_EXPIRATION_TIME = 1000*60*60; // 1 hour
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/v1/users";
    public static final String VERIFICATION_EMAIL_URL = "/api/v1/users/email-verification";
    public static final String PASSWORD_RESET_REQUEST_URL = "/api/v1/users/password-reset-request";
    public static final String PASSWORD_RESET_URL = "/api/v1/users/password-reset";

    public static String getTokenSecret() {
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
        return appProperties.getTokenSecret();
    }
}
