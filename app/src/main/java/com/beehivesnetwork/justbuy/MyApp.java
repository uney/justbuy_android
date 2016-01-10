package com.beehivesnetwork.justbuy;

import android.app.Application;

import com.beehivesnetwork.justbuy.network.ApiHandler;
import com.beehivesnetwork.justbuy.ui.utils.FileStorage;
import com.beehivesnetwork.justbuy.ui.utils.PreferencesUtils;

import timber.log.Timber;

/**
 * Created by froger_mcs on 05.11.14.
 */
public class MyApp extends Application {

    private static MyApp myApp;
    private PreferencesUtils preferencesUtils;
    private FileStorage fileStorage;
    private ApiHandler apiHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        Timber.plant(new Timber.DebugTree());
    }
    public static MyApp getMyApp(){
        return myApp;
    }

    public PreferencesUtils getPreferencesUtils(){
        if(preferencesUtils==null){
            preferencesUtils = new PreferencesUtils(myApp);
        }
        return preferencesUtils;
    }

    public FileStorage getFileStorage(){
        if(fileStorage==null){
            fileStorage = new FileStorage(myApp);
        }
        return fileStorage;
    }

    public ApiHandler getApiHandler(){
        if(apiHandler==null){
            apiHandler = new ApiHandler();
        }
        return apiHandler;
    }


}
