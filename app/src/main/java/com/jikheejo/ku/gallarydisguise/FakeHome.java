package com.jikheejo.ku.gallarydisguise;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FakeHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_home);
        Button b = (Button)findViewById(R.id.logobutton);
        b.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent inten = new Intent(FakeHome.this, HomeScreenActivity.class);
                startActivity(inten);
                return false;
            }
        });
    }


}
