package com.adlitteram.jsync;

import java.util.Locale;
import java.util.ResourceBundle;

public class Messages {

    public final static ResourceBundle BUNDLE = ResourceBundle.getBundle("messages", Locale.getDefault());

    public static String message(String str) {
        try {
            return BUNDLE.getString(str);
        }
        catch (Exception e) {
            return str;
        }
    }
}
