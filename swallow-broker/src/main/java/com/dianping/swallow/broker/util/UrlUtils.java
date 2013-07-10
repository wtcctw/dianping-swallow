package com.dianping.swallow.broker.util;

public class UrlUtils {
    private static String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    public static boolean isValidUrl(String url) {
        if (url == null) {
            return false;
        }
        return url.matches(regex);
    }

    public static void main(String[] args) {
        System.out.println(isValidUrl(""));
    }
}
