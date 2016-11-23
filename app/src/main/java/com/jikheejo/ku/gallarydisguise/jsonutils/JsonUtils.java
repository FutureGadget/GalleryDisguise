package com.jikheejo.ku.gallarydisguise.jsonutils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by dw on 2016-11-24.
 */

public class JsonUtils {
    /**
     * Parsing a json file with the given path and returns a json object.
     * Returns null if the JSON file does not exist or is empty.
     * @param path JSON file path
     * @return a parsed JSONObject
     */
    public static JSONObject readJSONObject (String path) {
        try {
            File jsonFile = new File(path);
            if (!jsonFile.exists() || jsonFile.length() == 0) {
                return null;
            }
            InputStream jsonStream = new FileInputStream(jsonFile);
            byte[] inBytes = new byte[(int)jsonFile.length()];
            jsonStream.read(inBytes);
            jsonStream.close();
            return new JSONObject(new String(inBytes));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get JSON Array object of a directory array.
     * @return JSONArray of a directory array.
     */
    public static JSONArray getDirJSONArray (String path) {
        try {
            JSONObject obj = readJSONObject(path);
            if (obj == null) {
                return new JSONArray();
            } else {
                return obj.getJSONArray("List");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Removes the target directory path from the array.
     * @param array JSONArray to modify
     * @param target target path to remove from the JSONArray
     * @throws Exception JSONException
     */
    public static void removeDirEntry(JSONArray array, String target) throws Exception {
        for (int i = 0; i < array.length(); ++i) {
            if (array.getJSONObject(i).getString("original_path").equals(target)) {
                array.remove(i);
            }
        }
    }

    /**
     * Update JSON File with a given JSON object.
     * @param jsonOutput target file stream
     * @param src source object
     */
    public static void updateJSONObject (FileOutputStream jsonOutput, JSONObject src) {
        try {
            jsonOutput.write(src.toString().getBytes());
            jsonOutput.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
