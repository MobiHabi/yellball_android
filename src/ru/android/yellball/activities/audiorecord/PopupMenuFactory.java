package ru.android.yellball.activities.audiorecord;

import android.content.Context;
import android.view.View;
import android.widget.PopupMenu;

/**
 * Created by user on 09.01.2015.
 */
public class PopupMenuFactory {
    static public PopupMenu create(Context context, View anchor, int resource, PopupMenu.OnMenuItemClickListener onMenuItemClickListener, PopupMenu.OnDismissListener onDismissListener) {
        PopupMenu menu = new PopupMenu(context, anchor);
        menu.setOnMenuItemClickListener(onMenuItemClickListener);
        menu.setOnDismissListener(onDismissListener);
        menu.getMenuInflater().inflate(resource, menu.getMenu());
        return menu;
    }

    static public PopupMenu create(Context context, View anchor, int resource, PopupMenu.OnMenuItemClickListener onMenuItemClickListener) {
        return create(context, anchor, resource, onMenuItemClickListener, null);
    }
}
