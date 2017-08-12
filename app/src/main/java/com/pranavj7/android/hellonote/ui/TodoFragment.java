package com.pranavj7.android.hellonote.ui;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pranavj7.android.hellonote.adapters.TodoAdapter;
import com.pranavj7.android.hellonote.provider.NotesContract.*;
import com.pranavj7.android.hellonote.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TodoFragment extends Fragment implements TodoAdapter.Listeners,
        LoaderManager.LoaderCallbacks<Cursor>, NotesAndTodoContainerActivity.FabFragment{

    private static final String EXTRA_NOTE_ID = "note_id";
    private static final int TODO_LOADER_ID = 1;

    @BindView(R.id.todo_list) RecyclerView mTodoList;
    @BindView(R.id.list_empty) NestedScrollView mEmptyMessage;

    private final TodoAdapter mAdapter = new TodoAdapter(this);
    private int mNoteId = 0;

    public static TodoFragment newInstance(int noteId) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_NOTE_ID, noteId);

        TodoFragment fragment = new TodoFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mNoteId = getArguments().getInt(EXTRA_NOTE_ID, -1);
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        getLoaderManager().initLoader(TODO_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo, container, false);

        ButterKnife.bind(this, rootView);
        mTodoList.setAdapter(mAdapter);
        return rootView;

    }

    @Override
    public void OnFabClick() {
        if (getFragmentManager() != null) {
            TodoDialogFragment.newInstanceNote(mNoteId)
                    .show(getActivity().getSupportFragmentManager(), "dialog");
        }
    }

    /*@Override
    public void OnFabClick1() {
        int id=150;
        while(id>=0){
            //        Toast.makeText(getActivity(),String.valueOf(id),Toast.LENGTH_SHORT).show();
            final Uri Uri = ContentUris.withAppendedId(TODOC.CONTENT_URI,id);
            //   new AlertDialog.Builder(getContext())
            //         .setTitle(R.string.Todo_dialog_delete)
            //       .setMessage(R.string.Todo_remove_message)
            //     .setPositiveButton(R.string.Todo_delete, new DialogInterface.OnClickListener() {
            //       @Override
            //     public void onClick(DialogInterface dialog, int which) {
            getContext().getContentResolver().delete(Uri, null, null);
            id--;
        }
    }
*/
    @Override
    public void TaskEditClicked(int id) {
        TodoDialogFragment.newInstanceTodo(id).show(getFragmentManager(), "dialog");
    }
    @Override
    public void TaskDeleteClicked(final int id) {
            final Uri Uri = ContentUris.withAppendedId(TODOC.CONTENT_URI, id);
        new AlertDialog.Builder(getContext())
                    .setTitle(R.string.Todo_dialog_delete)
                    .setMessage(R.string.Todo_remove_message)
                    .setPositiveButton(R.string.Todo_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            getContext().getContentResolver().delete(Uri, null, null);
                        }

                    })
                    .setNegativeButton(R.string.Todo_dialog_negative, null)
                    .show();
        }
  /*   @OnClick(R.id.fab123)
     void hello(){
         Toast.makeText(getActivity(),"Deleting tasks..",Toast.LENGTH_LONG).show();
         int id=150;
        while(id>=0){
    //        Toast.makeText(getActivity(),String.valueOf(id),Toast.LENGTH_SHORT).show();
            final Uri Uri = ContentUris.withAppendedId(TODOC.CONTENT_URI,id);
            //   new AlertDialog.Builder(getContext())
            //         .setTitle(R.string.Todo_dialog_delete)
            //       .setMessage(R.string.Todo_remove_message)
            //     .setPositiveButton(R.string.Todo_delete, new DialogInterface.OnClickListener() {
            //       @Override
            //     public void onClick(DialogInterface dialog, int which) {
            getContext().getContentResolver().delete(Uri, null, null);
            id--;
        }

    }
    */
    @Override
    public void TaskUnchecked(int id) {
        setTaskChecked(id, false);
    }

    @Override
    public void TaskChecked(int id) {
        setTaskChecked(id, true);
    }

    private void setTaskChecked(int id, boolean isChecked) {
        final Uri reminder = ContentUris.withAppendedId(TODOC.CONTENT_URI, id);
        final ContentValues values = new ContentValues();
        values.put(TODOC.COLUMN_IS_CHECKED, isChecked);
        getContext().getContentResolver().update(reminder, values, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = null;
        String[] selectionArgs = null;

        if (mNoteId != 0) {
            selection = TODOC.COLUMN_NOTES_ID + " = ?";
            selectionArgs = new String[] { String.valueOf (mNoteId) };
        }
        return new CursorLoader(getContext(), TODOC.CONTENT_URI,
                TodoAdapter.PROJECTION, selection, selectionArgs, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            final Uri reminder = ContentUris.withAppendedId(NOTESC.CONTENT_URI, mNoteId);
            final ContentValues values = new ContentValues();
            values.put(NOTESC.COLUMN_INDICATOR, 1);
            getContext().getContentResolver().update(reminder, values, null, null);
            mTodoList.setVisibility(View.VISIBLE);
            mEmptyMessage.setVisibility(View.GONE);
        }
        else
        {
            final Uri reminder = ContentUris.withAppendedId(NOTESC.CONTENT_URI, mNoteId);
            final ContentValues values = new ContentValues();
            values.put(NOTESC.COLUMN_INDICATOR, 0);
            getContext().getContentResolver().update(reminder, values, null, null);

            mTodoList.setVisibility(View.GONE);
            mEmptyMessage.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        mAdapter.swapCursor(null);
    }
}