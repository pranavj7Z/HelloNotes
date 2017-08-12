package com.pranavj7.android.hellonote.ui;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pranavj7.android.hellonote.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * Created by pinkzz on 3/19/2017.
 */
public final class share extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_share, container, false);

        ButterKnife.bind(this, rootView);
        ImageButton myButton = (ImageButton) rootView.findViewById(R.id.helloo1);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                // Add data to the intent, the receiving app will decide
                // what to do with it.
                share.putExtra(Intent.EXTRA_SUBJECT, "HelloNotes");
                share.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=com.pranavj7.android.hellonote");
                startActivity(Intent.createChooser(share, "Share link!"));


            }
        });
        return rootView;
    }
}



