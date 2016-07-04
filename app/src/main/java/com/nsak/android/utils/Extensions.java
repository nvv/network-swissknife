package com.nsak.android.utils;

import android.text.*;

import com.nsak.android.App;

/**
 * @author Vlad Namashko
 */

public class Extensions {

    public static int tryParse(String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ignored) {
        }

        return defaultValue;
    }

    public static double tryParse(String string, double defaultValue) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException ignored) {
        }

        return defaultValue;
    }

    public static boolean tryParse(String string, boolean defaultValue) {
        if (android.text.TextUtils.isEmpty(string)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(string);
    }

    /**
     * check whether string contains pattens ignoring case.
     * Does not respect locale specific symbols.
     *
     * @param string
     * @param pattern
     * @return
     */
    public static boolean containsIgnoreCase(String string, String pattern) {
        return string.toLowerCase().contains(pattern.toLowerCase());
    }

    public static boolean areEqual(String string1, String string2) {
        if (android.text.TextUtils.isEmpty(string1) && android.text.TextUtils.isEmpty(string2)) {
            return true;
        }
        return !android.text.TextUtils.isEmpty(string1) && string1.equals(string2);
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().equals("");
    }

    public static int getResourcesId(String type, String id) {
        return App.sInstance.getResources().getIdentifier(id, type, App.sInstance.getPackageName());
    }

    public static boolean oneOf(int currentItem, int ... items) {
        if (items == null) {
            return false;
        }

        for (int item : items) {
            if (item == currentItem) {
                return true;
            }
        }

        return false;
    }

    public static <T> boolean objOneOf(T currentItem, T ... items) {
        if (items == null) {
            return false;
        }

        for (T item : items) {
            if (item.equals(currentItem)) {
                return true;
            }
        }

        return false;
    }
}
