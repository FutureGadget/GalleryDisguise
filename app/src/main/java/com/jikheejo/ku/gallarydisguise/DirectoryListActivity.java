package com.jikheejo.ku.gallarydisguise;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.jikheejo.ku.gallarydisguise.Encryption.GenerateKey;
import com.jikheejo.ku.gallarydisguise.Encryption.LFSR;
import com.jikheejo.ku.gallarydisguise.Encryption.Preprocessing;
import com.jikheejo.ku.gallarydisguise.jsonutils.JsonUtils;
import com.jikheejo.ku.gallarydisguise.picpath.PhotoPath;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.jikheejo.ku.gallarydisguise.HomeScreenActivity.RESULT_CLOSE_ALL;
import static com.jikheejo.ku.gallarydisguise.HomeScreenActivity.RESULT_PASS;

public class DirectoryListActivity extends AppCompatActivity {
    private RecyclerView mDirRecyclerView;
    private DirListAdapter mAdapter;
    private Set<String> mSelectedPaths;
    private Button mDirEncryptButton;
    private CharSequence[] mItems = {"cat", "fishing", "planking"};
    private Map<String, Integer> numServerFiles = new HashMap<>();
    private final int MY_PERMISSIONS_READ_WRITE_EXTERNAL = 1;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private boolean FAKE_PASS_STATE = false;
    private boolean ADD_BUTTON_PRESSED_STATE = false;

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences settings = getSharedPreferences("setting", 0);
        if (settings.getBoolean("fake", false)) {
            if (!FAKE_PASS_STATE && !ADD_BUTTON_PRESSED_STATE) {
                Intent fakeHome = new Intent(DirectoryListActivity.this, FakeScreenActivity.class);
                startActivityForResult(fakeHome, 0);
            }
        }
        ADD_BUTTON_PRESSED_STATE = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("TEST", "TESTEST");
        FAKE_PASS_STATE = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode)
        {
            case RESULT_CLOSE_ALL:
                setResult(RESULT_CLOSE_ALL);
                finish();
                break;
            case RESULT_PASS:
                setResult(RESULT_PASS);
                FAKE_PASS_STATE = true;
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_PASS);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_list);

        // Came to this activity by pressing "Add" button
        Bundle extras = getIntent().getExtras();
        ADD_BUTTON_PRESSED_STATE = extras.getBoolean("ADD_BUTTON_PRESSED", false);

        // init server file number
        numServerFiles.put("cat", 30);
        numServerFiles.put("fishing", 10);
        numServerFiles.put("planking", 10);

        // Request permission
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(DirectoryListActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(DirectoryListActivity.this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(DirectoryListActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_READ_WRITE_EXTERNAL);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        //서버 이미지 setting
        BitmapFactory.Options bmOptions;
        bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        //list setting
        mDirRecyclerView = (RecyclerView)findViewById(R.id.dirListRecyclerView);
        mDirRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Button setting
        mDirEncryptButton = (Button)findViewById(R.id.dirEncryptButton);
        mDirEncryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DirectoryListActivity.this);
                builder.setTitle("Encryption")
                        .setItems(mItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int totalNumFiles = 0;
                                for (String path : mSelectedPaths) {
                                    totalNumFiles += new File(path).listFiles().length;
                                }
                                new EncryptionProcess().execute(mItems[which].toString(), totalNumFiles);
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        // init select array
        mSelectedPaths = new HashSet<>();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_READ_WRITE_EXTERNAL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    updateUI();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private class EncryptionProcess extends AsyncTask<Object, String, Integer> {
        private ProgressDialog mPdialog;
        @Override
        protected void onPreExecute() {
            mPdialog = new ProgressDialog(DirectoryListActivity.this);
            mPdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mPdialog.setMessage("Encrypting...");
            mPdialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer result) {
            Toast.makeText(DirectoryListActivity.this, Integer.toString(result) + " are encrypted.",
                    Toast.LENGTH_SHORT).show();
            mPdialog.dismiss();
            onBackPressed();
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            if (progress[0].equals("progress")) {
                mPdialog.setProgress(Integer.parseInt(progress[1]));
                mPdialog.setMessage(progress[2]);
            } else if (progress[0].equals("max")) {
                mPdialog.setMax(Integer.parseInt(progress[1]));
            }
            super.onProgressUpdate(progress);
        }

        @Override
        protected Integer doInBackground(Object... params) {
            String tag = (String)params[0];
            String[] projection = { MediaStore.Images.Media._ID };
            String selection = MediaStore.Images.Media.DATA + " = ?";

            final int taskCnt = (Integer)params[1];
            int progressCnt = 0;
            publishProgress("max", Integer.toString(taskCnt));

            JSONObject originalJSON = JsonUtils.readJSONObject(getFilesDir()+"/trans.json");

            if (!mSelectedPaths.isEmpty()) {
                JSONArray objArray;
                JSONObject obj;
                Set<String> removed = new HashSet<>();
                setting = getSharedPreferences("setting", 0);

                try {
                    String imgUrl = "https://s3.ap-northeast-2.amazonaws.com/jickheejo/";
                    objArray = JsonUtils.getDirJSONArray(getFilesDir()+"/trans.json");
                    JSONArray serverFiles = new JSONArray();
                    for (String path : mSelectedPaths) {
                        removed.add(path);
                        File file = new File(path);

                        //String tagfoldernum = tag + "foldernum";
                        String tagusingimgnum = tag + "usingimgnum";
                        // setting.getInt(tagfoldernum, 0);

                        int originalfilecount = setting.getInt(tagusingimgnum, 0);
                        int updateimgcount = 0;

                        // must be declared in final
                        final String dirPath = getFilesDir() + "/" + file.getName() + tag;
                        File newDir = new File(dirPath);
                        newDir.mkdir();

                        for (File rawFile :file.listFiles()) {
                            updateimgcount++;
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

                            /**
                             * Remove files from contents resolver.
                             * This method will update the gallery automatically.
                             */
                            String[] selectionArgs = new String[] { rawFile.getAbsolutePath() };
                            Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            ContentResolver contentResolver = getContentResolver();
                            Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
                            if (c.moveToFirst()) {
                                // We found the ID. Deleting the item via the content provider will also remove the file
                                long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                                Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                                contentResolver.delete(deleteUri, null, null);
                            } else {
                                // File not found in media store DB
                            }
                            c.close();

                            progressCnt++;
                            publishProgress("progress", Integer.toString(progressCnt),
                                    "("+Integer.toString(progressCnt)+"/"+Integer.toString(taskCnt)+")");
                        }

                        downloadAndSaveImage(imgUrl, tag, originalfilecount, updateimgcount);
                        publishProgress("progress", Integer.toString(progressCnt), "Downloading images...");

                        // TEST ONLY
                        // these files will be excluded (because these are already encrypted files)
                        // when synchronizing a directory.
                        for (int i = 1; i <= updateimgcount; ++i) {
                            int tmpi = i + originalfilecount;
                            serverFiles.put(tmpi + ".jpg");
                        }

                        updateimgcount = updateimgcount+originalfilecount;
                        editor = setting.edit();
                        editor.putInt(tagusingimgnum, updateimgcount);
                        editor.commit();

                        /**
                         * Add a new entry. (Newly encrypted directory information)
                         * Record the following:
                         * 1. Original dir path
                         * 2. Encrypted dir path
                         * 3. Tag name
                         */
                        JSONObject popped = JsonUtils.jsonPopFromArray(path, objArray);
                        popped.put("original_path", path);
                        popped.put("out_path", getFilesDir() + "/" + file.getName() + tag);
                        popped.put("tag", tag);
                        objArray.put(popped);
                    }
                    // Remove Processed path from the set
                    for (String rm : removed) {
                        mSelectedPaths.remove(rm);
                    }

                    obj = new JSONObject();
                    obj.put("List", objArray);

                    // add downloaded fake files to the existing array or create a new list for the tag.
                    JSONArray tagFakeFiles = JsonUtils.getTagFakeFileArray(getFilesDir()+"/trans.json", tag);
                    for (int i = 0; i < serverFiles.length(); ++i) {
                        tagFakeFiles.put(serverFiles.getString(i));
                    }
                    obj.put(tag, tagFakeFiles);

                    // recover original tags
                    if (originalJSON != null) {
                        Iterator<String> keys = originalJSON.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            if (!key.equals("List") && !key.equals(tag)) {
                                JSONArray tagJsonObject = originalJSON.getJSONArray(key);
                                obj.put(key, tagJsonObject);
                            }
                        }
                    }
                    // write out to json file.
                    JsonUtils.updateJSONObject(openFileOutput("trans.json", MODE_PRIVATE), obj);
                    return taskCnt;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }
    }

    private void downloadAndSaveImage(String url, String tagname, int orifico, int numFIles) {
        Bitmap mBitmap = null;
        InputStream in = null;
        for(int i = 0; i < numFIles; i++){
            int tmi = (i + orifico)%(numServerFiles.get(tagname)) + 1;
            String tmpurl = url + tagname+"/"+tmi+".jpg";
            try {
                in = new java.net.URL(tmpurl).openStream();
                mBitmap = BitmapFactory.decodeStream(in);
                ImgSaver(tagname, i + orifico + 1, mBitmap);
                in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void ImgSaver(String tagname, int filename, Bitmap bmimg) {
        OutputStream outputStream = null;
        String fpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                .getAbsolutePath()+"/"+tagname;

        //파일 경로 생성
        String sdcard = Environment.getExternalStorageState();
        File file = null;
        if (!sdcard.equals(Environment.MEDIA_MOUNTED)) {
            // SD카드가 마운트되어있지 않음
            file = Environment.getRootDirectory();
        } else {
            // SD카드가 마운트되어있음
            file = Environment.getExternalStorageDirectory();
        }

        file = new File(fpath);
        if ( !file.exists() ) {
            // 디렉토리가 존재하지 않으면 디렉토리 생성
            file.mkdirs();
        }

        String fn = filename+".jpg";
        File fil = new File(fpath, fn);

        try{
            Log.i("LSJ", "File check:" + fil.exists());
            if(fil.exists() == false){
                outputStream = new FileOutputStream(fil);
                bmimg.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                Log.i("LSJ", "File check:" + "같은 이름 없음");
            } else {
                int j = 0;
                while (fil.exists() == true) {
                    j++;
                    fn = filename + "(" + j + ").jpg";
                    fil = new File(fpath, fn);
                }
                outputStream = new FileOutputStream(fil);
                bmimg.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                Log.i("LSJ", "File check:" + "파일 중복으로 다른 이름 저장");
            }
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fil)));
        } catch(FileNotFoundException e){
            e.printStackTrace();
            Toast.makeText(DirectoryListActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(DirectoryListActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Update Recycler view items. (List items)
     */
    private void updateUI() {
        Set<String> exceptionDirNames = new HashSet<>();
        try {
            JSONArray dirArray = JsonUtils.getDirJSONArray(getFilesDir()+"/trans.json");
            // add exception dir names
            for (int i = 0; i < dirArray.length(); ++i) {
                exceptionDirNames.add(dirArray.getJSONObject(i).getString("tag"));  // 'tag' named directories (ex) cat, trap ...
                exceptionDirNames.add(dirArray.getJSONObject(i).getString("original_path")); // disguised directories
            }
        } catch (Exception e) { e.printStackTrace(); }
        List<String> dirPathsDCIM = PhotoPath.getLeafPhotoDirs(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), exceptionDirNames);
        List<String> dirPathsPICS = PhotoPath.getLeafPhotoDirs(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), null);
        List<String> finalPath = new ArrayList<>();
        for (String s : dirPathsDCIM) {
            if (!exceptionDirNames.contains(s)) {
                finalPath.add(s);
            }
        }
        for (String s : dirPathsPICS) {
            if (!exceptionDirNames.contains(s)) {
                finalPath.add(s);
            }
        }

        mAdapter = new DirListAdapter(finalPath);
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
                    }
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
