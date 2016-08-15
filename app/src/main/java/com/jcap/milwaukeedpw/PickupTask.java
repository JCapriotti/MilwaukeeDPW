package com.jcap.milwaukeedpw;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PickupTask extends AsyncTask<PickupLocationParameters, Void, String> {
    private String getNextPickup(PickupLocationParameters params) {
        Document doc = null;
        try {
            // All inputs need to be upper-case, the only short-cut I took was with the street name
            // as I didn't want to have separate "friendly names" and non-friendly.
            doc = Jsoup.connect("http://mpw.milwaukee.gov/services/garbage_day?embed=Y")
                    .data("laddr", params.houseNumber)
                    .data("sdir", params.directional)
                    .data("sname", params.street.toUpperCase())
                    .data("stype", params.suffix)
                    .post();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (doc == null)
            return "Unable to get data";
        else
            return doc.html();
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
