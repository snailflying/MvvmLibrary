package com.juqitech.moretickets.core.http;

/**
 * @author zhanfeng
 * @date 2019-06-05
 * @desc
 */
public class HttpStatusCode {

    public static final int NETWORK_EXCEPTION = -1000;
    public static final int NOT_NETWORK_EXCEPTION = -1;
    public static final int PARSE_EXCEPTION = -2;
    public static final int EXCEPTION = -10;
    public static final int SUCCESS = 200;
    public static final int SESSION_EXPIRED = 1005;
    public static final int LOGIN_EXPIRED = 1006;
    public static final int REFRESH_SESSION_FAILURE = -1005;

}
