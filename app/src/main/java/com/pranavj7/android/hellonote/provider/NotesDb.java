package com.pranavj7.android.hellonote.provider;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.pranavj7.android.hellonote.provider.NotesContract.*;
public class NotesDb extends SQLiteOpenHelper {
    public static final String NAME = "NotesDB";
    public static final int VERSION = 2;
    private static NotesDb sInstance;


    private NotesDb(Context context) {
        super(context, NAME, null, VERSION);
    }

    public static synchronized NotesDb getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new NotesDb(context.getApplicationContext());
        }
        return sInstance;
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + NOTESC.TABLE_NAME + " (" +
                NOTESC._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NOTESC.COLUMN_TITLE + " TEXT NOT NULL," +
                NOTESC.COLUMN_DESCRIPTION + " TEXT NOT NULL," +
                NOTESC.COLUMN_IMAGE + " BLOB," +
                NOTESC.COLUMN_DATE + " INTEGER NOT NULL," +
                NOTESC.COLUMN_NUM + " INTEGER DEFAULT 0," +
                NOTESC.COLUMN_IS_ARCHIVED + " INTEGER NOT NULL DEFAULT 0," +
                NOTESC.COLUMN_COLOR + " INTEGER NOT NULL," +
                NOTESC.COLUMN_IS_FAVORITED + " INTEGER NOT NULL DEFAULT 0," +
                NOTESC.COLUMN_LINK + " INTEGER DEFAULT 0," +
                NOTESC.COLUMN_INDICATOR + " INTEGER DEFAULT 0," +
                NOTESC.COLUMN_FAV + " INTEGER DEFAULT 0" +
                ")");

        db.execSQL("CREATE TABLE " + TODOC.TABLE_NAME + " (" +
                TODOC._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TODOC.COLUMN_IS_CHECKED + " INTEGER NOT NULL DEFAULT 0," +
                TODOC.COLUMN_NOTIFY_CB + " INTEGER NOT NULL DEFAULT 0," +
                TODOC.COLUMN_NOTES_ID + " INTEGER NOT NULL," +
                TODOC.COLUMN_TIME + " INTEGER," +
                TODOC.COLUMN_DATE + " INTEGER NOT NULL," +
                TODOC.COLUMN_TASKS + " TEXT NOT NULL," +
                "FOREIGN KEY (" + TODOC.COLUMN_NOTES_ID + ") " +
                "REFERENCES " + NOTESC.TABLE_NAME + " (" + NOTESC._ID + ") " +
                "ON DELETE CASCADE" +
                ")");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: implement when updating DB
        int version = oldVersion;
        if(version == 1) {
            version = 2;
        }

        if(version != VERSION) {
            db.execSQL("DROP TABLE IF EXISTS " + NOTESC.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TODOC.TABLE_NAME);
            onCreate(db);
        }
    }



}
