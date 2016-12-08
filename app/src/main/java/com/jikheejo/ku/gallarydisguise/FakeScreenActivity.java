package com.jikheejo.ku.gallarydisguise;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

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

        //로고에 long press checker 설정
        ImageView iv = (ImageView)findViewById(R.id.fakelogo);
        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mLongPressChecker.deliverMotionEvent(v, event);
                return false;
            }
        });

        ToggleButton monitoring = (ToggleButton)findViewById(R.id.monitoring);
        ToggleButton saver = (ToggleButton)findViewById(R.id.saver);
        ToggleButton detection = (ToggleButton)findViewById(R.id.detection);

        monitoring.setChecked(true);
        monitoring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setBackgroundColor(Color.parseColor("#3F51B5"));
                } else {
                    buttonView.setBackgroundColor(Color.parseColor("#d8dcf0"));
                }
            }
        });

        saver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setBackgroundColor(Color.parseColor("#3F51B5"));
                } else {
                    buttonView.setBackgroundColor(Color.parseColor("#d8dcf0"));
                }
            }
        });

        detection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setBackgroundColor(Color.parseColor("#3F51B5"));
                } else {
                    buttonView.setBackgroundColor(Color.parseColor("#d8dcf0"));
                }
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
