package com.pranavj7.android.hellonote.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.pranavj7.android.hellonote.R;
import com.pranavj7.android.hellonote.provider.NotesContract.*;
import com.pranavj7.android.hellonote.ui.NotesAndTodoContainerActivity;

import java.text.DateFormat;


class HelloNoteRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Cursor mCursor;
    private Context mContext;
    private static final String[] COLUMNS_PROJECTION = new String[] {
            NOTESC._ID,
            NOTESC.COLUMN_TITLE,
            NOTESC.COLUMN_DATE
    };
    private static final int COLUMN_ID = 0;
   private static final int COLUMN_TITLE=1;
    private static final int COLUMN_DATE=2;
    HelloNoteRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = mContext.getContentResolver().query(NOTESC.CONTENT_WITH_STATUS_URI,
                COLUMNS_PROJECTION, NOTESC.COLUMN_IS_ARCHIVED + " = 0", null, null);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }
    @Override
    public RemoteViews getViewAt(int pos) {
        mCursor.moveToPosition(pos);
        RemoteViews view = new RemoteViews(mContext.getPackageName(), R.layout.widget_listnote);
        view.setTextViewText(R.id.note_title, mCursor.getString(COLUMN_TITLE));
        view.setTextViewText(R.id.note_date, DateFormat.getDateInstance().format(mCursor.getLong(COLUMN_DATE)));
        Intent intent = new Intent();
        intent.putExtra(NotesAndTodoContainerActivity.EXTRA_NOTE_ID, mCursor.getInt(COLUMN_ID));
        view.setOnClickFillInIntent(R.id.list_item, intent);
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        mCursor.moveToPosition(i);

        return mCursor.getLong(COLUMN_ID);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
