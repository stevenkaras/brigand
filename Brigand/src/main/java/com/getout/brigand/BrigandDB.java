package com.getout.brigand;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
            values.put("payload", transfer.payload);
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
        columnIndex = c.getColumnIndex("payload");
        result.payload = c.getString(columnIndex);
        return result;
    }

    public QueuedTransfer getNextTransfer() {
        SQLiteDatabase db = getWritableDatabase();
        assert db != null;
        try {
            Cursor c = db.query("queuedTransfers", null, null, null, null, null, "startTime ASC", "1");
            if (c.getCount() == 0) {
                return null;
            }
            c.moveToFirst();
            QueuedTransfer result = readQueuedTransferFromCursor(c);
            // delete the transfer from the db after
            db.delete("queuedTransfers", "_id = ?", new String[] { String.valueOf(result.id) });
            return result;
        } finally {
            db.close();
        }
    }

    /**
     * @return the number of queued transfers
     */
    public int getQueueSize() {
        SQLiteDatabase db = getReadableDatabase();
        assert db != null;
        try {
            Cursor c = db.rawQuery("SELECT COUNT(*) FROM queuedTransfers", null);
            c.moveToFirst();
            return c.getInt(0);
        } finally {
            db.close();
        }
    }

    private static final int VERSION = 2;

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
        if (to < 2) return;
        if (from < 2) {
            db.execSQL("ALTER TABLE queuedTransfers ADD COLUMN payload TEXT");
        }
        if (to > 2) throw new RuntimeException("unrecognized BrigandDB version");
    }
}
