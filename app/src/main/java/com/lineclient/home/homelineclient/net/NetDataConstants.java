package com.lineclient.home.homelineclient.net;

public class NetDataConstants {

    //public static final String SERVER_URL = "http://yfykisssky.eicp.net:57588/HomeLines";
    //public static final String SERVER_URL="http://192.168.134.28:8080/HomeLines";
    public static final String SERVER_URL = "http://192.168.0.108:8080/HomeLineServer";

    public static final String WS_SERVER_URL = "ws://192.168.0.108:8080/HomeLineServer";

    public static final String WS_URL = WS_SERVER_URL + "/websocket";

    public static final String GETPUBKEY = SERVER_URL + "/getpubkey";

    public static final String LOGIN = SERVER_URL + "/login";

    public static final String LOGIN_OUT = SERVER_URL + "/loginout";

    public static final String SEND_CODE = SERVER_URL + "/sendcode";

    public static final String CHANGE_PASSWORD = SERVER_URL + "/changepassword";

    public static final String GETDATA = SERVER_URL + "/getdata";

    public static final String SEND_ORDER = SERVER_URL + "/sendorder";
}
