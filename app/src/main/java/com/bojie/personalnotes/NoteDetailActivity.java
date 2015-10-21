package com.bojie.personalnotes;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;

/**
 * Created by bojiejiang on 10/18/15.
 */
public class NoteDetailActivity extends BaseActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

        private static int sMonth, sHour, sDay, sMinute, sSecond;

        private DropboxAPI<AndroidAuthSession> mAPI;
        private File mDropBoxFile;
        private String mCameraFileName;
        private NoteCustomList mNoteCustomList;

        // Constants
        public static final int NORMAL = 1;
        public static final int LIST = 2;
        public static final int CAMERA_REQUEST = 1888;
        public static final int TAKE_GALLERY_CODE = 1;


}
