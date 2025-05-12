package com.klaxon.daily.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BearerUtil {

    public static String getBearer(String bearer) {
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }
}
