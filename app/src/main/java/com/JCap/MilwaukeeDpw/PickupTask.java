package com.JCap.MilwaukeeDpw;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class PickupTask extends AsyncTask<Void, Void, String> {
    private String getNextPickup() {
        Document doc = null;
        try {
            doc = Jsoup.connect("http://mpw.milwaukee.gov/services/garbage_day")
                    .data("laddr", "2220")
                    .data("sdir", "E")
                    .data("sname", "OKLAHOMA")
                    .data("stype", "AV")
                    .post();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String data = doc.getElementById("centerZone").html();

        return data;
    }

    @Override
    protected String doInBackground(Void... params) {
        return getNextPickup();
    }
}
