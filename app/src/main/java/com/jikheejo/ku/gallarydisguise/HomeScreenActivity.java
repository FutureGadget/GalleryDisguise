package com.jikheejo.ku.gallarydisguise;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toast;

import com.jikheejo.ku.gallarydisguise.Encryption.GenerateKey;
import com.jikheejo.ku.gallarydisguise.Encryption.LFSR;
import com.jikheejo.ku.gallarydisguise.Encryption.Preprocessing;
import com.jikheejo.ku.gallarydisguise.jsonutils.JsonUtils;
import com.jikheejo.ku.gallarydisguise.picpath.BackButtonPress;
import com.jikheejo.ku.gallarydisguise.picpath.PhotoPath;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        mDirRecyclerView = (RecyclerView) findViewById(R.id.dirRecyclerView);
        mDirRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateUI();

        //back button setting
        backButtonPress = new BackButtonPress(this);

        //toggle button setting
        final SharedPreferences setting = getSharedPreferences("setting", 0);
        final SharedPreferences.Editor editor = setting.edit();
        boolean run = setting.getBoolean("fake", false);


        final Switch tb = (Switch) this.findViewById(R.id.app_Disguise);
        tb.setChecked(run);
        tb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (tb.isChecked()) {
                    editor.putBoolean("fake", true);
                } else {
                    editor.putBoolean("fake", false);
                }
                editor.commit();
            }
        });

        // add button behavior
        Button addButton = (Button) this.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeScreenActivity.this, DirectoryListActivity.class);
                startActivity(i);
            }
        });

        // decrypt buttons behavior
//        Button decryptButton = (Button)this.findViewById(R.id.decryptButton);
//        decryptButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenActivity.this);
//                builder.setTitle("Decryption")
//                        .setItems(mSelectedPaths.toArray(new CharSequence[mSelectedPaths.size()]), new DialogInterface.OnClickListener(){
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Ok button behavior
//                        decrypt();
//                        updateUI();
//                    }
//                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Cancel button Behavior
//                    }
//                });
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    /**
     * Selected paths contains a set of directories that the user wants to decrypt.
     * For each paths, this method decrypts the files in it and moves it to the original directory.
     */
    private void decrypt(final String path) {
        try {
            JSONArray dirArray = JsonUtils.getDirJSONArray(getFilesDir() + "/trans.json");
            String outDirPath = getOutDirPath(path);    // get the directory that contains encrypted files
            File inDir = new File(outDirPath);  // open the directory
            for (File encryptedFile : inDir.listFiles()) {  // for each encrypted file
                final String decryptedFileName = Preprocessing.fileName_Parse(encryptedFile.getName(), 0);  // base64 decoding
                File outFile = new File(path + "/" + decryptedFileName);    // open file to write decrypted contents

                OutputStream out = new FileOutputStream(outFile);   // open output stream
                String key = GenerateKey.key_generate(getSharedPreferences("setting", 0).getString("key", "")); // get key
                byte[] bytes = LFSR.transform(Preprocessing.byteRead(encryptedFile), key, 8);   // decrypt
                out.write(bytes);
                out.close();
                encryptedFile.delete();
            }
            inDir.delete();  // delete encrypted directory (already decrypted)

            // remove decrypted directory entry from the directory JSONArray
            JsonUtils.removeDirEntry(dirArray, path);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("List", dirArray);
            JsonUtils.updateJSONObject(openFileOutput("trans.json", MODE_PRIVATE), jsonObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Output Directory path given an original path.
     * Search through the "trans.json" file.
     * EX) "original_path : /dcim/camera", "out_path : /data/data/gallery/tag"
     *
     * @return a directory path which contains encrypted files.
     */
    private String getOutDirPath(String path) {
        try {
            JSONObject jsonObj;
            JSONArray jsonArray = JsonUtils.getDirJSONArray(getFilesDir() + "/trans.json");
            for (int i = 0; i < jsonArray.length(); ++i) {
                jsonObj = jsonArray.getJSONObject(i);
                String oriPath = jsonObj.getString("original_path");
                if (path.equals(oriPath)) {
                    return jsonObj.getString("out_path");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //back button run
    @Override
    public void onBackPressed() {
        backButtonPress.onBackPressed();
    }

    // update list
    private void updateUI() {
        List<String> dirPaths = new ArrayList<>();
        JSONArray dirInfoArray = JsonUtils.getDirJSONArray(getFilesDir() + "/trans.json");
        JSONObject object;
        try {
            for (int i = 0; i < dirInfoArray.length(); ++i) {
                object = dirInfoArray.getJSONObject(i);
                dirPaths.add(object.getString("original_path"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        public void onBindViewHolder(final DirListHolder holder, int position) {
            final String dirPath = mDirPaths.get(position);
            holder.bind(dirPath);
        }

        @Override
        public int getItemCount() {
            return mDirPaths.size();
        }
    }

    private class DirListHolder extends RecyclerView.ViewHolder {
        public TextView mPathTextView;
        public ImageButton mDeleteButton;
        public ImageButton mSyncButton;
        public String mPath;

        public DirListHolder(View itemView) {
            super(itemView);
            mPathTextView = (TextView) itemView.findViewById(R.id.list_encrypted_dir_text_view);
            mDeleteButton = (ImageButton) itemView.findViewById(R.id.list_encrypted_dir_delete_button);
            mSyncButton = (ImageButton) itemView.findViewById(R.id.list_encrypted_dir_sync_button);
        }

        public void bind(String dirPath) {
            mPathTextView.setText(dirPath.substring(dirPath.lastIndexOf('/')+1, dirPath.length()));
            mPath = dirPath;
            final String path = dirPath;
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decrypt(path);
                    updateUI();
                }
            });
            mSyncButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}