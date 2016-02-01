package com.jcap.milwaukeedpw;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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

        if (doc == null)
            return "Unable to get data";
        else
            return doc.getElementById("centerZone").html();
    }

    @Override
    protected String doInBackground(Void... params) {
        return getNextPickup();
    }
}
