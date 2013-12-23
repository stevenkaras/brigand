package com.getout.brigand.conditions;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Created by steven on 12/23/13.
 */
public class Battery {

    private static Intent batteryStatus(Context context) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, intentFilter);
        return batteryStatus;
    }

    /**
     * Get the battery level as a percentage
     */
    public static int level(Context context) {
        Intent batteryStatus = batteryStatus(context);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;
        return (int) (batteryPct * 100);
    }

    public static final int CHARGE_SOURCE_NONE = 0;
    public static final int CHARGE_SOURCE_USB = 0;
    public static final int CHARGE_SOURCE_AC = 0;
    public static final int CHARGE_SOURCE_UNKNOWN = 0;
    /**
     * Get a constant of whether the battery is being discharged or not
     *
     * @see #CHARGE_SOURCE_NONE
     * @see #CHARGE_SOURCE_USB
     * @see #CHARGE_SOURCE_AC
     * @see #CHARGE_SOURCE_UNKNOWN
     * @return one of the CHARGE_SOURCE constants
     */
    public static int chargeSource(Context context) {
        Intent batteryStatus = batteryStatus(context);
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        int chargeSource = CHARGE_SOURCE_NONE;
        if (chargePlug == BatteryManager.BATTERY_PLUGGED_USB) {
            chargeSource = CHARGE_SOURCE_USB;
        } else if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC) {
            chargeSource = CHARGE_SOURCE_AC;
        } else {
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            if (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL)
                chargeSource = CHARGE_SOURCE_UNKNOWN;
        }
        return chargeSource;
    }
}
