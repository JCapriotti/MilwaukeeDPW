package com.jcap.milwaukeedpw;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class RequestServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_service);
        setupActionBar();

        TextView text_garbage = (TextView)findViewById(R.id.request_garbage);
        TextView text_street = (TextView)findViewById(R.id.request_street);
        TextView text_snow = (TextView)findViewById(R.id.request_snow);

        text_garbage.setMovementMethod(LinkMovementMethod.getInstance());
        text_street.setMovementMethod(LinkMovementMethod.getInstance());
        text_snow.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

}
