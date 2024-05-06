package com.stormerg.gbotj.utils;

public  class StringUtility {

    public static String escapeForJson(final String content) {
        return content
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replaceAll("\n", "\\\\n");
    }
}
