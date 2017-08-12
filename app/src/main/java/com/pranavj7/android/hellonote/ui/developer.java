package com.pranavj7.android.hellonote.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.pranavj7.android.hellonote.R;

import butterknife.ButterKnife;

/**
 * Created by pranavj7 on 4/20/2017.
 */
public final class developer extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dev, container, false);
        ButterKnife.bind(this, rootView);
        CardView myButton1 = (CardView) rootView.findViewById(R.id.dev7);
        myButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment;
                fragment = new aboutme();
                getFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });
        return rootView;
    }
}