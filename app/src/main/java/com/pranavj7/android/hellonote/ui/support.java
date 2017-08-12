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
 * Created by pinkzz on 3/19/2017.
 */
public final class support extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_support, container, false);
        ButterKnife.bind(this, rootView);
        ImageButton myButton1 = (ImageButton) rootView.findViewById(R.id.helloo1);
        ImageButton myButton = (ImageButton) rootView.findViewById(R.id.helloo);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent.setType("vnd.android.cursor.item/email");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"connectpranavjayaraj@gmail.com"});
                startActivity(Intent.createChooser(emailIntent, "Send mail using..."));
            }
        });
        myButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment;
                fragment = new privacy_policy();
                getFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });
return rootView;
    }
}