package com.getout.brigand;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.getout.brigand.conditions.Battery;
import com.getout.brigand.conditions.Network;

public class BrigandService extends IntentService {

    private BrigandDB db;

    public BrigandService() {
        super("brigand");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = new BrigandDB(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if ("com.getout.brigand.QUEUE".equals(intent.getAction())) {
            long time = System.currentTimeMillis();
            String verb = intent.getStringExtra("com.getout.brigand.verb");
            String url = intent.getStringExtra("com.getout.brigand.url");
            String payload = intent.getStringExtra("com.getout.brigand.payload");
            db.scheduleTransfer(new QueuedTransfer(url, verb, payload, time));
        }
        executeQueuedTransfers();
    }

    private void executeQueuedTransfers() {
        if (deferTransfers()) return;
        QueuedTransfer transfer;
        while ((transfer = db.getNextTransfer()) != null) {
            QueuedTransfer.TransferResult result = transfer.executeTransfer();
            if (result.errorInTransfer) {
                db.scheduleTransfer(transfer);
                Log.e("Brigand", "Error in transfer: " + result.errorMessage);
                break;
            }
        }
        SharedPreferences prefs = getSharedPreferences("brigand", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("lastQueuePump", System.currentTimeMillis() / 1000);
        editor.commit();
    }

    private boolean deferTransfers() {
        int cost = Network.dataCost(this);

        // cost is divided by 2 for every 30 minutes since the last transfer
        SharedPreferences prefs = getSharedPreferences("brigand", MODE_PRIVATE);
        long lastPump = prefs.getLong("lastQueuePump", 0);
        if (lastPump == 0) {
            lastPump = System.currentTimeMillis() / 1000;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("lastQueuePump", System.currentTimeMillis() / 1000);
            editor.commit();
        }
        // determine the number of cost periods we've passed
        long elapsedPeriods = ((System.currentTimeMillis() / 1000) - lastPump) / (30 * 60);
        if (elapsedPeriods == 0) return true; // never transfer in the first 30 minutes
        // otherwise, adjust the cost based on how long we've been waiting
        cost = cost >> elapsedPeriods;

        // cost cannot be below 50
        cost = cost < 50 ? 50 : cost;

        int supply = batteryPolicy();
        return cost < supply;
    }

    /**
     * @return 0-1000 for the amount of battery we're willing to burn. 0 is none, 1000 is a lot
     */
    private int batteryPolicy() {
        int chargeSource = Battery.chargeSource(this);
        if (chargeSource == Battery.CHARGE_SOURCE_NONE) {
            int level = Battery.level(this);
            if (level < 10) {
                return 0;
            } else if (level < 20) {
                return 100;
            } else if (level < 50) {
                return 200;
            } else if (level < 80) {
                return 500;
            } else {
                return 800;
            }
        } else {
            int level = Battery.level(this);
            if (level < 30) {
                return 400;
            } else if (level < 80) {
                return 800;
            } else {
                return 1000;
            }
        }
    }

}
