package com.jikheejo.ku.gallarydisguise;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class temp extends AppCompatActivity {

    String imgUrl = "https://s3.ap-northeast-2.amazonaws.com/jickheejo/cat/01.jpg";
    Bitmap bmImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        ImageView bmImage = (ImageView) findViewById(R.id.imageView);
        Button bb = (Button) findViewById(R.id.button);
        bb.setOnClickListener(btnSaveOnClickListener);

        BitmapFactory.Options bmOptions;
        bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        OpenHttpConnection openHttpConnection = new OpenHttpConnection();
        openHttpConnection.execute(bmImage, imgUrl);
    }

    Button.OnClickListener btnSaveOnClickListener = new Button.OnClickListener(){
        public void onClick(View arg0){
            OutputStream outputStream = null;
            String extStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
            String fpath = extStorageDirectory + "/DCIM/Camera";

            File file1 = new File(fpath, "123.jpg");
            File file2 = new File(fpath, "456.jpg");

            try{
                Log.i("LSJ", "File check:" + file1.exists());
                if(file1.exists() == false){
                    outputStream = new FileOutputStream(file1);
                    bmImg.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    Toast.makeText(temp.this, "저장완료", Toast.LENGTH_LONG).show();

                    Log.i("LSJ", "File check:" + "같은 이름 없음");
                } else if(file1.exists() == true){
                    outputStream = new FileOutputStream(file2);
                    bmImg.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    Toast.makeText(temp.this, "파일이 중복되어 다른이름으로 변경", Toast.LENGTH_LONG).show();
                    Log.i("LSJ", "File check:" + "파일 중복으로 다른 이름 저장");
                }
            } catch(FileNotFoundException e){
                e.printStackTrace();
                Toast.makeText(temp.this, e.toString(), Toast.LENGTH_LONG).show();
            } catch (IOException e){
                e.printStackTrace();
                Toast.makeText(temp.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    };

    private class OpenHttpConnection extends AsyncTask<Object,Void, Bitmap> {

        private  ImageView bmpimage;

        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap mBitmap = null;
            bmpimage = (ImageView) params[0];
            String url = (String) params[1];
            InputStream in = null;
            try {
                in = new java.net.URL(url).openStream();
                mBitmap = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return mBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            super.onPostExecute(bm);
            bmImg = bm;
            bmpimage.setImageBitmap(bm);
        }
    }
}
