package com.getout.brigand;

import android.content.Context;
import android.content.Intent;

import java.util.Map;

/**
 * API class for Brigand.
 *
 * You can use the functions in here to schedule transfers
 */
public class Brigand {

    private Context context;

    /**
     * Create a new Brigand queue for sending messages
     *
     * @param context
     */
    public Brigand(Context context) {
        this.context = context;
    }

    /**
     * Perform a GET HTTP request to the given endpoint. Will not store a result for later retrieval
     */
    public void get(String endpoint) {
        httpWithoutResult("GET", endpoint, null);
    }

    public void post(String endpoint, String payload) {
        httpWithoutResult("POST", endpoint, payload);
    }

    public void put(String endpoint, String payload) {
        httpWithoutResult("PUT", endpoint, payload);
    }

    private void httpWithoutResult(String verb, String url, String payload) {
        // get brigand service, and transmit the request
        Intent i = new Intent();
        i.setAction("com.getout.brigand.QUEUE");
        i.setClass(context.getApplicationContext(), BrigandService.class);
        i.putExtra("com.getout.brigand.url", url);
        i.putExtra("com.getout.brigand.verb", verb);
        i.putExtra("com.getout.brigand.payload", payload);
        context.startService(i);
    }

}
