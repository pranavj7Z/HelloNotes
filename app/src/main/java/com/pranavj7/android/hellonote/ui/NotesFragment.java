package com.pranavj7.android.hellonote.ui;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.EventLog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.pranavj7.android.hellonote.R;
import com.pranavj7.android.hellonote.adapters.NotesAdapter;
import com.pranavj7.android.hellonote.provider.NotesContract;
import com.pranavj7.android.hellonote.provider.NotesContract.*;

import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class NotesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,NotesAdapter.Listeners
{
    public static final String[] PROJECTION = new String[] {
            NotesContract.NOTESC.COLUMN_NUM,
            NotesContract.NOTESC.COLUMN_LINK,
    };
    private static final int COLUMN_NUM=1;
    private static final int COLUMN_LINK=2;
   private NotesAdapter mn;


    @BindView(R.id.notes_list) RecyclerView mNotesList;
    @BindView(R.id.notes_list_empty) TextView mEmptyMessage;

    private boolean mIncludeArchived = false;
    private boolean mAllNotes = true;
    private boolean mSortBy = true;
    private boolean mAlpha = false;
    private boolean mFav = false;
    private static final int NOTES_LOADER_ID = 1;
    private static final String SELECTION_EXCLUDE_ARCHIVED = NOTESC.COLUMN_IS_ARCHIVED + " = 0";
    private static final String SELECTION_INCLUDE_ONLY_ARCHIVED = NOTESC.COLUMN_IS_ARCHIVED;
    private static final String SELECTION_INCLUDE_ONLY_FAV = NOTESC.COLUMN_IS_FAVORITED;
    private static final String SELECTION_EXCLUDE_FAVOURITED = NOTESC.COLUMN_IS_ARCHIVED + " = 0";
    private static final  String SORT_BY_LATEST = NOTESC.COLUMN_DATE + " DESC";
    private static final  String SORT_BY_OLDEST = NOTESC.COLUMN_DATE + " ASC";
    private static final  String SORT_BY_ALPHA = NOTESC.COLUMN_TITLE + " ASC";
    private final NotesAdapter mAdapter = new NotesAdapter(this);
    private NotesAdapter adapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notes, container, false);

        ButterKnife.bind(this, rootView);
   //     Parcelable state = mNotesList.onSaveInstanceState();

        mNotesList.setAdapter(mAdapter);

        //   mNotesList.onRestoreInstanceState(state);
        getLoaderManager().initLoader(NOTES_LOADER_ID, null, this);
        registerForContextMenu(mNotesList);

            return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        setHasOptionsMenu(true);

        inflater.inflate(R.menu.menu_notes_fragment, menu);

    }

    boolean userRegistered = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.allnotes:
                item.setChecked(!item.isChecked());
                userRegistered=false;
                mIncludeArchived = false;
                mFav=false;
                getLoaderManager().restartLoader(NOTES_LOADER_ID, null, this);
                return true;
            case R.id.archive:
                item.setChecked(!item.isChecked());
                userRegistered=true;
                mIncludeArchived = item.isChecked();
                mFav=false;
                getLoaderManager().restartLoader(NOTES_LOADER_ID, null, this);
                return true;
            case R.id.favorites:
                item.setChecked(!item.isChecked());
                mIncludeArchived = false;
                mFav=true;
                getLoaderManager().restartLoader(NOTES_LOADER_ID, null, this);
                return true;
            case R.id.action_sortby_latest:
                mSortBy = true;
                mAlpha=false;
                getLoaderManager().restartLoader(NOTES_LOADER_ID, null, this);
                return true;
            case R.id.action_sortby_oldest:
                mSortBy = false;
                mAlpha = false;
                getLoaderManager().restartLoader(NOTES_LOADER_ID, null, this);
                return true;
            case R.id.action_sortby_alpha:
                mAlpha = true;
                getLoaderManager().restartLoader(NOTES_LOADER_ID, null, this);
                return true;
          //  case R.id.haha:
            //    Intent intent = new Intent(getContext(), NewDrawableActivity.class);
              //  getContext().startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    //detail note activity


        @Override
        public void NoteArchiveClicked(int id) {
        Uri habitUri = ContentUris.withAppendedId(NOTESC.CONTENT_URI, id);
        ContentValues values = new ContentValues();
        values.put(NOTESC.COLUMN_IS_ARCHIVED, true);
    values.put(NOTESC.COLUMN_LINK, 1);
        getContext().getContentResolver().update(habitUri, values, null, null);
          }



    @Override
    public void NoteFavClicked(int id) {
        Uri habitUri = ContentUris.withAppendedId(NOTESC.CONTENT_URI, id);
        ContentValues values = new ContentValues();
        values.put(NOTESC.COLUMN_IS_FAVORITED, true);
        values.put(NOTESC.COLUMN_FAV,1);
        getContext().getContentResolver().update(habitUri, values, null, null);
        Toast.makeText(getActivity(), "Added to Favourites!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void NoteUnfavClicked(int id) {
        Uri habitUri = ContentUris.withAppendedId(NOTESC.CONTENT_URI, id);
        ContentValues values = new ContentValues();
        values.put(NOTESC.COLUMN_IS_FAVORITED, false);
        values.put(NOTESC.COLUMN_FAV,0);
        getContext().getContentResolver().update(habitUri, values, null, null);
        Toast.makeText(getActivity(), "Removed from favourites!",
                Toast.LENGTH_SHORT).show();
    }




    @Override
    public void NoteClicked(int id) {
        Intent intent = new Intent(getContext(), NotesAndTodoContainerActivity.class);
        intent.putExtra(NotesAndTodoContainerActivity.EXTRA_NOTE_ID, id);
        getContext().startActivity(intent);
    }


    @Override
    public void NoteLongClicked(int id) {
        Intent intent = new Intent(getContext(), NewNoteActivity.class);
        intent.putExtra(NewNoteActivity.EXTRA_NOTE_ID, id);
        getContext().startActivity(intent);
    }

    @Override
    public void NoteEditClicked(int id) {
        Intent intent = new Intent(getContext(), NewNoteActivity.class);
        intent.putExtra(NewNoteActivity.EXTRA_NOTE_ID, id);
        getContext().startActivity(intent);
    }

 /*   @Override
    public void NoteArchiveClicked(int id) {
        Uri habitUri = ContentUris.withAppendedId(NOTESC.CONTENT_URI, id);
        ContentValues values = new ContentValues();
        values.put(NOTESC.COLUMN_IS_ARCHIVED, true);
        getContext().getContentResolver().update(habitUri, values, null, null);
    }
*/
    @Override
    public void NoteUnArchiveClicked(int id) {
        Uri habitUri = ContentUris.withAppendedId(NOTESC.CONTENT_URI, id);
        ContentValues values = new ContentValues();
        values.put(NOTESC.COLUMN_IS_ARCHIVED, false);
        values.put(NOTESC.COLUMN_LINK, 0);
        getContext().getContentResolver().update(habitUri, values, null, null);
    }

    @Override
    public void NoteDeleteClicked(int id) {
        final Uri NotesUri = ContentUris.withAppendedId(NOTESC.CONTENT_URI, id);
        new AlertDialog.Builder(getContext())
            .setTitle(getContext().getString(R.string.del))
            .setMessage(getContext().getString(R.string.dialog_remove_message))
            .setPositiveButton(getContext().getString(R.string.dialog_delete_note_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getContext().getContentResolver().delete(NotesUri, null, null);
                }
            })
            .setNegativeButton(getContext().getString(R.string.dialog_delete_cancel), null)
            .show();
    }


        @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    //String sortBy = mSortBy ? SORT_BY_LATEST : SORT_BY_OLDEST;
            screen();
            mNotesList.setLayoutManager(new StaggeredGridLayoutManager(List_Column_Count, StaggeredGridLayoutManager.VERTICAL));
            String sortBy;
        if(mAlpha)
        {
            sortBy = SORT_BY_ALPHA;
        }
        else {

            if (mSortBy) {
                sortBy = SORT_BY_LATEST;
            }
            else
            {
                sortBy = SORT_BY_OLDEST;
            }
        }
        String selection = "";
        if(mFav)
         {
    selection = SELECTION_INCLUDE_ONLY_FAV;
         }
        else {
    if (!mIncludeArchived) {
        selection = SELECTION_EXCLUDE_ARCHIVED;
    } else {
        selection = SELECTION_INCLUDE_ONLY_ARCHIVED;
    }
}
        return new CursorLoader(
                getContext(),
                NOTESC.CONTENT_WITH_STATUS_URI,
                NotesAdapter.PROJECTION,
                selection,
                null,   //selectionArgs
                sortBy
        );
    }
    int List_Column_Count;

