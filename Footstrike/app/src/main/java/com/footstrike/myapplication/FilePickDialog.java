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

public class FilePickDialog extends Dialog {

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


        ListView l = findViewById(R.id.listView);

        FileItem[] files = Arrays.stream(FileHelper.getStoredFiles()).map(s -> {
            int i = s.lastIndexOf('.');
            return new FileItem(s.substring(0, i), s.substring(i + 1));
        }).toArray(FileItem[]::new);


        ArrayAdapter<FileItem> fileAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, files);

        l.setAdapter(fileAdapter);

        l.setOnItemClickListener((adapterView, view, i, l1) -> {

            if (fileSelectedHandler != null) {
                fileSelectedHandler.onFileSelected(new File(FileHelper.getRoot(), files[i].getFile()));
            }
            FilePickDialog.this.dismiss();
        });
    }

    public void setFileSelectedHandler(IFileSelected fileSelectedHandler) {
        this.fileSelectedHandler = fileSelectedHandler;
    }

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

    public interface IFileSelected {
        public void onFileSelected(File file);
    }

}
