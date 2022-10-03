package com.footstrike.myapplication;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileHelper {


    private static File root;
    private static File internal;

    public static void init(Context context) {
        // Only accessible by the app and
        root = new File(context.getFilesDir(), "records");
        root.mkdirs();

        internal = new File(Environment.getExternalStorageDirectory(),"FootStrike Data/");
        internal.mkdirs();
    }

    public static String[] getStoredFiles() {
        return root.list();
    }

    public static void fileTest() throws IOException {

        File f = new File(root, "test.txt");
        FileWriter writer = new FileWriter(f);

        writer.write("Ek is cool");
        writer.flush();
        writer.close();
    }


    public static File getRoot() {
        return root;
    }

    public static File getinternal() {
        return internal;
    }
}