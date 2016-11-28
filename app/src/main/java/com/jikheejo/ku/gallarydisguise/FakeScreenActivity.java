package com.jikheejo.ku.gallarydisguise;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class FakeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_home);

        // Fake quit button
        Button quit = (Button)findViewById(R.id.quitbutton);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final LongPressChecker mLongPressChecker = new LongPressChecker(this);
        mLongPressChecker.setOnLongPressListner(new LongPressChecker.OnLongPressListner() {
            @Override
            public void onLongPressed() {
                setResult(HomeScreenActivity.RESULT_PASS);
                finish();
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

    @Override
    public void onBackPressed() {
        setResult(HomeScreenActivity.RESULT_CLOSE_ALL);
        finish();
        super.onBackPressed();
    }
}
