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
import android.util.Base64;
import android.util.Log;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.TextView;

import com.jcap.milwaukeedpw.iab.IabHelper;
import com.jcap.milwaukeedpw.iab.IabResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    static final String TAG = "MilwaukeeGarbage";

    IabHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupIab();

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

    private String getPublicKey() {
        String key = "CB73bfrRbx6aN9uagSjKniasH8NNDccAhAVjCLqGeGjdH2DyWqA4NqlXAok1Ocy9Pq2luR4/rbTULgkncjzQvJV3QZGtpjewQkl4/36kwipXKb8NBc6DiLbZmWUnllJhD4EeFcNeK3IP7KBSzA3+ZMwABE7wvIMwsGI53xAjs0FTtYftxJOYl6eUZDlssTxZeLhh+DlDkW5RGQOfjsF8HdIpF72EeXFnhHWFfvH4YhhpTnKzTznVwzNGmorz7UfRsvVL1sn0Y1UHVVyC9A21oxdSRW3D66pCCfz1xD8KZVsM49PC/tqljpIAKZivh2yj4YxXesXNT5UjUQPF7Sd1nlpQbbFrqml6N4GZAhK/k1qjxvQ3aLxZg7TOc32VfSsLl20PuxCN";
        String keykey = "OJz2T8rcaRewsZMcdiXLnyepH8DPDMgAtIdiArgEeWnd91D1Vn7ZPlGgvE3rtbu7NNCsUemHTUJpDF4wmTrm3anAfGt7YCtzUnOfyC3HoqM0mrNo1e19qwoTGQCawxG3BtZjlF8uAXB6kOtW3SB5fO96VgXRpbEmtn90CHnPlFhHkKlBzq32kzmTef0YN2tkP3X1UrYt0BQFwGLm4loBEVxVujfbCopIDfsHOridA1LUz3GgPt8Ox9RPOpWBspV0tJwZN21A7IyNazCDqtBZA1MrLugM4HgHdByd73OngHe47jWROaSxcxvsWmdpCCHZMduxHUmp5Df0UUq5e2MNQ1MIeBnqYJGDHLRaKAlAZZjfcIoOVUOVgYVGGlFdwfd5qG8MuhCM";

        int length = 294;
        byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);
        byte[] xorBytes = Base64.decode(keykey, Base64.DEFAULT);
        byte[] result = new byte[length];


        for (int i = 0; i < length; ++i) {
            result[i] = (byte)(keyBytes[i] ^ xorBytes[i]);
        }
        return Base64.encodeToString(result, Base64.DEFAULT);
    }

    private void setupIab() {
        String base64EncodedPublicKey = getPublicKey();

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                }
                // Hooray, IAB is fully set up!
            }
        });
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;
    }

}
