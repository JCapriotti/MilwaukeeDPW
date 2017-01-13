package com.jcap.milwaukeedpw;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle(getResources().getString(R.string.app_name));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        loadDisplay(sharedPref);
    }

    

    private void loadDisplay(SharedPreferences prefs) {
        TextView nextPickupText = (TextView)findViewById(R.id.nextPickupText);
        TextView updatedText = (TextView)findViewById(R.id.pickupUpdated);

        String houseNumber = prefs.getString(getString(R.string.pref_key_houseNumber), "");
        String directional = prefs.getString(getString(R.string.pref_key_directional), "");
        String street = prefs.getString(getString(R.string.pref_key_street), "");
        String suffix = prefs.getString(getString(R.string.pref_key_suffix), "");

        if (houseNumber.isEmpty() || directional.isEmpty() || street.isEmpty() || suffix.isEmpty()) {
            updatedText.setText("");
            nextPickupText.setText(R.string.no_address);
            return;
        }

        PickupLocationParameters params = new PickupLocationParameters(houseNumber, directional, street, suffix);

        PickupTask task = new PickupTask();
        AsyncTask<PickupLocationParameters, Void, String> taskResult = task.execute(params);

        String updatedTime = getString(R.string.updated) + new SimpleDateFormat("M/d HH:mm:ss", Locale.US).format(new Date());
        try {
            nextPickupText.setMovementMethod(LinkMovementMethod.getInstance());
            nextPickupText.setText(Html.fromHtml(taskResult.get()));
            updatedText.setText(updatedTime);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        loadDisplay(prefs);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent intent = new Intent();
            intent.setClassName(this, "com.jcap.milwaukeedpw.AppSettingsActivity");
            startActivity(intent);
        }
        else if (id == R.id.nav_about) {
            Intent intent = new Intent();
            intent.setClassName(this, "com.jcap.milwaukeedpw.AboutActivity");
            startActivity(intent);
        }
        else if (id == R.id.nav_donate) {
            Intent intent = new Intent();
            intent.setClassName(this, "com.jcap.milwaukeedpw.DonateActivity");
            startActivity(intent);
        }
        else if (id == R.id.nav_request) {
            Intent intent = new Intent();
            intent.setClassName(this, "com.jcap.milwaukeedpw.RequestServiceActivity");
            startActivity(intent);
        }
        else if (id == R.id.nav_alerts) {
            Intent intent = new Intent();
            intent.setClassName(this, "com.jcap.milwaukeedpw.RequestServiceActivity");
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
