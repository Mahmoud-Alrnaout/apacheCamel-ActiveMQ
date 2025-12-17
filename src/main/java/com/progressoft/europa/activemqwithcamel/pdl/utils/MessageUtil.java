package com.progressoft.europa.activemqwithcamel.pdl.utils;

import com.google.gson.Gson;

public class MessageUtil {


    // we make this to not init the class (by ahmad salah xD)
    private MessageUtil() {
    }

    public static <T> String toJson(T object, Gson gson) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz, Gson gson) {
        return gson.fromJson(json, clazz);
    }
}