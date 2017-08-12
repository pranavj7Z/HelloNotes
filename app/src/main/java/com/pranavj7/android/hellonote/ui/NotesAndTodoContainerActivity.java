package com.pranavj7.android.hellonote.ui;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pranavj7.android.hellonote.provider.NotesContract;
import com.pranavj7.android.hellonote.provider.NotesContract.*;
import com.pranavj7.android.hellonote.R;
import com.pranavj7.android.hellonote.adapters.ViewPageAdapter;

import java.text.DateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotesAndTodoContainerActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.fabd)
    FloatingActionButton mFab1;
    // @BindView(R.id.fab123) FloatingActionButton mFab123;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.adView7)
    AdView mAdView;



    public interface FabFragment {
        void OnFabClick();
    }
public interface Faby{
    void OnFabClick1();
}

    public static final String EXTRA_NOTE_ID = "note_id";
    private static final String[] PROJECTION = new String[]{
            NOTESC.COLUMN_TITLE
    };
    private ViewPageAdapter mViewPageAdapter;
    private boolean mShouldShowFab = false;
    private int mCurrentPage = 0;
    private int mNoteId=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        ButterKnife.bind(this);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mAdView.loadAd(adRequest);
        setSupportActionBar(mToolbar);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mNoteId = getIntent().getIntExtra(EXTRA_NOTE_ID, -1);
        mViewPageAdapter = new ViewPageAdapter(getSupportFragmentManager(), this, mNoteId);
        if (mNoteId != -1) {
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int page, float positionOffset, int positionOffsetPixels) {
               float offset = positionOffset;
                if (page < mCurrentPage) {
                    offset = 1 - positionOffset;
                }

                if (!mShouldShowFab || offset > 0.05) {
                    mFab1.show();
                    mFab.hide();

                    // mFab123.hide();
                } else {
                    mFab1.hide();
                    mFab.show();
                    //mFab123.show();
                }
            }

            @Override
            public void onPageSelected(int page) {
                mCurrentPage = page;
                mShouldShowFab = (mViewPageAdapter.getItem(page) instanceof FabFragment);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setAdapter(mViewPageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        getSupportLoaderManager().initLoader(NoteFetch.ID, null, this);
    }
    String share, share1;
    int i = 1234;
    private void reloadNote(Cursor cursor) {
        //convert milliseconds to date
        share1 = cursor.getString(NoteFetch.COLUMN_DESCRIPTION);
        share = cursor.getString(NoteFetch.COLUMN_TITLE);
//   share1 = cursor.getString(NoteFetch.COLUMN_TITLE);
        // i = cursor.getInt(NoteFetch.COLUMN_DATE);
    }


    @OnClick(R.id.fab)
    void OnFabClick() {
        TodoDialogFragment.newInstanceNote(mNoteId)
                .show(getSupportFragmentManager(), "dialog");
    }



    @OnClick(R.id.fabd)
    void OnFabClick1() {

        String shareBody = share + "\n" + "\n" + share1;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "share using"));
        }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case NoteFetch.ID:
                Uri contentUri = ContentUris.withAppendedId(NOTESC.CONTENT_URI, mNoteId);
                return new CursorLoader(this, contentUri, NoteFetch.PROJECTION, null, null, null);
            default:
                throw new UnsupportedOperationException("Unknown loader ID");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || !cursor.moveToFirst()) {
            return;
        }

        switch (loader.getId()) {
            case NoteFetch.ID:
                reloadNote(cursor);
                break;

            default:
                throw new UnsupportedOperationException("Unknown loader ID");
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private static final class NoteFetch {
        private NoteFetch() {
        }

        public static final int ID = 101;
        private static final String[] PROJECTION = new String[]{
                NotesContract.NOTESC.COLUMN_DESCRIPTION,
                NotesContract.NOTESC.COLUMN_TITLE,
        };

        private static final int COLUMN_DESCRIPTION = 0;
        private static final int COLUMN_TITLE = 1;
    }
}