package com.jcap.milwaukeedpw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.webkit.WebView;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        WebView contentArea = (WebView)findViewById(R.id.aboutContent);
        String aboutText = getString(R.string.about);
        contentArea.loadDataWithBaseURL(null, aboutText, "text/html", "utf-8", null);
    }
}
