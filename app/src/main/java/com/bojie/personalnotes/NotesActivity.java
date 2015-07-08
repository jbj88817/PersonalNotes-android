package com.bojie.personalnotes;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<List<Note>>,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private List<Note> mNotes;
    private RecyclerView mRecyclerView;
    private NotesAdapter mNotesAdapter;
    private ContentResolver mContentResolver;
    private static Boolean mIsInAuth;
    private static Bitmap mSendingImage = null;
    private boolean mIsImageNotFound = false;
    private DropboxAPI<AndroidAuthSession> mDropboxAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        activateToolbar();
        setUpForDropbox();
        setUpNavigationDrawer();
        setUpRecyclerView();
        setUpActions();
    }

    private void setUpForDropbox() {
        AndroidAuthSession session = DropboxActions.buildSession(getApplicationContext());
        mDropboxAPI = new DropboxAPI<AndroidAuthSession>(session);
    }

    private void setUpRecyclerView() {
        mContentResolver = getContentResolver();
        mNotesAdapter = new NotesAdapter(NotesActivity.this, new ArrayList<Note>());
        int LOADER_ID = 1;
        getSupportLoaderManager().initLoader(LOADER_ID, null, NotesActivity.this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_home);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mNotesAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                edit(view);
            }

            @Override
            public void onItemLongClick(View view, int postion) {
                PopupMenu popupMenu = new PopupMenu(NotesActivity.this, view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu_ations_notes, popupMenu.getMenu());
                popupMenu.show();
                final View v = view;
                final int pos = postion;
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_delete) {
                            moveToTrash();
                            delete(v, pos);
                        } else if (item.getItemId() == R.id.action_archive) {
                            moveToArchive(v, pos);
                        } else if (item.getItemId() == R.id.action_edit) {
                            edit(v);
                        }
                        return false;
                    }
                });
            }

        }));

    }

    @Override
    public Loader<List<Note>> onCreateLoader(int id, Bundle args) {
        mContentResolver = getContentResolver();
        return new NotesLoader(NotesActivity.this, mContentResolver, BaseActivity.mType);
    }

    @Override
    public void onLoadFinished(Loader<List<Note>> loader, List<Note> data) {
        this.mNotes = data;
        final Thread[] thread = new Thread[mNotes.size()];
        int threadCounter = 0;
        for (final Note aNote : mNotes) {
            if (AppConstant.GOOGLE_DRIVE_SELECTION == aNote.getStorageSelection()) {
                GDUT.init(this);
                if (checkPlayServices() && checkUserAccout()) {
                    GDActions.init(this, GDUT.AM.getActiveEmil());
                    GDActions.connect(true);
                }
                thread[threadCounter] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        do {
                            ArrayList<GDActions.GF> gfs = GDActions.search(AppSharedPreferences.getGoogleDriveResourceId(getApplicationContext()),
                                    aNote.getImagePath(), GDUT.MIME_JPEG);
                            if (gfs.size() > 0) {
                                byte[] imageBytes = GDActions.read(gfs.get(0).id, 0);
                                Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                aNote.setBitmap(bmp);
                                mIsImageNotFound = false;
                                mNotesAdapter.setData(mNotes);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNotesAdapter.notifyImageObtained();
                                    }
                                });
                            } else {
                                aNote.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_loading));
                                mIsImageNotFound = true;
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } while (mIsImageNotFound);
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
