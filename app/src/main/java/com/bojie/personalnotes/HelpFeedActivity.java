package com.bojie.personalnotes;

import android.os.Bundle;

/**
 * Created by bojiejiang on 8/14/15.
 */
public class HelpFeedActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_feedback_layout);
        mToolBar = activateToolbar();
        setUpNavigationDrawer();
    }
}
