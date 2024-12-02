package com.example.tigers_lottery.utils;

import android.content.Context;
import android.provider.Settings;

/**
 * Helper class to find the device id of the user.
 */

public class DeviceIDHelper {
    /**
     * Constructor for finding the user's device id.
     *
     * @param context app context.
     * @return the user's deviceId.
     */
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
