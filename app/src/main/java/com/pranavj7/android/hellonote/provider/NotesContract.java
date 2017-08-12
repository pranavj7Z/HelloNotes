package com.pranavj7.android.hellonote.provider;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
public final class
        NotesContract {
    public static final String CONTENT_AUTHORITY = "com.pranavj7.android.HelloNote.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static class NOTESC implements BaseColumns {
        public static final String PATH = "notes";
        public static final String WITH_STATUS_PATH = "path";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        public static final Uri CONTENT_WITH_STATUS_URI =
                CONTENT_URI.buildUpon().appendPath(WITH_STATUS_PATH).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_URI  + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH;

        public static final String TABLE_NAME = "notes";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IMAGE ="image";
        public static final String COLUMN_NUM="number";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_COLOR = "color";
        public static final String COLUMN_IS_ARCHIVED = "is_archived";
        public static final String COLUMN_IS_FAVORITED = "is_favorite";
        public static final String COLUMN_LINK="link";
        public static final String COLUMN_INDICATOR ="indicator";
        public static final String COLUMN_FAV="favourite";
    }
    public static class TODOC implements BaseColumns {
        public static final String PATH = "todos";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_URI  + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH;
        public static final String TABLE_NAME = "todo";
        public static final String COLUMN_IS_CHECKED = "is_checked";
        public static final String COLUMN_NOTIFY_CB= "is_cb";
        public static final String COLUMN_NOTES_ID = "notes_id";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TASKS="tasks";
        //public static final String COLUMN_INDICATOR = "indicator";
    }
}
