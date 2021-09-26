package com.example.autotracktransformproject;

import android.app.Application;

import com.walker.analytics.sdk.SensorsDataAPI;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initSensorsDataAPI(this);
    }

    private void initSensorsDataAPI(Application application) {
        SensorsDataAPI.init(application);
    }
}
