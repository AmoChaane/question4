package com.example.question4.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "inventory";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
//        id, name, price, stock, category - products table
//        id, productId (FK), quantity, totalPrice, saleDate - sales table

        String query = "CREATE TABLE products" + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "price INTEGER, "
                + "stock INTEGER, "
                + "category TEXT)";

        String query2 = "CREATE TABLE sales (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "productId INTEGER, " +
                "quantity INTEGER, " +
                "totalPrice REAL, " +
                "saleDate INTEGER, " +
                "FOREIGN KEY(productId) REFERENCES products(id)" +
                ")";

        db.execSQL(query);
        db.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
