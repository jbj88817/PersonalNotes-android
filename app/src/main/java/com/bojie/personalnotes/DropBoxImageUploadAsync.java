package com.bojie.personalnotes;

import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by bojiejiang on 10/15/15.
 */
public class DropBoxImageUploadAsync extends AsyncTask<Void, Long, Boolean> {
    private DropboxAPI<?> mAPI;
    private String mPath;
    private File mFile;
    private String mFileName;


    public DropBoxImageUploadAsync(Context context, DropboxAPI<?> API, File file, String fileName) {
        mAPI = API;
        mPath = AppSharedPreferences.getDropBoxUploadPath(context);
        mFile = file;
        mFileName = fileName;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        String errorMessage;
        try{
            FileInputStream fis = new FileInputStream(mFile);
            String path = mPath +"/" +mFileName;
            try {
                DropboxAPI.UploadRequest request = mAPI.putFileOverwriteRequest(path, fis, mFile.length(),
                        new ProgressListener() {
                            @Override
                            public void onProgress(long bytes, long total) {
                                publishProgress(bytes);

                            }

                            @Override
                            public long progressInterval() {
                                return 500;
                            }
                        });

                if (request != null) {
                    request.upload();
                    return true;
                }
            } catch (DropboxException e) {
                errorMessage = "Dropbox exception";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();

    }
        return false;
});
