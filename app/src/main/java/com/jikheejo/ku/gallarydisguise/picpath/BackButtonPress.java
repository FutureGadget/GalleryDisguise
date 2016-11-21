package com.jikheejo.ku.gallarydisguise.picpath;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import com.jikheejo.ku.gallarydisguise.FakeHome;

/**
 * Created by daseob on 2016-11-21.
 */

public class BackButtonPress {
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackButtonPress(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity,
                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
