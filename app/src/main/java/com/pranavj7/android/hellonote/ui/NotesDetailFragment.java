package com.pranavj7.android.hellonote.ui;
import com.pranavj7.android.hellonote.R;
import com.pranavj7.android.hellonote.provider.NotesContract.NOTESC;
import com.pranavj7.android.hellonote.Utility.RuledLineNote;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.text.DateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotesDetailFragment extends Fragment implements NotesAndTodoContainerActivity.Faby,
        LoaderManager.LoaderCallbacks<Cursor>{
    @BindView(R.id.notes_desc) RuledLineNote mNotesDesc;
    @BindView(R.id.Title) TextView mTitle;
    @BindView(R.id.date) Button mDate;
    @BindView(R.id.hbt) FrameLayout mhbt;
    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SERIF, Typeface.NORMAL);

    private static final String EXTRA_NOTE_ID = "note_id";
    private int mNoteId = -1;

    public static NotesDetailFragment newInstance(int noteId) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_NOTE_ID, noteId);
        NotesDetailFragment fragment = new NotesDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null) {
            mNoteId = getArguments().getInt(EXTRA_NOTE_ID);
        }
        if (mNoteId == -1) {
            throw new UnsupportedOperationException("An ID must be passed");

        }
        getLoaderManager().initLoader(NoteFetch.ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notes_details, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    public void OnFabClick1() {
        String shareBody = share + "\n" + "\n" + share1;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "share using"));
    }

String share;
    String share1;
        private void reloadNote(Cursor cursor) {
        //convert milliseconds to date
        mDate.setText(DateFormat.getDateInstance().format(cursor.getLong(NoteFetch.COLUMN_DATE)));
        mNotesDesc.setText(cursor.getString(NoteFetch.COLUMN_DESCRIPTION));
        mNotesDesc.setTextColor(Color.BLACK);
        mTitle.setText(cursor.getString(NoteFetch.COLUMN_TITLE));
            mTitle.setTypeface(NORMAL_TYPEFACE);
            share = cursor.getString(NoteFetch.COLUMN_TITLE);
            share1=cursor.getString(NoteFetch.COLUMN_DESCRIPTION);
    }

    @Override
        public Loader<Cursor> onCreateLoader ( int id, Bundle args){
            switch (id) {
                case NoteFetch.ID:
                    return new CursorLoader(getContext(), NOTESC.CONTENT_URI,
                            NoteFetch.PROJECTION, NoteFetch.SELECTION,
                            new String[]{String.valueOf(mNoteId)},
                            null);
                default:
                    throw new UnsupportedOperationException("Unknown loader ID");
            }
        }
        @Override
        public void onLoadFinished (Loader < Cursor > loader, Cursor cursor){
            if (cursor == null || !cursor.moveToFirst()) {
                return;
            }
                    reloadNote(cursor);
            }

            @Override
            public void onLoaderReset (Loader < Cursor > loader) {}

            private static final class NoteFetch {
                private NoteFetch() {
                }
                static final int ID = 500;
                static final String SELECTION = NOTESC._ID + " = ?";
                static final String[] PROJECTION = new String[]{
                        NOTESC.COLUMN_DESCRIPTION,
                        NOTESC.COLUMN_DATE,
                        NOTESC.COLUMN_TITLE,
                        NOTESC.COLUMN_COLOR,
                };
                static final int COLUMN_DESCRIPTION = 0;
                static final int COLUMN_DATE = 1;
                static final int COLUMN_TITLE= 2;
                static final int COLUMN_COLOR=3;
            }
        }