public void screen() {

    String tabletSize = getResources().getString(R.string.screen_type);
    // check whether device is tablet of 10'
    if (tabletSize.equals("10-inch-tablet"))
    {
        List_Column_Count=5;
    }
     // check whether device is tab of 7'
    else if(tabletSize.equals("7-inch-tablet")) {

        List_Column_Count=4;
    }
    // check whether device is phablet
    else if(tabletSize.equals("phablets-phones")) {
        List_Column_Count = 3;
    }
    //check if device is a phone
    else if(tabletSize.equals("phones"))
    {
            List_Column_Count=2;
    }
    // default value
    else if(tabletSize.equals("default"))
        {
            List_Column_Count=2;
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

// Prevents 'clear()' from clearing/resetting the listview
        //check for notes

        if (cursor.getCount() > 0) {
            // checking the device (tab or phone)
            mNotesList.setVisibility(View.VISIBLE);
            mEmptyMessage.setVisibility(View.GONE);
        }
        // no notes ? set empty message
        else {
            mNotesList.setVisibility(View.GONE);
            mEmptyMessage.setVisibility(View.VISIBLE);
        }
        if (!userRegistered) {
                ContentValues values = new ContentValues();
                values.put(NOTESC.COLUMN_LINK, 1);
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(NOTESC.COLUMN_LINK, 0);

        }

        mAdapter.swapCursor(cursor);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);

    }
}