package com.jcap.milwaukeedpw;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RequestServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_service);
        setupActionBar();

        int[] links = {R.id.request_garbage, R.id.request_street, R.id.request_snow,
                R.id.request_parking};

        for (int link: links) {
            TextView textView = (TextView)findViewById(link);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

}
