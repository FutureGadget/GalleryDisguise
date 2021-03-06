package com.jikheejo.ku.gallarydisguise;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by daseob on 2016-11-20.
 * Long Press Checker
 * 현재는 3초로 시간을 직접 넣어둠
 */

//context 설정
public class LongPressChecker {
    public interface OnLongPressListner{
        void onLongPressed();
    }

    private Handler mHan = new Handler();
    private LongPressCheckRunnable mLongPressCheckRunnable = new LongPressCheckRunnable();

    private int mLongPressTimeout;
    private int mScaledTouchSlope;

    private View mTargetView;
    private OnLongPressListner mOnLongPressListner;
    private boolean mLongPressed = false;

    private float mLastX;
    private float mLastY;

    public LongPressChecker(Context con){
        if(Looper.myLooper() != Looper.getMainLooper())
            throw new RuntimeException();
        mLongPressTimeout = ViewConfiguration.getLongPressTimeout();
        mScaledTouchSlope = ViewConfiguration.get(con).getScaledPagingTouchSlop();
    }

    public void setOnLongPressListner(OnLongPressListner listen){
        mOnLongPressListner = listen;
    }
    // 각 motion에 대한 event 정의
    public  void deliverMotionEvent(View v, MotionEvent even){
        switch (even.getAction()){
            //로고 터치시 시간 측정
            case MotionEvent.ACTION_DOWN:
                mTargetView = v;
                mLastX = even.getX();
                mLastY = even.getY();
                startTimeout();
                break;
            // 그 외 반응에 시간 측정 중지
            case MotionEvent.ACTION_MOVE:
                float x = even.getX();
                float y = even.getY();
                if(Math.abs(x-mLastX)>mScaledTouchSlope || Math.abs(y-mLastY) > mScaledTouchSlope)
                    stopTimeout();
                break;
            case MotionEvent.ACTION_UP:
                stopTimeout();
                break;
            case MotionEvent.ACTION_CANCEL:
                stopTimeout();
                break;
        }
    }
    //delay값을 3초로 줌
    public void startTimeout(){
        mLongPressed = false;
        mHan.postDelayed(mLongPressCheckRunnable, 3000);
    }
    //call back
    public void stopTimeout(){
        if(!mLongPressed)
            mHan.removeCallbacks(mLongPressCheckRunnable);
    }

    private class LongPressCheckRunnable implements Runnable{
        @Override
        public  void run(){
            mLongPressed = true;
            if(mOnLongPressListner != null){
                mTargetView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                mOnLongPressListner.onLongPressed();
            }
        }
    }
}
