
package com.example.danny.myproject.utility;

import android.util.Log;

public class Logger {
    private static boolean enableLog = true;

    public static void i(String tag, String msg) {
        if (enableLog) {
            Log.i(ProjectConstant.APP_TAG, tag + "_" + msg);
        }
    }

    public static void d(String tag, String msg) {
        if (enableLog) {
            Log.d(ProjectConstant.APP_TAG, tag + "_" + msg);
        }
    }

    public static void v(String tag, String msg) {
        if (enableLog) {
            Log.v(ProjectConstant.APP_TAG, tag + "_" + msg);
        }
    }

    public static void w(String tag, String msg) {
        if (enableLog) {
            Log.w(ProjectConstant.APP_TAG, tag + "_" + msg);
        }
    }

    public static void e(String tag, String msg) {
        Log.e(ProjectConstant.APP_TAG, tag + "_" + msg);
    }

}
