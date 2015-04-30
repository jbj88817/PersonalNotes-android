package com.bojie.personalnotes;

/**
 * Created by bojiejiang on 4/30/15.
 */
public class NavigationDrawerItem {

    private int iconId;
    private String title;

    public NavigationDrawerItem(int iconId, String title) {
        this.iconId = iconId;
        this.title = title;
    }

    public int getIconId() {
        return iconId;
    }

    public String getTitle() {
        return title;
    }
}
