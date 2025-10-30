package com.example.question4.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DataSource {
    private SQLiteDatabase database;
    private DBHandler dbHandler;

    public DataSource(Context context) {
        dbHandler = new DBHandler(context);
    }

    public void open() {
        database = dbHandler.getWritableDatabase();
    }

    public void close() {
        dbHandler.close();
    }
}
