package com.lineclient.home.homelineclient.tools;

public class DataUtils {

    private static String userName;

    private static String token;

    private static String aesNetKey;

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        DataUtils.userName = userName;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        DataUtils.token = token;
    }

    public static String getAesNetKey() {
        return aesNetKey;
    }

    public static void setAesNetKey(String aesNetKey) {
        DataUtils.aesNetKey = aesNetKey;
    }
}
