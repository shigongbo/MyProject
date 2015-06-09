
package com.example.danny.myproject.musicproject.musicui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.danny.myproject.musicproject.musicbeens.LyricObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 显示歌词内容
 */
public class LyricView extends TextView {

    private String TAG = getClass().getSimpleName();

    private Paint mPaint;
    private float mX;
    private Paint mPathPaint;
    public int index = 0;
    public float mTouchHistoryY;
    private int mY;
    private int currentDuringTime;// 当前歌词持续时间
    private float middleY;// Y轴中间
    private final int DY = 40;// 每一行的间隔
    public float driftX;// x偏移量
    public float driftY;// y偏移量
    private float drift_r;
    public boolean showProgress;// 滑动时显示进度
    public int temp = 0;

    private List<LyricObject> mLyricObjectList = new ArrayList<>();
    private Boolean hasLyricText = false;

    public LyricView(Context context) {
        super(context);
        init();
    }

    public LyricView(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    public LyricView(Context context, AttributeSet attr, int i) {
        super(context, attr, i);
        init();
    }

    /**
     * @param lyricObjectList 歌词文件的list
     * @param hasText         本地是否存在歌词
     */
    public void initLyricText(List<LyricObject> lyricObjectList, boolean hasText) {
        mLyricObjectList = lyricObjectList;
        hasLyricText = hasText;
    }

    private void init() {
        setFocusable(true);
        // 非高亮部分
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(18);
        mPaint.setColor(Color.GREEN);
        mPaint.setTypeface(Typeface.SERIF);
        // 高亮部分 当前歌词
        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true);
        mPathPaint.setTextSize(21);
        mPathPaint.setColor(Color.RED);
        mPathPaint.setTypeface(Typeface.SANS_SERIF);
    }

    protected void onDraw(Canvas canvas) {
        if (hasLyricText) {
            // 滑动相关
            // 显示进度相关
            int j = (int) (-driftY / 40);
            if (temp < j) {
                temp++;
            } else if (temp > j) {
                temp--;
            }

            if (index + temp >= 0 && index + temp < mLyricObjectList.size() - 1)
                drift_r = driftY;

//            canvas.drawColor(0xFFFFFF);
            Paint p = mPaint;
            Paint p2 = mPathPaint;
            p.setTextAlign(Paint.Align.CENTER);

            if (index == -1)
                return;
            p2.setTextAlign(Paint.Align.CENTER);
            // 先画当前行，之后再画他的前面和后面，这样就保持了当前行在中间的位置
            canvas.drawText(mLyricObjectList.get(index).getLrcText(), mX, middleY + drift_r, p2);
            if (showProgress && index + temp < mLyricObjectList.size() - 1) {
                p2.setTextAlign(Paint.Align.LEFT);
                if (index + temp >= 0) {
                    canvas.drawText(MsecParseTime(mLyricObjectList.get(index + temp).getLrcTimestamp
                            () + ""), 0, middleY, p2);
                } else {
                    canvas.drawText("00:00", 0, middleY, p2);
                }
                canvas.drawLine(0, middleY + 1, mX * 2, middleY + 1, p2);
            }
            float tempY = middleY + drift_r;
            // 画出本句之前的句子
            for (int i = index - 1; i >= 0; i--) {
                // 向上推移
                tempY = tempY - DY;
                if (tempY < 0) {
                    break;
                }
                canvas.drawText(mLyricObjectList.get(i).getLrcText(), mX, tempY, p);
            }
            tempY = middleY + drift_r;
            // 画出本句之后的句子
            for (int i = index + 1; i < mLyricObjectList.size(); i++) {
                // 向下推移
                tempY = tempY + DY;
                if (tempY > mY) {
                    break;
                }
                canvas.drawText(mLyricObjectList.get(i).getLrcText(), mX, tempY, p);
            }
        } else {
            Paint p = mPathPaint;
            p.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("找不到歌词", mX, 310, p);
        }

        super.onDraw(canvas);


    }

    private String MsecParseTime(String Mesc) {
        int mescint = Integer.parseInt(Mesc) / 1000;
        String ss = String.valueOf(mescint % 60);
        String mm = String.valueOf(mescint / 60);
        if (ss.length() == 1) ss = "0" + ss;
        if (mm.length() == 1) mm = "0" + mm;
        String time = mm + ":" + ss;
        return time;
    }

    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        mX = w * 0.5f;// 屏幕中心坐标(转换为float?)
        mY = h;
        middleY = h * 0.5f;
    }

    /**
     * @param CurrentPosition 当前歌词的时间轴
     * @return drift 可以返回数据（已经废弃）
     */
    public float updateindex(int CurrentPosition) {
        // 歌词数组的序号
        while (CurrentPosition < mLyricObjectList.get(index).getLrcTimestamp()) {
            index--;
            if (index < 0) {
                index = 0;
                break;
            }
        }

        if (CurrentPosition >= mLyricObjectList.get(index).getLrcTimestamp() && index + 1 <
                mLyricObjectList.size() && CurrentPosition < mLyricObjectList.get(index + 1).getLrcTimestamp()) {
            currentDuringTime = mLyricObjectList.get(index + 1).getLrcTimestamp() - mLyricObjectList
                    .get(index).getLrcTimestamp();
//            index++;
            driftY = 0;
            driftX = 0;
        } else if (index + 1 < mLyricObjectList.size() && CurrentPosition >= mLyricObjectList.get
                (index + 1).getLrcTimestamp()) {
            while (index + 1 < mLyricObjectList.size() && CurrentPosition > mLyricObjectList.get
                    (index + 1).getLrcTimestamp()) {
                index++;
            }

            if (index < mLyricObjectList.size() - 1) {
                currentDuringTime = mLyricObjectList.get(index + 1).getLrcTimestamp() - mLyricObjectList
                        .get(index).getLrcTimestamp();
            } else {
                currentDuringTime = CurrentPosition - mLyricObjectList.get(index).getLrcTimestamp();
            }
            driftY = 0;
            driftX = 0;

        } else if (index == 0) {
            currentDuringTime = mLyricObjectList.get(index).getLrcTimestamp();
        }

        if (driftY > -40.0) {
            driftY = (float) (driftY - 40.0 / (currentDuringTime / 100));
        }


        /*if (index > 0) {
            Logger.d(TAG, "updateindex  index == " + index);
            Logger.d(TAG, "updateindex  CurrentPosition == " + CurrentPosition);
            Logger.d(TAG, "updateindex  mLyricObjectList.get(index).getLrcText() == " + mLyricObjectList.get(index).getLrcText());
        }*/

        if (index == -1) {
            return -1;
        }

        return driftY;
    }

    public boolean repair() {
        if (index <= 0) {
            index = 0;
            return false;
        }
        if (index > mLyricObjectList.size() - 1)
            index = mLyricObjectList.size() - 1;
        return true;
    }

}
