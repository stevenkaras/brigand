package com.getout.brigand;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents a queued transfer
 */
public class QueuedTransfer {

    public long id;
    public long startedTime;
    public String url;
    public String verb;

    public QueuedTransfer(String url, String verb, long startedTime) {
        this.id = -1;
        this.url = url;
        this.verb = verb;
        this.startedTime = startedTime;
    }

    public QueuedTransfer() {
    }

    public static class TransferResult {
        public boolean errorInRequest = false;
        public boolean errorInTransfer = false;
        public String errorMessage = null;
        public int code = -1;
        public String body = null;
    }

    public TransferResult executeTransfer() {
        TransferResult result = new TransferResult();
        try {
            Log.v("Brigand", String.format("About to GET <%s>", url));
            HttpURLConnection hurl = (HttpURLConnection) new URL(url).openConnection();
            hurl.setRequestMethod(verb);
            result.body = hurl.getResponseMessage();
            result.code = hurl.getResponseCode();
            Log.v("Brigand", String.format("Got result <%s>", result));
            return result;
        } catch (MalformedURLException e) {
            result.errorInRequest = true;
            return result;
        } catch (IOException e) {
            result.errorInTransfer = true;
            result.errorMessage = e.getMessage();
            return result;
        }
    }

}
