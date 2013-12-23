package com.getout.brigand;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BrigandReceiver extends BroadcastReceiver {
    public BrigandReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Brigand", String.format("received %s", intent.getAction()));
        // doesn't really matter what woke us up, just poke the service
        Intent i = new Intent();
        i.setAction("com.getout.brigand.POKE_SERVICE");
        i.setClass(context, BrigandService.class);
        context.startService(i);
    }
}
