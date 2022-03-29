package com.example.simplecalls;

import static com.example.simplecalls.Constants.THEMES_LIST;
import static com.example.simplecalls.Constants.THEMES_LIST_ANDROID_S;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.google.android.material.color.DynamicColors;

public class ThemeUtils {
    private static String sTheme;

    public static void changeToTheme(Activity activity) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            sTheme = sPref.getString("theme", THEMES_LIST_ANDROID_S[0]);
        } else {
            sTheme = sPref.getString("theme", THEMES_LIST[0]);
        }
        activity.recreate();
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            sTheme = sPref.getString("theme", THEMES_LIST_ANDROID_S[0]);
            App currentApp = App.getInstance();
            switch (sTheme) {
                default:
                case "System":
                    DynamicColors.applyToActivitiesIfAvailable(currentApp);
                    sTheme = "System";
                    break;
                case "Light":
                    DynamicColors.applyToActivitiesIfAvailable(currentApp,
                            R.style.Theme_SimpleCalls);
                    sTheme = "Light";
                    break;
                case "Dark":
                    DynamicColors.applyToActivitiesIfAvailable(currentApp,
                            R.style.Theme_SimpleCalls_Dark);
                    sTheme = "Dark";
                    break;
            }
        } else {
            sTheme = sPref.getString("theme", THEMES_LIST[0]);
            App currentApp = App.getInstance();
            switch (sTheme) {
                default:
                case "Light":
                    activity.setTheme(R.style.Theme_SimpleCalls);
                    sTheme = "Light";
                    break;
                case "Dark":
                    activity.setTheme(R.style.Theme_SimpleCalls_Dark);
                    sTheme = "Dark";
                    break;
            }
        }

    }

    public static String getCurrentTheme() {
        return sTheme;
    }
}
