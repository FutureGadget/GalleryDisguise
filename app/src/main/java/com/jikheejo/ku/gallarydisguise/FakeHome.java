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
import android.widget.ImageView;

import com.jikheejo.ku.gallarydisguise.Encryption.GenerateKey;

public class FakeHome extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_home);

        SharedPreferences setting = getSharedPreferences("setting", 0);
        final SharedPreferences.Editor editor = setting.edit();

        boolean run = setting.getBoolean("fake", false);

        /*
            initial check, generate key if this app is executed for the first time.
            Use the key to generate the genuine key(through a hidden algorithm) and use it for
            encryption/decryption.
         */
        boolean initialStart = setting.getBoolean("first", true);
        if (initialStart) {
            editor.putBoolean("first", false);
            String key = GenerateKey.randKey();
            editor.putString("key", key);
            editor.commit();
        }

        if (!run){
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

        ImageView iv = (ImageView)findViewById(R.id.fakelogo);
        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mLongPressChecker.deliverMotionEvent(v, event);
                return false;
            }
        });
    }
}


