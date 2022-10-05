package com.footstrike.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.Arrays;
// This class allows the user to pick a file from a predefined location
public class FilePickDialog extends Dialog {
    // Whether to show the file extention in the ui or not
    private boolean showExt = true;

    private IFileSelected fileSelectedHandler;

    public FilePickDialog(@NonNull Context context) {
        super(context);
    }

    public FilePickDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected FilePickDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.file_picker_dialog);

        // ListView where the files will be displayed
        ListView l = findViewById(R.id.listView);

        // Retrieve files from predefined storage location on device and store as a FileItem
        // allows for separation between name and extension
        FileItem[] files = Arrays.stream(FileHelper.getStoredFiles()).map(s -> {
            int i = s.lastIndexOf('.');
            return new FileItem(s.substring(0, i), s.substring(i + 1));
        }).toArray(FileItem[]::new);


        ArrayAdapter<FileItem> fileAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, files);

        // Apply array adapter to the ListView l
        l.setAdapter(fileAdapter);

        // Listen to user interaction with items
        l.setOnItemClickListener((adapterView, view, i, l1) -> {

            if (fileSelectedHandler != null) {
                fileSelectedHandler.onFileSelected(new File(FileHelper.getRoot(), files[i].getFile()));
            }
            FilePickDialog.this.dismiss();
        });
    }
    // Setter for the file selection handler
    public void setFileSelectedHandler(IFileSelected fileSelectedHandler) {
        this.fileSelectedHandler = fileSelectedHandler;
    }
    // Class that describes the file
    public class FileItem {
        private String filename;
        private String fileExt;

        public FileItem(String filename, String fileExt) {
            this.filename = filename;
            this.fileExt = fileExt;
        }

        public String getFile() {
            return filename + "." + fileExt;
        }

        @NonNull
        @Override
        public String toString() {
            if (showExt)
                return filename + "." + fileExt;
            else
                return filename;

        }
    }
    // Interface that is used when a file is selected in this dialog
    public interface IFileSelected {
        public void onFileSelected(File file);
    }

}
