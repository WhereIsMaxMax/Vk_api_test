package com.whrsmxmx.vk_api_test;

import android.app.Application;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Max on 13.03.2017.
 */

public class App extends Application {

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                Toast.makeText(App.this, "INVALID ACCESS TOKEN", Toast.LENGTH_LONG).show();
//                 in real project - redirect to special login activity;
            }
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
    }
}
