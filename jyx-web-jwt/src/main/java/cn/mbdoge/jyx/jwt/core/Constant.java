package cn.mbdoge.jyx.jwt.core;

public class Constant {
    public static final String AUTHENTICATION_SCHEME_BEARER = "bearer ";
    public static final String AUTHENTICATION_SCHEME_BASIC = "basic ";

    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CHECK_KEY = "c";
    private static final String CHECK_VALUE = "u";
    private static final String CLAIM_KEY_CREATED = "created";
    private static final String CLAIM_KEY_ROLES = "roles";
    private static final String AUTHORITY_KEY = "authority";
//    public static final long UNLIMITED_TIME_VALUE = -1000L;
}
