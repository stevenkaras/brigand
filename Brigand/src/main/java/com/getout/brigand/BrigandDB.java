package com.getout.brigand;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Persistent storage of queued transfers and results
 */
public class BrigandDB extends SQLiteOpenHelper {

    public void scheduleTransfer(QueuedTransfer transfer) {
        SQLiteDatabase db = getWritableDatabase();
        assert db != null;
        try {
            ContentValues values = new ContentValues();
            values.put("url", transfer.url);
            values.put("verb", transfer.verb);
            values.put("startTime", transfer.startedTime);
            db.insert("queuedTransfers", null, values);
        } finally {
            db.close();
        }
    }

    public static QueuedTransfer readQueuedTransferFromCursor(Cursor c) {
        //TODO: add partial projection handling
        QueuedTransfer result = new QueuedTransfer();
        int columnIndex = c.getColumnIndex("_id");
        result.id = c.getLong(columnIndex);
        columnIndex = c.getColumnIndex("url");
        result.url = c.getString(columnIndex);
        columnIndex = c.getColumnIndex("verb");
        result.verb = c.getString(columnIndex);
        columnIndex = c.getColumnIndex("startTime");
        result.startedTime = c.getLong(columnIndex);
        return result;
    }

    private static final int VERSION = 1;

    public BrigandDB(Context context) {
        super(context, "brigand", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onUpgrade(db, 0, VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int from, int to) {
        Log.i("BrigandDB", String.format("upgrading db from %d to %d", from, to));
        if (to < 1) return;
        if (from < 1) {
            db.execSQL("CREATE TABLE queuedTransfers (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "url TEXT," +
                    "verb TEXT," +
                    "startTime INTEGER" +
                    ")");
        }
        if (to > 1) throw new RuntimeException("unrecognized BrigandDB version");
    }
}
