package com.jikheejo.ku.gallarydisguise;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toast;

import com.jikheejo.ku.gallarydisguise.picpath.BackButtonPress;
import com.jikheejo.ku.gallarydisguise.picpath.PhotoPath;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

public class HomeScreenActivity extends Activity {
    private RecyclerView mDirRecyclerView;
    private DirListAdapter mAdapter;
    private BackButtonPress backButtonPress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        //list setting
        mDirRecyclerView = (RecyclerView)findViewById(R.id.dirRecyclerView);
        mDirRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateUI();

        //back button setting
        backButtonPress = new BackButtonPress(this);

        //toggle button setting
        final SharedPreferences setting = getSharedPreferences("setting", 0);
        final SharedPreferences.Editor editor = setting.edit();
        boolean run = setting.getBoolean("fake", false);

        final ToggleButton tb = (ToggleButton)this.findViewById(R.id.app_Disguise);
        tb.setChecked(run);
        tb.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(tb.isChecked()){
                    editor.putBoolean("fake", true);
                }
                else{
                    editor.putBoolean("fake", false);
                }
                editor.commit();
            }
        });

        // add button behavior
        Button addButton = (Button)this.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    //back button run
    @Override
    public void onBackPressed(){
        backButtonPress.onBackPressed();
    }

    private void updateUI() {
        // For test purposes only.
        List<String> dirPaths = PhotoPath.getLeafPhotoDirs();
        mAdapter = new DirListAdapter(dirPaths);
        mDirRecyclerView.setAdapter(mAdapter);
    }

    private class DirListAdapter extends RecyclerView.Adapter<DirListHolder> {
        private List<String> mDirPaths;
        public DirListAdapter(List<String> dirPaths) {
            mDirPaths = dirPaths;
        }

        @Override
        public DirListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(HomeScreenActivity.this);
            View view = layoutInflater.inflate(R.layout.list_item, parent, false);
            return new DirListHolder(view);
        }

        @Override
        public void onBindViewHolder(DirListHolder holder, int position) {
            String dirPath = mDirPaths.get(position);
            holder.folder_tag.setText(dirPath);
        }

        @Override
        public int getItemCount() {
            return mDirPaths.size();
        }
    }

    private class DirListHolder extends RecyclerView.ViewHolder
    {
        public TextView folder_tag;
        public ImageButton sync_button;
        public ImageButton delete_button;

        public DirListHolder(View itemView) {
            super(itemView);
            folder_tag = (TextView)itemView.findViewById(R.id.folder_tag);
            sync_button = (ImageButton)itemView.findViewById(R.id.sync_button);
            delete_button = (ImageButton)itemView.findViewById(R.id.delete_button);
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}