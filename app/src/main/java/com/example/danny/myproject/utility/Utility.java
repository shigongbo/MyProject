package com.example.danny.myproject.utility;

import android.os.Environment;

/**
 * Created by danny on 2015/6/5.
 */
public class Utility {

    public static String getSdcardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getExceptionMessage(Exception ex){
        String result="";
        StackTraceElement[] stes = ex.getStackTrace();
        for(int i=0;i<stes.length;i++){
            result=result+stes[i].getClassName()
                    + "." + stes[i].getMethodName()
                    + "  " + stes[i].getLineNumber() +"line"
                    +"\r\n";
        }
        return result;
    }
}
