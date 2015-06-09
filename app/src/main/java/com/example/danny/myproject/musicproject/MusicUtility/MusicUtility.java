package com.example.danny.myproject.musicproject.musicutility;

import com.example.danny.myproject.utility.Utility;

/**
 * Created by danny on 2015/6/5.
 */
public class MusicUtility {

    public static String getMusicPath() {
        String sdcardPath = Utility.getSdcardPath();
        return sdcardPath + "/musictest/test.mp3";
    }

}
