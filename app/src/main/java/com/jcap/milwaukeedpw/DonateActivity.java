package com.jcap.milwaukeedpw;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jcap.milwaukeedpw.iab.IabHelper;
import com.jcap.milwaukeedpw.iab.IabResult;
import com.jcap.milwaukeedpw.iab.Inventory;
import com.jcap.milwaukeedpw.iab.Purchase;
import com.jcap.milwaukeedpw.utility.VersionHelper;

import java.util.ArrayList;
import java.util.List;

public class DonateActivity extends AppCompatActivity {

    static final String SKU_DONATE_LOW = "donate_low";
    static final String SKU_DONATE_MEDIUM = "donate_medium";
    static final String SKU_DONATE_HIGH = "donate_high";
    static final String SKU_TEST = "android.test.purchased";

    static final String TAG = "MilwaukeeGarbage";

    static final int PURCHASE_REQUEST_CODE = 10000;

    IabHelper mHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        setupActionBar();
        setupIab();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    public void onSubmitClicked(View view){
        RadioGroup optionGroup = findViewById(R.id.donate_options);
        int optionId = optionGroup.getCheckedRadioButtonId();
        String selectedSku = "";

        switch (optionId) {
            case R.id.radio_low:
                selectedSku = SKU_DONATE_LOW;
                break;
            case R.id.radio_medium:
                selectedSku = SKU_DONATE_MEDIUM;
                break;
            case R.id.radio_high:
                selectedSku = SKU_DONATE_HIGH;
                break;
            case -1:
                return;
        }

        if (VersionHelper.getIsPreRelease(this)) {
            selectedSku = SKU_TEST;
        }

        try {
            mHelper.launchPurchaseFlow(this, selectedSku, PURCHASE_REQUEST_CODE, mPurchaseFinishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.d(TAG, e.toString());
        }
    }

    public void onRadioButtonClicked(View view) {
        Button button = findViewById(R.id.donate_button);
        button.setEnabled(true);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
                    updateDisplayWithBillingProblem(result.toString());
                }
                else {
                    try {
                        setupItems();
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        Log.d(TAG, e.toString());
                    }
                }
            }
        });
    }

    private void updateDisplayWithBillingProblem(String result) {
        Button button = findViewById(R.id.donate_button);
        button.setEnabled(false);

        TextView alert = findViewById(R.id.donate_alert);
        alert.setText(getResources().getString(R.string.donate_alert, result));
        alert.setVisibility(View.VISIBLE);
    }

    private void setupItems() throws IabHelper.IabAsyncInProgressException {
        List<String> skuList = new ArrayList<>();
        skuList.add(SKU_DONATE_LOW);
        skuList.add(SKU_DONATE_MEDIUM);
        skuList.add(SKU_DONATE_HIGH);
        mHelper.queryInventoryAsync(true, skuList, null, mQueryFinishedListener);
    }

    IabHelper.QueryInventoryFinishedListener
            mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory)
        {
            if (result.isFailure()) {
                Log.d(TAG, result.getMessage());
                return;
            }

            consumeTestProducts(inventory);
            loadProductsFromInventory(inventory);
        }
    };

    public void loadProductsFromInventory(Inventory inventory) {
        String lowPrice = inventory.getSkuDetails(SKU_DONATE_LOW).getPrice();
        String mediumPrice = inventory.getSkuDetails(SKU_DONATE_MEDIUM).getPrice();
        String highPrice = inventory.getSkuDetails(SKU_DONATE_HIGH).getPrice();

        String lowDescription = inventory.getSkuDetails(SKU_DONATE_LOW).getDescription();
        String mediumDescription = inventory.getSkuDetails(SKU_DONATE_MEDIUM).getDescription();
        String highDescription = inventory.getSkuDetails(SKU_DONATE_HIGH).getDescription();

        RadioButton radioLow = findViewById(R.id.radio_low);
        RadioButton radioMedium = findViewById(R.id.radio_medium);
        RadioButton radioHigh = findViewById(R.id.radio_high);

        radioLow.setText(String.format("%s - %s", lowPrice, lowDescription));
        radioLow.setEnabled(true);
        radioMedium.setText(String.format("%s - %s", mediumPrice, mediumDescription));
        radioMedium.setEnabled(true);
        radioHigh.setText(String.format("%s - %s", highPrice, highDescription));
        radioHigh.setEnabled(true);
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)  {
            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);
            }
            else {
                try {
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
                showThankYou();
            }
        }
    };

    private void showThankYou() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.donate_thankyou_title))
                .setMessage(getResources().getString(R.string.donate_thankyou_message))
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase, IabResult result) {
                    if (!result.isSuccess()) {
                        Log.d(TAG, "Unable to consume product");
                        Log.d(TAG, result.getMessage());
                    }
                }
            };

    private void consumeTestProducts(Inventory inventory) {
        if (VersionHelper.getIsPreRelease(this)) {
            try {
                Purchase purchase = inventory.getPurchase(SKU_TEST);
                if (purchase != null)
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
    }
}
