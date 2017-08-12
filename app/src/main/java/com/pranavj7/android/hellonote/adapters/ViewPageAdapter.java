package com.pranavj7.android.hellonote.adapters;

import android.support.v4.app.FragmentPagerAdapter;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import com.pranavj7.android.hellonote.R;
import com.pranavj7.android.hellonote.ui.NotesDetailFragment;
import com.pranavj7.android.hellonote.ui.TodoFragment;

import java.util.ArrayList;

public class ViewPageAdapter extends FragmentPagerAdapter {
    private final ArrayList<Pair<String, Fragment>> mTabs = new ArrayList<>();

    public ViewPageAdapter(FragmentManager fragmentManager, Context context, int habitId) {
        super(fragmentManager);
        mTabs.add(Pair.create(context.getString(R.string.tab_note_details),
                (Fragment) NotesDetailFragment.newInstance(habitId)));
        mTabs.add(Pair.create(context.getString(R.string.tab_todo),
                (Fragment) TodoFragment.newInstance(habitId)));
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mTabs.get(position).second;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return mTabs.get(position).first;
    }
}
