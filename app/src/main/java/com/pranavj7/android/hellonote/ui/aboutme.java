package com.pranavj7.android.hellonote.ui;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.pranavj7.android.hellonote.R;
import butterknife.ButterKnife;
/**
 * Created by pranavj7 on 4/13/2017.
 */
public class aboutme extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about, container, false);
        ButterKnife.bind(this, rootView);
        ImageButton myButton1 = (ImageButton) rootView.findViewById(R.id.gp);
        ImageButton myButton2 = (ImageButton) rootView.findViewById(R.id.lk);
        myButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/105539629296001895841")));
            }

        });
        myButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/pranavjayaraj"));
                    startActivity(intent);
                            }

        });
        return rootView;
    }

}