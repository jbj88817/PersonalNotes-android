package com.bojie.personalnotes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bojiejiang on 6/23/15.
 */
public class NoteCustomList extends LinearLayout {

    private Context mContext;
    private LinearLayout mListItem;
    private List<EditText> mTextBoxes = new ArrayList<>();

    public NoteCustomList(Context context) {
        super(context);
        mContext = context;
    }

    public void setUp() {
        setOrientation(VERTICAL);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
    }

    public void setUpForHomeAdapter(String listEntries) {
        // Eggs$false%Ham$false%Bread$false%Vegemite$false
        setOrientation(VERTICAL);
        String[] listEntryTokens = listEntries.split("%");
        // [0] Eggs$false
        // [1] Ham$false
        // ....

        boolean isStrikeOut = false;
        String listItem = "";
        for (String entryDetails : listEntryTokens) {
            mListItem = new LinearLayout(mContext);
            mListItem.setOrientation(HORIZONTAL);
            String[] listEntry = entryDetails.split("\\$");
            // [0] Eggs
            // [1] false

            for (int i = 0; i < listEntry.length; i++) {
                if (i % 2 ==0){
                    listItem = listEntry[i];
                } else {
                    isStrikeOut = Boolean.valueOf(listEntry[i]);
                }
            }

            CheckBox checkBox = new CheckBox(mContext);
            checkBox.setChecked(isStrikeOut);
            checkBox.setEnabled(false);
            TextView textView = new TextView(mContext);
            textView.setText(listItem);
            textView.setBackgroundColor(Color.TRANSPARENT);
            if (isStrikeOut) {
                textView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }

            mListItem.addView(checkBox);
            mListItem.addView(textView);
            addView(mListItem);
            
        }

    }
}
