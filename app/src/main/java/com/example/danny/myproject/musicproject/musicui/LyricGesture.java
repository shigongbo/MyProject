package com.example.danny.myproject.musicproject.musicui;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.example.danny.myproject.musicproject.MusicPlayerActivity;

public class LyricGesture implements OnTouchListener, OnGestureListener {
    private MusicPlayerActivity mContext;
    private GestureDetector mGestureDetector;
    private int way;
    private boolean starttoggle;
    private boolean updatetoggle;

    public LyricGesture(Context context) {
        this.mContext = (MusicPlayerActivity) context;
        mGestureDetector = new GestureDetector(context, this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.d("onTouch", "onTouch TTTTTTTTTT");
            way = 0;
            if (updatetoggle) {
                mContext.updatePlayer();
                updatetoggle = false;
            }
        }
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
                           float arg3) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent arg0) {
    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
                            float arg3) {
        Log.d("onTouch", "onScroll SSSSSSSSSSSSSS");
        //前3次用作starttoggle触发条件
        if (way < 3) {
            mContext.updatelab(-arg2, -arg3, true);
            way++;
        } else {
            starttoggle = mContext.updatelab(-arg2, -arg3, false);
            if (starttoggle) {
                updatetoggle = true;
                mContext.slidestart();
                starttoggle = false;
            }
            if (updatetoggle) {
                mContext.updateprogress(-arg2, -arg3);
            }
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent arg0) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        return false;
    }

}
