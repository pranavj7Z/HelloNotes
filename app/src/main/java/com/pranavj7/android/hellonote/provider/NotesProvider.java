package com.pranavj7.android.hellonote.provider;

import android.appwidget.AppWidgetManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pranavj7.android.hellonote.provider.NotesContract.*;

import java.util.HashMap;
import java.util.Map;

import static com.pranavj7.android.hellonote.provider.NotesContract.CONTENT_AUTHORITY;

public class NotesProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private NotesDb mOpenHelper;

    private static final int NOTE = 100;
    private static final int NOTES = 101;
    private static final int NOTE_WITH_STATUS = 102;
    private static final int TODO = 200;
    private static final int TODOS = 201;


    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, NOTESC.PATH + "/#", NOTE);
        sUriMatcher.addURI(CONTENT_AUTHORITY, NOTESC.PATH + "/",NOTES );
        sUriMatcher.addURI(CONTENT_AUTHORITY, NOTESC.PATH + "/" + NOTESC.WITH_STATUS_PATH + "/", NOTE_WITH_STATUS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, TODOC.PATH + "/#", TODO);
        sUriMatcher.addURI(CONTENT_AUTHORITY, TODOC.PATH + "/", TODOS);

    }


    @Override
    public boolean onCreate() {
        mOpenHelper = NotesDb.getInstance(getContext());

        return true;
    }

    private String getTable(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case NOTE:
            case NOTES:
                return NOTESC.TABLE_NAME;
            case TODO:
            case TODOS:
                return TODOC.TABLE_NAME;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private String addIdToSelection(@NonNull Uri uri, String selection) {
        switch (sUriMatcher.match(uri)) {
            case NOTE:
            case TODO:
                String idSelection = "_ID = " + ContentUris.parseId(uri);

                if (selection != null && !selection.isEmpty()) {
                    return idSelection + " AND " + selection;
                }

                return idSelection;
        }

        return selection;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        switch (sUriMatcher.match(uri)) {
            case NOTE_WITH_STATUS:
                return queryStatus(projection, selection, selectionArgs, sortOrder);
            case NOTE:
            case NOTES:
            case TODO:
            case TODOS:
                Cursor cursor = mOpenHelper.getWritableDatabase().query(
                        getTable(uri),
                        projection,
                        addIdToSelection(uri, selection),
                        selectionArgs,
                        null, // group by
                        null, // having
                        sortOrder);

                if (getContext() != null) {
                    cursor.setNotificationUri(getContext().getContentResolver(), uri);
                }

                return cursor;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case NOTE:
                return NOTESC.CONTENT_ITEM_TYPE;
            case NOTES:
            case NOTE_WITH_STATUS:
                return NOTESC.CONTENT_TYPE;
            case TODO:
                return TODOC.CONTENT_ITEM_TYPE;
            case TODOS:
                return TODOC.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case NOTES:
            case TODOS:
                long id = mOpenHelper.getWritableDatabase().insert(getTable(uri), null, values);

                if (id <= 0) {
                    throw new RuntimeException("Unable to insert rows into: " + uri);
                }

                if (getContext() != null) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    getContext().sendBroadcast(new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE));
                }

                return ContentUris.withAppendedId(uri, id);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case NOTE:
            case NOTES:
            case TODO:
            case TODOS:
                int rows = mOpenHelper.getWritableDatabase()
                        .delete(getTable(uri), addIdToSelection(uri, selection), selectionArgs);

                // null selection will delete all rows
                if ((selection == null || rows > 0) && getContext() != null) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    getContext().sendBroadcast(new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE));
                }

                return rows;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {

        switch (sUriMatcher.match(uri)) {
            case NOTE:
            case NOTES:
            case TODO:
            case TODOS:
                int rows = mOpenHelper.getWritableDatabase().update(getTable(uri), values,
                        addIdToSelection(uri, selection), selectionArgs);

                if (rows > 0 && getContext() != null) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    getContext().sendBroadcast(new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE));
                }

                return rows;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    private Cursor queryStatus(String[] projection, String selection,
                                         String[] selectionArgs, String sortOrder) {

        final Map<String, String> columnMap = new HashMap<>();

        columnMap.put(NOTESC._ID, NOTESC.TABLE_NAME + "." + NOTESC._ID);
        columnMap.put(NOTESC.COLUMN_TITLE, NOTESC.COLUMN_TITLE);
        columnMap.put(NOTESC.COLUMN_DESCRIPTION, NOTESC.COLUMN_DESCRIPTION);
        columnMap.put(NOTESC.COLUMN_DATE, NOTESC.COLUMN_DATE);
        columnMap.put(NOTESC.COLUMN_COLOR, NOTESC.COLUMN_COLOR);
        columnMap.put(NOTESC.COLUMN_IS_ARCHIVED, NOTESC.COLUMN_IS_ARCHIVED);
        columnMap.put(NOTESC.COLUMN_IMAGE, NOTESC.COLUMN_IMAGE);
        columnMap.put(NOTESC.COLUMN_NUM,NOTESC.COLUMN_NUM);
        columnMap.put(NOTESC.COLUMN_LINK,NOTESC.COLUMN_LINK);
        columnMap.put(NOTESC.COLUMN_INDICATOR,NOTESC.COLUMN_INDICATOR);
        columnMap.put(NOTESC.COLUMN_FAV,NOTESC.COLUMN_FAV);

        final String notesId = NOTESC.TABLE_NAME + "." + NOTESC._ID;

        final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setProjectionMap(columnMap);
        builder.setTables(NOTESC.TABLE_NAME);
        final Cursor cursor = builder.query(mOpenHelper.getWritableDatabase(), projection,
                selection, selectionArgs, notesId, null, sortOrder);
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), NotesContract.BASE_CONTENT_URI);
        }
        return cursor;
    }
}
