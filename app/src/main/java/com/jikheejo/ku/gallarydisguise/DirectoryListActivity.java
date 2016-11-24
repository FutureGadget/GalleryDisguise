package com.jikheejo.ku.gallarydisguise;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jikheejo.ku.gallarydisguise.Encryption.GenerateKey;
import com.jikheejo.ku.gallarydisguise.Encryption.LFSR;
import com.jikheejo.ku.gallarydisguise.Encryption.Preprocessing;
import com.jikheejo.ku.gallarydisguise.jsonutils.JsonUtils;
import com.jikheejo.ku.gallarydisguise.picpath.PhotoPath;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DirectoryListActivity extends AppCompatActivity {
    private RecyclerView mDirRecyclerView;
    private DirListAdapter mAdapter;
    private Set<String> mSelectedPaths;
    private Button mDirEncryptButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_list);

        //list setting
        mDirRecyclerView = (RecyclerView)findViewById(R.id.dirListRecyclerView);
        mDirRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateUI();

        // Button setting
        mDirEncryptButton = (Button)findViewById(R.id.dirEncryptButton);
        mDirEncryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DirectoryListActivity.this);
                builder.setView(R.layout.encrypt_dialog)
                        .setTitle("Encryption")
                        .setItems(mSelectedPaths.toArray(new CharSequence[mSelectedPaths.size()]), new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Ok button behavior
                        TextView tv = (TextView)((AlertDialog)dialog).findViewById(R.id.tag_edit_text);
                        String tag;
                        if (!((tag = tv.getText().toString()).equals(""))) {
                            encryptAndSaveFiles(tag); // Encrypt selected directories
                            updateUI();
                        }
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel button Behavior
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        // init select array
        mSelectedPaths = new HashSet<>();
    }

    /**
     * Encrypt selected directories.
     * Currently, a user input tag is used to create a new directory in which the encrypted files
     * @param tag use this tag to download photos from the server.
     */
    private void encryptAndSaveFiles(String tag) {
        if (!mSelectedPaths.isEmpty()) {
            JSONArray objArray;
            JSONObject obj, tmp;
            Set<String> removed = new HashSet<>();
            try {
                objArray = JsonUtils.getDirJSONArray(getFilesDir()+"/trans.json");
                for (String path : mSelectedPaths) {
                    removed.add(path);
                    tmp = new JSONObject(); // To record original directory path
                    File file = new File(path);

                    final String dirPath = getFilesDir() + "/" + tag;
                    File newDir = new File(dirPath);
                    newDir.mkdir();

                    JSONArray serverFiles = new JSONArray();
                    for (File rawFile :file.listFiles()) {
                        // must be declared in final
                        final String filename = Base64.encodeToString(rawFile.getName().getBytes(), Base64.URL_SAFE|Base64.NO_WRAP);


                        File outFile = new File(dirPath + "/" + filename);
                        FileOutputStream out = new FileOutputStream(outFile);

                        String key = GenerateKey.key_generate(getSharedPreferences("setting", 0).getString("key", ""));
                        byte[] bytes = LFSR.transform(Preprocessing.byteRead(rawFile), key, 8); // key, tab, revised.
                        out.write(bytes);
                        out.close();
                        rawFile.delete();
                        // record images files downloaded from server.
                        // This information is needed for synchronization function.
                    }
                    // TEST ONLY
                    // these files will be excluded (because these are already encrypted files)
                    // when synchronizing a directory.
                    serverFiles.put("server1.jpg");
                    serverFiles.put("server2.jpg");

                    /**
                     * Add a new entry. (Newly encrypted directory information)
                     * Record the following:
                     * 1. Original dir path
                     * 2. Encrypted dir path
                     * 3. An array of file names downloaded from the server
                     */
                    tmp.put("original_path", path);
                    tmp.put("out_path", getFilesDir() + "/" + tag);
                    tmp.put("files", serverFiles);
                    objArray.put(tmp);
                }
                // Remove Processed path from the set
                for (String rm : removed) {
                    mSelectedPaths.remove(rm);
                }
                obj = new JSONObject();
                obj.put("List", objArray);
                JsonUtils.updateJSONObject(openFileOutput("trans.json", MODE_PRIVATE), obj);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUI() {
        JSONArray dirArray = JsonUtils.getDirJSONArray(getFilesDir()+"/trans.json");
        List<String> dirPaths = PhotoPath.getLeafPhotoDirs();
        try {
            // Remove directories which have already been encrypted from the list.
            for (int i = 0; i < dirArray.length(); ++i) {
                JSONObject obj = dirArray.getJSONObject(i);
                dirPaths.remove(obj.getString("original_path"));
            }
        } catch(Exception e) {
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
            LayoutInflater layoutInflater = LayoutInflater.from(DirectoryListActivity.this);
            View view = layoutInflater.inflate(R.layout.list_item_dir, parent, false);
            return new DirListHolder(view);
        }

        @Override
        public void onBindViewHolder(final DirListHolder holder, final int position) {
            final String dirPath = mDirPaths.get(position);
            // Since recycler view reuses same objects, this code is needed to avoid wrongly checked objects.
            holder.mPathTextView.setText(dirPath);
            if (mSelectedPaths.contains(dirPath)) {
                holder.mSelectCheckBox.setChecked(true);
            } else {
                holder.mSelectCheckBox.setChecked(false);
            }
            holder.mSelectCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!holder.mSelectCheckBox.isChecked()) {
                        mSelectedPaths.remove(dirPath);
                    } else {
                        mSelectedPaths.add(dirPath);
                        Log.i("TEST", dirPath);
                    }
                    // Log.i("TEST", holder.getAdapterPosition()+"");
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDirPaths.size();
        }
    }

    private static class DirListHolder extends RecyclerView.ViewHolder
    {
        public TextView mPathTextView;
        public CheckBox mSelectCheckBox;

        public DirListHolder(View itemView) {
            super(itemView);
            mPathTextView = (TextView)itemView.findViewById(R.id.list_item_dir_text_view2);
            mSelectCheckBox = (CheckBox)itemView.findViewById(R.id.list_item_dir_selected_checkbox);
        }
    }
}
