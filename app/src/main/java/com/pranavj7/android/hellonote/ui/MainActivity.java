package com.pranavj7.android.hellonote.ui;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pranavj7.android.hellonote.R;
import com.pranavj7.android.hellonote.QuotesSyncAdapter.SyncAdapter;
import com.pranavj7.android.hellonote.Utility.QuotesPreference;

import org.greenrobot.eventbus.util.ErrorDialogFragments;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.img7)
    ImageView mImg;
    @BindView(R.id.navigation_view) NavigationView mNavigationView;
    @BindView(R.id.fab1) FloatingActionButton mf;


    ActionBarDrawerToggle mDrawerToggle;
    Bundle bundle;
    Bundle appOpenBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SyncAdapter.initializeSyncAdapter(this);

        MobileAds.initialize(this, getString(R.string.banner_ad_unit_id));

            getSupportFragmentManager()

                    .beginTransaction()

                    .replace(R.id.fragment_container, new NotesFragment())

                    .commit();

            ButterKnife.bind(this);

            setSupportActionBar(mToolbar);

       getSupportActionBar().setDisplayShowTitleEnabled(false);

        rateme.show(this, getFragmentManager());
        appOpenBundle = new Bundle();

            appOpenBundle.putString(FirebaseAnalytics.Param.ORIGIN, "firebase test app open");

            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, MainActivity.this.appOpenBundle);

        mf.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, MainActivity.this.bundle);
                Intent intent = new Intent(MainActivity.this, NewNoteActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar,
                    R.string.drawer_open, R.string.drawer_close);
            mDrawer.addDrawerListener(mDrawerToggle);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
            mNavigationView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5,
                                           int i6, int i7) {

                    mNavigationView.removeOnLayoutChangeListener(this);

                    TextView header = (TextView) mNavigationView.findViewById(R.id.quote_of_the_day);
                    if (header != null) {
                        header.setText(QuotesPreference.getQuoteOfDay(MainActivity.this));
                    }
                }
            });
        }

    Fragment fragment;
    public void selectDrawerItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_support:
                fragment = new support();
                mf.setVisibility(View.INVISIBLE);
                break;
            case R.id.nav_share:
                fragment = new share();
                mf.setVisibility(View.INVISIBLE);
                break;
            case R.id.help:
                fragment = new Help();
                mf.setVisibility(View.INVISIBLE);
                break;
            case R.id.about:
                fragment = new developer();
                mf.setVisibility(View.INVISIBLE);
                break;
            case R.id.nav_habits_page:
            default:
                fragment = new NotesFragment();
                mf.setVisibility(View.VISIBLE);

        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();

        item.setChecked(true);

        setTitle(item.getTitle());

        mDrawer.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);

    }


    }

