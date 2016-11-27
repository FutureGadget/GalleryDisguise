package com.jikheejo.ku.gallarydisguise;

import android.Manifest;
import android.app.Activity;
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
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import static android.content.Intent.FLAG_ACTIVITY_NO_USER_ACTION;

public class HomeScreenActivity extends AppCompatActivity {
    private RecyclerView mDirRecyclerView;
    private DirListAdapter mAdapter;
    private BackButtonPress backButtonPress;
    private final int MY_PERMISSIONS_READ_WRITE_EXTERNAL = 1;
    private boolean homeButtonPressed;
    public static final int RESULT_CLOSE_ALL = 1;

    @Override
    public void onResume() {
        homeButtonPressed = false;
        updateUI();
        super.onResume();
    }

    @Override
    public void onUserLeaveHint() {
        homeButtonPressed = true;
        super.onUserLeaveHint();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (homeButtonPressed) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        //list setting
        mDirRecyclerView = (RecyclerView) findViewById(R.id.dirRecyclerView);
        mDirRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Request permission
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(HomeScreenActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeScreenActivity.this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(HomeScreenActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_READ_WRITE_EXTERNAL);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

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

        // Get password from user (Just for one time only)
        boolean hasPassword = setting.getBoolean("hasPassword", false);
        if (!hasPassword) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenActivity.this);
            final EditText input = new EditText(HomeScreenActivity.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            builder.setTitle("Password Setting")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String text = input.getText().toString();
                            if (!text.equals("")) {
                                editor.putString("password", GenerateKey.encryption(text)); // sha256 hashing
                                editor.putBoolean("hasPassword", true);
                                editor.commit();
                            }
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        // add button behavior
        Button addButton = (Button) this.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeScreenActivity.this, DirectoryListActivity.class);
                i.addFlags(FLAG_ACTIVITY_NO_USER_ACTION); // Prevents activity lifecycle from calling onUserLeaveHint()
                startActivityForResult(i, 0); // Prepares to close this activity on Home button pressed from the DirectoryListActivity
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode)
        {
            case RESULT_CLOSE_ALL:
                setResult(RESULT_CLOSE_ALL);
                finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    /**
     * Selected paths contains a set of directories that the user wants to decrypt.
     * For each paths, this method decrypts the files in it and moves it to the original directory.
     */
    private class DecryptProcess extends AsyncTask<Object, String, Integer> {
        private ProgressDialog mPdialog;
        @Override
        protected void onPreExecute() {
            mPdialog = new ProgressDialog(HomeScreenActivity.this);
            mPdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mPdialog.setMessage("Decrypting...");
            mPdialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer result) {
            Toast.makeText(HomeScreenActivity.this, Integer.toString(result) + " are decrypted.",
                    Toast.LENGTH_SHORT).show();
            updateUI();
            mPdialog.dismiss();
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
            String path = (String)params[0];
            SharedPreferences setting = getSharedPreferences("setting", 0);
            String[] projection = { MediaStore.Images.Media._ID };
            String selection = MediaStore.Images.Media.DATA + " = ?";
            try {
                JSONObject jsonObject = JsonUtils.readJSONObject(getFilesDir()+"/trans.json");
                JSONArray dirArray = jsonObject.getJSONArray("List");
                JSONObject dirEntryObject = JsonUtils.jsonPopFromArray(path, dirArray);
                String tag = dirEntryObject.getString("tag");
                String tagDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/" + tag;
                String outDirPath = getOutDirPath(path);    // get the directory that contains encrypted files
                File inDir = new File(outDirPath);          // open the directory

                // init progress bar
                final int taskCnt = inDir.listFiles().length;
                int progressCnt = 0;
                publishProgress("max", Integer.toString(taskCnt));

                // array of file names downloaded from the image server.(fake files created in the encryption process.)
                JSONArray fakeFilesArray = jsonObject.getJSONArray(tag);

                /**
                 * Remove files downloaded from the server using ContentResolver.
                 * This method will update the gallery automatically.
                 */
            Set<String> removed = new HashSet<>();
                for (int i = 0; i < inDir.listFiles().length; ++i) {
                    String fakeFileAbsPath = tagDirPath + "/" + fakeFilesArray.getString(i);
                    String[] selectionArgs = new String[] { fakeFileAbsPath };
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
                    removed.add(fakeFilesArray.getString(i));

                    // update progress
                    progressCnt++;
                    publishProgress("progress", Integer.toString(progressCnt),
                            "("+Integer.toString(progressCnt)+"/"+Integer.toString(taskCnt)+")");
                }

                // recreate JSON array based on the deleted fake files.
                JSONArray newArray = new JSONArray();
                for (int i = 0; i < fakeFilesArray.length(); ++i) {
                    if (!removed.contains(fakeFilesArray.getString(i))) {
                        newArray.put(fakeFilesArray.getString(i));
                    }
                }

                // if all fake files are deleted
                if (newArray.length() == 0) {
                    // reset tag file number counter
                    // should be reset only when the  becomes zero.
                    SharedPreferences.Editor editor = setting.edit();
                    editor.remove(tag+"usingimgnum");
                    editor.commit();

                    // and if the fake directory(tag directory) is empty
                    File fakeFilesDir = new File(tagDirPath);
                    if (fakeFilesDir.listFiles().length == 0) {
                        fakeFilesDir.delete();
                    }
                }

                for (File encryptedFile : inDir.listFiles()) {  // for each encrypted file
                    final String decryptedFileName = Preprocessing.fileName_Parse(encryptedFile.getName(), 0);  // base64 decoding
                    File outFile = new File(path + "/" + decryptedFileName);    // open file to write decrypted contents

                    OutputStream out = new FileOutputStream(outFile);   // open output stream
                    String key = GenerateKey.key_generate(getSharedPreferences("setting", 0).getString("key", "")); // get key
                    byte[] bytes = LFSR.transform(Preprocessing.byteRead(encryptedFile), key, 8);   // decrypt
                    out.write(bytes);
                    out.close();
                    encryptedFile.delete();
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + outFile.getAbsolutePath())));
                }
                inDir.delete();  // delete encrypted directory (already decrypted)

                // JsonUtils.removeDirEntry(dirArray, path);
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("List", dirArray);  // Since the decrypted directory entry is already removed when calling "jsonPopFromArray()", no need to update it here.
                if (newArray.length() != 0) {
                    jsonObj.put(tag, newArray);
                }
                JsonUtils.updateJSONObject(openFileOutput("trans.json", MODE_PRIVATE), jsonObj);

                return taskCnt;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
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

    // Verify user before executing decryption.
    private boolean verify(String inputPassword) {
        SharedPreferences setting = getSharedPreferences("setting", 0);
        String sha256pass = setting.getString("password", "");
        if (sha256pass.equals(GenerateKey.encryption(inputPassword))) {
            return true;
        }
        return false;
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

    /**
     * Synchronize unencrypted files in the disguised folder.
     */
    private class SyncProcess extends AsyncTask<Object, String, Integer> {
        private ProgressDialog mPdialog;
        @Override
        protected void onPreExecute() {
            mPdialog = new ProgressDialog(HomeScreenActivity.this);
            mPdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mPdialog.setMessage("Encrypting...");
            mPdialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer result) {
            Toast.makeText(HomeScreenActivity.this, Integer.toString(result) + " are encrypted.",
                    Toast.LENGTH_SHORT).show();
            updateUI();
            mPdialog.dismiss();
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
            final int taskCnt = (Integer)params[2];
            int progressCnt = 0;
            publishProgress("max", Integer.toString(taskCnt));

            String path = (String)params[0];
            String tag = (String)params[1];

            String[] projection = { MediaStore.Images.Media._ID };
            String selection = MediaStore.Images.Media.DATA + " = ?";
            JSONArray objArray;
            JSONObject obj;
            SharedPreferences setting = getSharedPreferences("setting", 0);

            try {
                String imgUrl = "https://s3.ap-northeast-2.amazonaws.com/jickheejo/";
                objArray = JsonUtils.getDirJSONArray(getFilesDir()+"/trans.json");
                JSONArray serverFiles = new JSONArray();

                File file = new File(path);

                String tagusingimgnum = tag + "usingimgnum";

                int originalfilecount = setting.getInt(tagusingimgnum, 0);
                int updateimgcount = 0;

                // must be declared in final
                final String dirPath = getFilesDir() + "/" + file.getName() + tag;
                // File newDir = new File(dirPath);
                // newDir.mkdir();

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

                    // Update progress
                    ++progressCnt;
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
                SharedPreferences.Editor editor = setting.edit();
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

                obj = new JSONObject();
                obj.put("List", objArray);

                // add downloaded fake files to the existing array or create a new list for the tag.
                JSONArray tagFakeFiles = JsonUtils.getTagFakeFileArray(getFilesDir()+"/trans.json", tag);
                for (int i = 0; i < serverFiles.length(); ++i) {
                    tagFakeFiles.put(serverFiles.getString(i));
                }
                obj.put(tag, tagFakeFiles);

                // write out to json file.
                JsonUtils.updateJSONObject(openFileOutput("trans.json", MODE_PRIVATE), obj);
                return taskCnt;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    //서버에서 이미지 다운
    private void downloadAndSaveImage(String url, String tagname, int orifico, int numFIles) {
        Bitmap mBitmap = null;
        InputStream in = null;
        for(int i = 0; i < numFIles; i++){
            int tmi = (i + orifico)%30 + 1;
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
            Toast.makeText(HomeScreenActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(HomeScreenActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private class DirListHolder extends RecyclerView.ViewHolder {
        public TextView mPathTextView;
        public ImageButton mDeleteButton;
        public ImageButton mSyncButton;
        public String mPath;
        public String mTag;

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenActivity.this);
                    builder.setTitle("Password protected");
                    // Set up the input
                    final EditText input = new EditText(HomeScreenActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String inputPassword = input.getText().toString();
                            if(verify(inputPassword)) {
//                                decrypt(path);
//                                updateUI();
                                new DecryptProcess().execute(path);
                            } else {
                                Toast.makeText(HomeScreenActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            });
            if (new File(mPath).listFiles().length != 0) {
                if (mTag == null) {
                    try {
                        JSONArray jsonArray = JsonUtils.getDirJSONArray(getFilesDir() + "/trans.json");
                        JSONObject dirEntry;
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            dirEntry = jsonArray.getJSONObject(i);
                            if (dirEntry.getString("original_path").equals(mPath)) {
                                mTag = dirEntry.getString("tag");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                mSyncButton.setVisibility(View.VISIBLE);
                mSyncButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SyncProcess().execute(mPath, mTag, new File(mPath).listFiles().length);
                    }
                });
            } else {
                mSyncButton.setVisibility(View.INVISIBLE);
            }
        }
    }
}