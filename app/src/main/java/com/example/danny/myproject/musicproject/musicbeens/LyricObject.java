
package com.example.danny.myproject.musicproject.musicbeens;

public class LyricObject {

    public int lrcTimestamp; // 每句歌词前的时间戳
    public String lrcText; // 每句歌词内容

    public int getLrcTimestamp() {
        return lrcTimestamp;
    }

    public void setLrcTimestamp(int lrcTimestamp) {
        this.lrcTimestamp = lrcTimestamp;
    }

    public String getLrcText() {
        return lrcText;
    }

    public void setLrcText(String lrcText) {
        this.lrcText = lrcText;
    }
}
