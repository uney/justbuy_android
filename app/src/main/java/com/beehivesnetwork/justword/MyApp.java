package com.beehivesnetwork.justword;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by froger_mcs on 05.11.14.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
