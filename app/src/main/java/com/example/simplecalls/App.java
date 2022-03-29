package com.example.simplecalls;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;

import androidx.room.Room;

import com.google.android.material.color.DynamicColors;

import java.util.ArrayList;

public class App extends Application {
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        DynamicColors.applyToActivitiesIfAvailable(this);

    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    public static App getInstance() {
        return instance;
    }

}
