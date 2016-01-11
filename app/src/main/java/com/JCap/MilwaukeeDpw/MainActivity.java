package com.JCap.MilwaukeeDpw;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView nextPickupText = (TextView)findViewById(R.id.nextPickupText);
        TextView updatedText = (TextView)findViewById(R.id.pickupUpdated);

        PickupTask task = new PickupTask();
        AsyncTask<Void, Void, String> taskResult = task.execute();

        String updatedTime = "Updated: " + new SimpleDateFormat("M/d HH:mm:ss").format(new Date());

        try {
            nextPickupText.setText(Html.fromHtml(taskResult.get()));
            updatedText.setText(updatedTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
