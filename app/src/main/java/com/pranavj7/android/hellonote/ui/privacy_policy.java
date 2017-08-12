package com.pranavj7.android.hellonote.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.pranavj7.android.hellonote.R;

import butterknife.ButterKnife;

/**
 * Created by pinkzz on 3/26/2017.
 */
public class privacy_policy extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_privacy, container, false);

        ButterKnife.bind(this, rootView);
        return rootView;
    }
}

