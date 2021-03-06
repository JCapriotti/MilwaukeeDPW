package com.jcap.milwaukeedpw;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.jcap.milwaukeedpw.utility.VersionHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private Integer resetClickCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle(getResources().getString(R.string.app_name));

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        loadDisplay(sharedPref);

        resetClickCounter = 0;

        checkFirstRun();
    }

    public void checkFirstRun() {
        String version = VersionHelper.getVersion(this);
        String preferenceKey = String.format("isFirstRun_%s", version);
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean(preferenceKey, true);
        if (isFirstRun) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.new_version_title)
                    .setMessage(R.string.new_version_details)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean(preferenceKey,
                            false)
                    .apply();
        }
    }

    private void loadDisplay(SharedPreferences prefs) {
        RelativeLayout main_nextPickup = findViewById(R.id.main_nextPickup);
        RelativeLayout main_noAddress = findViewById(R.id.main_noAddress);
        TextView nextPickupText = findViewById(R.id.nextPickupText);
        TextView updatedText = findViewById(R.id.pickupUpdated);

        String houseNumber = prefs.getString(getString(R.string.pref_key_houseNumber), "");
        String directional = prefs.getString(getString(R.string.pref_key_directional), "");
        String street = prefs.getString(getString(R.string.pref_key_street), "");
        String suffix = prefs.getString(getString(R.string.pref_key_suffix), "");

        if (houseNumber.isEmpty() || directional.isEmpty() || street.isEmpty() || suffix.isEmpty()) {
            main_nextPickup.setVisibility(View.GONE);
            main_noAddress.setVisibility(View.VISIBLE);
            return;
        }
        else {
            main_nextPickup.setVisibility(View.VISIBLE);
            main_noAddress.setVisibility(View.GONE);
        }

        PickupLocationParameters params = new PickupLocationParameters(houseNumber, directional, street, suffix);

        PickupTask task = new PickupTask();
        AsyncTask<PickupLocationParameters, Void, String> taskResult = task.execute(params);

        String formattedTime = new SimpleDateFormat("M/d hh:mm aa", Locale.US).format(new Date());
        String updatedTime = String.format("%s %s", getString(R.string.updated), formattedTime);
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent intent = new Intent();
            intent.setClassName(this, "com.jcap.milwaukeedpw.AppSettingsActivity");
            startActivity(intent);
        }
        else if (id == R.id.nav_about) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.jasonmke.com/garbage")));
        }
        else if (id == R.id.nav_request) {
            Intent intent = new Intent();
            intent.setClassName(this, "com.jcap.milwaukeedpw.RequestServiceActivity");
            startActivity(intent);
        }
        else if (id == R.id.nav_alerts) {
            Intent intent = new Intent();
            intent.setClassName(this, "com.jcap.milwaukeedpw.AlertActivity");
            startActivity(intent);
        }
        else if (id == R.id.nav_review) {
            openAppStore();
        }
        else if (id == R.id.nav_share) {
            share();
        }
        else if (id == R.id.nav_recycling) {
            openRecyclingInfo();
        }

        item.setChecked(false);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void share() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT,
                "Check out the Milwaukee Garbage schedule app: https://play.google.com/store/apps/details?id=com.jcap.milwaukeedpw");
        intent.setType("text/plain");
        startActivity(intent);
    }

    public void openRecyclingInfo() {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.jasonmke.com/garbage/recycling")));
    }

    public void openAppStore() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }
        else{
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }

        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    public void onPickupTextClick(View view) {
        showDeleteDataOption();
    }

    public void onOpenSettingsClick(View view) {
        Intent intent = new Intent();
        intent.setClassName(this, "com.jcap.milwaukeedpw.AppSettingsActivity");
        startActivity(intent);
    }

    private void showDeleteDataOption() {
        if (++resetClickCounter == 7) {
            resetClickCounter = 0;
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.reset_dialog_title)
                    .setMessage(R.string.reset_dialog_message)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            PreferenceManager
                                    .getDefaultSharedPreferences(getApplicationContext()).edit().clear().apply();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
    }

    public void onReviewClick(View view) {
        openAppStore();
    }

}
