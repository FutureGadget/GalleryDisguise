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
    /**
     * Example.
     * getLeafPhotoDirs(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
     * getLeafPhotoDirs(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))
     * @param dir Directory to search from.
     * @return ArrayList of leaf directory paths.
     */
    public static ArrayList<String> getLeafPhotoDirs(File dir) {
        ArrayList<String> leafPaths = new ArrayList<>();
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

    /**
     * Recursively finds leaf dirs from the root directory. (Root directory is given as a parameter)
     * @param f root directory file
     * @param leafPaths data structure to store leaf directories' absolute paths.
     * @return ArrayList of saved leaf directory paths.
     */
    private static boolean findLeafDir(File f, ArrayList<String> leafPaths) {
        boolean isLeaf = true;
        if (f.getName().equalsIgnoreCase(".thumbnails")) return false;
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
