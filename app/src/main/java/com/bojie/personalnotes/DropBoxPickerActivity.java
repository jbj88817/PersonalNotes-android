package com.bojie.personalnotes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Created by bojiejiang on 10/15/15.
 */
public class DropBoxPickerActivity extends BaseActivity
        implements DropBoxDirectoryListenerAsync.OnLoadFinished,
        DropBoxDirectoryCreatorAsync.OnDirectoryCreateFinished {

    private ProgressDialog mDialog;
    private DropboxAPI<AndroidAuthSession> mAPI;
    private boolean mAfterAuth = true;
    private DropboxAdapter mDropboxAdapter;
    private Stack<String> mDirectoryStack = new Stack<>();
    private boolean mIsFirstClick = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbx_picker_layout);
        mDirectoryStack.push("/");
        setUpList();
        setUpBar();
        setUpDirectoryCreator();
        if (!AppSharedPreferences.isDropBoxAuthenticated(getApplicationContext())) {
            authenticate();
        } else {
            AndroidAuthSession session = DropBoxActions.buildSession(getApplicationContext());
            mAPI = new DropboxAPI<AndroidAuthSession>(session);
            initProgressDialog();
            new DropBoxDirectoryListenerAsync(
                    getApplicationContext(),
                    mAPI,
                    getCurrentPath(),
                    DropBoxPickerActivity.this)
                    .execute();
        }
    }

    private void setUpDirectoryCreator() {
        final EditText newDirectory = (EditText) findViewById(R.id.new_directory_edit_text);
        final ImageView createDir = (ImageView) findViewById(R.id.new_directory);

        createDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsFirstClick) {
                    newDirectory.setVisibility(View.VISIBLE);
                    createDir.setImageResource(R.drawable.ic_action_done);
                    newDirectory.requestFocus();
                    mIsFirstClick = false;
                } else {
                    String directoryName = newDirectory.getText().toString();
                    createDir.setImageResource(R.drawable.ic_add_folder);
                    newDirectory.setVisibility(View.GONE);
                    if (directoryName.length() > 0) {
                        initProgressDialog();
                        new DropBoxDirectoryCreatorAsync(mAPI,
                                getCurrentPath() + "/" + directoryName,
                                getApplicationContext(),
                                DropBoxPickerActivity.this,
                                directoryName);
                    }
                }
            }
        });
    }

    private void setUpBar() {
        TextView logoutTV = (TextView) findViewById(R.id.log_out_drop_box_label);
        logoutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
                AppSharedPreferences.isDropBoxAuthenticated(getApplicationContext(), false);
                startActivity(new Intent(DropBoxPickerActivity.this, AppAuthenticationActivity.class));
            }
        });

        ImageView save = (ImageView) findViewById(R.id.selection_directory);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppSharedPreferences.storeDropBoxUploadPath(getApplicationContext(), getCurrentPath());
                AppSharedPreferences.setPersonalNotesPreference(getApplicationContext(), AppConstant.DROP_BOX_SELECTION);
                showToast(AppConstant.IMAGE_LOCATION_SAVED_DROPBOX);
                actAsNote();
                startActivity(new Intent(DropBoxPickerActivity.this, NotesActivity.class));
            }
        });

        ImageView back = (ImageView) findViewById(R.id.back_navigation);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initProgressDialog();
                try {
                    mDirectoryStack.pop();
                } catch (EmptyStackException e) {
                    startActivity(new Intent(DropBoxPickerActivity.this, NotesActivity.class));
                }

                new DropBoxDirectoryListenerAsync(getApplicationContext(),
                        mAPI, getCurrentPath(), DropBoxPickerActivity.this).execute();
            }
        });
    }
}
