package com.nsak.android.utils;

/**
 * @author Vlad Namashko.
 */
public class TextUtils {

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.trim().length() == 0;
    }

}
