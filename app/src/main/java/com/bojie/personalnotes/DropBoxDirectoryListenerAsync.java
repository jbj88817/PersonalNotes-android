package com.bojie.personalnotes;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bojiejiang on 10/12/15.
 */
public class DropBoxDirectoryListenerAsync extends AsyncTask<Void, Long, Boolean> {

    private Context mContext;
    private DropboxAPI<?> mAPI;
    private List<String> mDirecories = new ArrayList<>();
    private String mErrorMessage;
    private String mCurrentDiretory;
    private OnLoadFinished mOnLoadFinishedListerner;

    public DropBoxDirectoryListenerAsync(Context context, DropboxAPI<?> API, String currentDiretory, OnLoadFinished onLoadFinishedListerner) {
        mContext = context;
        mAPI = API;
        mCurrentDiretory = currentDiretory;
        mOnLoadFinishedListerner = onLoadFinishedListerner;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            mErrorMessage = null;
            DropboxAPI.Entry diretoryEntry = mAPI.metadata(mCurrentDiretory, 1000, null, true, null);
            if (!diretoryEntry.isDir || diretoryEntry.contents == null) {
                mErrorMessage = "File or empty directory";
                return false;
            }
            for (DropboxAPI.Entry entry : diretoryEntry.contents) {
                if (entry.isDir) {
                    mDirecories.add(entry.fileName());
                }
            }
        } catch (DropboxUnlinkedException e) {
            mErrorMessage = "Authentication dropbox error";
        } catch (DropboxPartialFileException e) {
            mErrorMessage = "Download canceled";
        } catch (DropboxServerException e) {
            mErrorMessage = "Network error, try again";
        } catch (DropboxParseException e) {
            mErrorMessage = "Dropbox Parse exception, try again";
        } catch (DropboxException e) {
            mErrorMessage = "Unknown dropbox error, try again";
        }

        if (mErrorMessage != null) {
            return false;
        }else {
            return true;
        }

    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            mOnLoadFinishedListerner.onLoadFinished(mDirecories);
        } else {
            showToast(mErrorMessage);
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        error.show();
    }

    public interface OnLoadFinished {
        void onLoadFinished(List<String> values);
    }
}
