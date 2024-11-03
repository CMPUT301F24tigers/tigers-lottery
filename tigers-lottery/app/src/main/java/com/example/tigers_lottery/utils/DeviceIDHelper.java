package com.example.tigers_lottery.utils;

import android.content.Context;
import android.provider.Settings;

public class DeviceIDHelper {
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
