package com.jcap.milwaukeedpw.utility;

import android.os.Bundle;
import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics {
    public static void Log(FirebaseAnalytics firebaseAnalytics, String contentType, String itemId) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
