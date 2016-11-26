package com.jikheejo.ku.gallarydisguise.Encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class GenerateKey {
    // Image Encryption Key
    public static String key_generate(String randkey){
        String key = "";
        char[] tmp = null;
        tmp = Cipher(randkey.toCharArray());
        for(int i=0; i<tmp.length; i++){
            key += Long.toBinaryString(tmp[i]);
        }
        return key;
    }

    // making random String
    public static String randKey(){
        String id = UUID.randomUUID().toString();
        return id.replaceAll("-", "");
    }

    public static char[] Cipher(char[] tmp){
        int j = tmp.length-1;
        int k = 0;
        char[] result = new char[8];
        for(int i=0; i<8; i+=2){
            result[i] = tmp[k];
            result[i+1] = tmp[j];
            k++;
            j--;
        }
        return result;
    }

    // SHA-256 Encryption
    public static String encryption(String str){
        String sha256 = " ";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(str.getBytes());
            byte data[] = md.digest();
            StringBuffer sb = new StringBuffer();
            for(int i=0; i<data.length; i++){
                sb.append(Integer.toString((data[i]&0xff)+0x100,16).substring(1));
            }
            sha256 = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            sha256 = "";
        }
        return sha256;
    }
}