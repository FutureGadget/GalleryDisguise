package com.jikheejo.ku.gallarydisguise;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;

public class FakeHome extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_home);

        SharedPreferences setting = getSharedPreferences("setting", 0);

        boolean run = setting.getBoolean("fake", false);

        if (run == false){
            Intent inten = new Intent(FakeHome.this, HomeScreenActivity.class);
            startActivity(inten);
        }
        final LongPressChecker mLongPressChecker = new LongPressChecker(this);
        mLongPressChecker.setOnLongPressListner(new LongPressChecker.OnLongPressListner() {
            @Override
            public void onLongPressed() {
                Intent inten = new Intent(FakeHome.this, HomeScreenActivity.class);
                startActivity(inten);
            }
        });

        Button b = (Button)findViewById(R.id.logobutton);
        b.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mLongPressChecker.deliverMotionEvent(v, event);
                return false;
            }
        });
    }
}


