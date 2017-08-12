package com.pranavj7.android.hellonote.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pranavj7.android.hellonote.R;

import butterknife.ButterKnife;

/**
 * Created by pranavj7 on 4/16/2017.
 */
public class Help extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.help, container, false);

        ButterKnife.bind(this, rootView);
        return rootView;
    }

}
