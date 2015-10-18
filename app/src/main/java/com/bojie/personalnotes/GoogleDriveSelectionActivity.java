package com.bojie.personalnotes;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

/**
 * Created by bojiejiang on 10/16/15.
 */
public class GoogleDriveSelectionActivity extends BaseGoogleDriveActivity {

    private static final int REQUEST_CODE_OPENER = 1;
    private static DriveId mDriveId;


    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);
        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{DriveFolder.MIME_TYPE})
                .setActivityTitle("Choose image storage")
                .build(getGoogleApiClient());

        try {

            startIntentSenderForResult(
                    intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);

        } catch (IntentSender.SendIntentException e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_OPENER:
                if (data != null && resultCode == RESULT_OK) {
                    mDriveId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    AppSharedPreferences.storeGoogleDriveResourceId(getApplicationContext(), mDriveId.getResourceId());
                    BaseActivity.actAsNote();
                    startActivity(new Intent(GoogleDriveSelectionActivity.this, GoogleDriveDirectoryNameGetterActivity.class));
                    finish();
                    return;
                } else {
                    startActivity(new Intent(GoogleDriveSelectionActivity.this, GoogleDriveDirectoryNameGetterActivity.class));
                    finish();
                    return;
                }

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        super.onConnectionSuspended(i);
    }
}
