package com.jikheejo.ku.gallarydisguise.Encryption;


import android.content.Context;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class Preprocessing {
    // image to byte style
    public static byte[] byteRead(File file) throws IOException{
        byte[] img = new byte[(int)file.length()];
        InputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(img);
        return img;
    }

    public static String fileName_Parse(String fileName, int select){
        if(select == 1)
            return fileName_Hide(fileName);
        else
            return fileName_Unhide(fileName);
    }

    // Hiding original file name
    public static String fileName_Hide(String str){
        // 원본 filename 을 숨김.
        byte[] encode = str.getBytes();
        return Base64.encodeToString(encode, Base64.URL_SAFE);
    }

    public static String fileName_Unhide(String str){
        byte[] decode = Base64.decode(str, Base64.URL_SAFE);
        return new String(decode);
    }
}
