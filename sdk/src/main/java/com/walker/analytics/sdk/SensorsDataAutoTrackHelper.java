package com.walker.analytics.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.View;

import androidx.annotation.Keep;

import org.json.JSONException;
import org.json.JSONObject;

public class SensorsDataAutoTrackHelper {

    private static final String TAG = "SensorsDataAutoTrackHelper";

    @SuppressLint("LongLogTag")
    @Keep
    public static void trackViewOnClick(View view) {
        try {

            if (SensorsDataAPI.getInstance() == null) {
                Log.e(TAG, "[trackViewOnClick] SensorsDataAPI haven't initialization.");
                return;
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("$element_type", SensorsDataPrivate.getElementType(view));
            jsonObject.put("$element_id", SensorsDataPrivate.getViewId(view));

            Activity activity = SensorsDataPrivate.getActivityFromView(view);
            if (activity != null) {
                jsonObject.put("$activity", activity.getClass().getCanonicalName());
            }

            SensorsDataAPI.getInstance().track("$AppClick", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
