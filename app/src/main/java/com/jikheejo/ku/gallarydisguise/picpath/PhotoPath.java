package com.jikheejo.ku.gallarydisguise.picpath;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dw on 2016-11-21.
 */

public class PhotoPath {
    public static ArrayList<String> getLeafPhotoDirs() {
        ArrayList<String> leafPaths = new ArrayList<>();
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        boolean isLeaf = true;
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                if (findLeafDir(f, leafPaths)) {
                    leafPaths.add(f.getAbsolutePath());
                }
                isLeaf = false;
            }
        }
        if (dir.listFiles().length != 0 && isLeaf) {
            leafPaths.add(dir.getAbsolutePath());
        }
        return leafPaths;
    }

    private static boolean findLeafDir(File f, ArrayList<String> leafPaths) {
        boolean isLeaf = true;
        // Exclude empty folders
        if (f.listFiles().length == 0) return false;
        for (File file : f.listFiles()) {
            if (file.isDirectory()) {
                if(findLeafDir(file, leafPaths)) {
                    leafPaths.add(file.getAbsolutePath());
                }
                isLeaf = false;
            }
        }
        return isLeaf;
    }
}
