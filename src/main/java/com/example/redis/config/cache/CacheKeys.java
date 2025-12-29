package com.example.redis.config.cache;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class CacheKeys {
    private CacheKeys() {}

    public static final String PRODUCT_BY_ID = "product:v1:%d";
    public static final String PRODUCT_LIST_VER = "products:list:ver";
    public static final String PRODUCT_SEARCH_VER = "products:search:ver";

    public static String productById(long id) {
        return String.format(PRODUCT_BY_ID, id);
    }

    public static String listKey(long ver, int page, int size, String sort) {
        return "products:list:v" + ver
                + ":page=" + page
                + ":size=" + size
                + ":sort=" + enc(sort);
    }

    public static String searchKey(long ver, String q, int page, int size, String sort) {
        return "products:search:v" + ver
                + ":q=" + enc(q)
                + ":page=" + page
                + ":size=" + size
                + ":sort=" + enc(sort);
    }

    private static String enc(String s) {
        if (s == null) return "";
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
