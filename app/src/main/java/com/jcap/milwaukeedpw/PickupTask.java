package com.jcap.milwaukeedpw;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PickupTask extends AsyncTask<PickupLocationParameters, Void, String> {
    private String getNextPickup(PickupLocationParameters params) {
        Document doc = null;
        try {
            doc = Jsoup.connect("http://mpw.milwaukee.gov/services/garbage_day")
                    .data("laddr", params.houseNumber)
                    .data("sdir", params.directional)
                    .data("sname", params.street)
                    .data("stype", params.suffix)
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
    protected String doInBackground(PickupLocationParameters... params) {
        return getNextPickup(params[0]);
    }
}

class PickupLocationParameters {
    String houseNumber;
    String directional;
    String street;
    String suffix;

    PickupLocationParameters(String houseNumber, String directional, String street, String suffix) {
        this.houseNumber = houseNumber;
        this.directional = directional;
        this.street = street;
        this.suffix = suffix;
    }
}
