package com.getout.brigand;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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
        long time = System.currentTimeMillis();
        String packageName = getPackageName();
        String verb = intent.getStringExtra(packageName + ".verb");
        String url = intent.getStringExtra(packageName + ".url");
        db.scheduleTransfer(new QueuedTransfer(url, verb, time));
    }

}
