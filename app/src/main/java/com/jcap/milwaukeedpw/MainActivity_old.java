package com.jcap.milwaukeedpw;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;

import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class MainActivity_old extends AppCompatActivity
    {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_old);

        TextView nextPickupText = (TextView)findViewById(R.id.nextPickupText);

        PickupTask task = new PickupTask();
        AsyncTask<Void, Void, String> taskResult = task.execute();


        try {
            nextPickupText.setText(Html.fromHtml(taskResult.get()));

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}