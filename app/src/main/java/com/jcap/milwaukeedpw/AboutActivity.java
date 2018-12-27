package com.jcap.milwaukeedpw;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.jcap.milwaukeedpw.utility.VersionHelper;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        WebView contentArea = findViewById(R.id.aboutContent);
        String aboutText = getString(R.string.about, VersionHelper.getVersion(this));
        contentArea.loadDataWithBaseURL(null, aboutText, "text/html", "utf-8", null);
    }

}