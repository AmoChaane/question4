package com.example.question4.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.question4.models.Product;
import com.example.question4.models.Sale;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "inventory.db";
    private static final int DB_VERSION = 1;

    // Tables
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_SALES = "sales";

    // Products Table Columns
    private static final String PRODUCT_ID = "id";
    private static final String PRODUCT_NAME = "name";
    private static final String PRODUCT_PRICE = "price";
    private static final String PRODUCT_STOCK = "stock";
    private static final String PRODUCT_CATEGORY = "category";

    // Sales Table Columns
    private static final String SALE_ID = "id";
    private static final String SALE_PRODUCT_ID = "productId";
    private static final String SALE_QUANTITY = "quantity";
    private static final String SALE_TOTAL_PRICE = "totalPrice";
    private static final String SALE_DATE = "saleDate";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " ("
                + PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PRODUCT_NAME + " TEXT NOT NULL, "
                + PRODUCT_PRICE + " REAL NOT NULL, "
                + PRODUCT_STOCK + " INTEGER NOT NULL, "
                + PRODUCT_CATEGORY + " TEXT NOT NULL)";

        String createSalesTable = "CREATE TABLE " + TABLE_SALES + " ("
                + SALE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SALE_PRODUCT_ID + " INTEGER NOT NULL, "
                + SALE_QUANTITY + " INTEGER NOT NULL, "
                + SALE_TOTAL_PRICE + " REAL NOT NULL, "
                + SALE_DATE + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + SALE_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + PRODUCT_ID + "))";

        db.execSQL(createProductsTable);
        db.execSQL(createSalesTable);

        // Insert sample data
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // Sample products
        ContentValues values = new ContentValues();
        
        values.put(PRODUCT_NAME, "Samsung Galaxy S21");
        values.put(PRODUCT_PRICE, 699.99);
        values.put(PRODUCT_STOCK, 25);
        values.put(PRODUCT_CATEGORY, "Electronics");
        db.insert(TABLE_PRODUCTS, null, values);

        values.clear();
        values.put(PRODUCT_NAME, "iPhone 13");
        values.put(PRODUCT_PRICE, 799.99);
        values.put(PRODUCT_STOCK, 15);
        values.put(PRODUCT_CATEGORY, "Electronics");
        db.insert(TABLE_PRODUCTS, null, values);

        values.clear();
        values.put(PRODUCT_NAME, "Nike Air Max");
        values.put(PRODUCT_PRICE, 129.99);
        values.put(PRODUCT_STOCK, 8);
        values.put(PRODUCT_CATEGORY, "Clothing");
        db.insert(TABLE_PRODUCTS, null, values);

        values.clear();
        values.put(PRODUCT_NAME, "Dell Laptop");
        values.put(PRODUCT_PRICE, 899.99);
        values.put(PRODUCT_STOCK, 12);
        values.put(PRODUCT_CATEGORY, "Electronics");
        db.insert(TABLE_PRODUCTS, null, values);

        values.clear();
        values.put(PRODUCT_NAME, "Coffee Beans");
        values.put(PRODUCT_PRICE, 24.99);
        values.put(PRODUCT_STOCK, 50);
        values.put(PRODUCT_CATEGORY, "Food");
        db.insert(TABLE_PRODUCTS, null, values);
    }

    // Product CRUD Operations
    public long addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(PRODUCT_NAME, product.getName());
        values.put(PRODUCT_PRICE, product.getPrice());
        values.put(PRODUCT_STOCK, product.getStock());
        values.put(PRODUCT_CATEGORY, product.getCategory());
        
        long result = db.insert(TABLE_PRODUCTS, null, values);
        db.close();
        return result;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " ORDER BY " + PRODUCT_NAME;
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_ID)));
                product.setName(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_NAME)));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(PRODUCT_PRICE)));
                product.setStock(cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_STOCK)));
                product.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_CATEGORY)));
                products.add(product);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return products;
    }

    public Product getProduct(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + PRODUCT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
        
        Product product = null;
        if (cursor.moveToFirst()) {
            product = new Product();
            product.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_ID)));
            product.setName(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_NAME)));
            product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(PRODUCT_PRICE)));
            product.setStock(cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_STOCK)));
            product.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_CATEGORY)));
        }
        
        cursor.close();
        db.close();
        return product;
    }

    public int updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(PRODUCT_NAME, product.getName());
        values.put(PRODUCT_PRICE, product.getPrice());
        values.put(PRODUCT_STOCK, product.getStock());
        values.put(PRODUCT_CATEGORY, product.getCategory());
        
        int result = db.update(TABLE_PRODUCTS, values, PRODUCT_ID + " = ?", 
                              new String[]{String.valueOf(product.getId())});
        db.close();
        return result;
    }

    public int deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_PRODUCTS, PRODUCT_ID + " = ?", 
                              new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    public List<Product> searchProducts(String searchTerm) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + PRODUCT_NAME + " LIKE ? OR " + PRODUCT_CATEGORY + " LIKE ?";
        String searchPattern = "%" + searchTerm + "%";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{searchPattern, searchPattern});
        
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_ID)));
                product.setName(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_NAME)));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(PRODUCT_PRICE)));
                product.setStock(cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_STOCK)));
                product.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_CATEGORY)));
                products.add(product);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return products;
    }

    public List<Product> getLowStockProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + PRODUCT_STOCK + " <= 10 ORDER BY " + PRODUCT_STOCK;
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_ID)));
                product.setName(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_NAME)));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(PRODUCT_PRICE)));
                product.setStock(cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_STOCK)));
                product.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_CATEGORY)));
                products.add(product);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return products;
    }

    // Sales CRUD Operations
    public long addSale(Sale sale) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(SALE_PRODUCT_ID, sale.getProductId());
        values.put(SALE_QUANTITY, sale.getQuantity());
        values.put(SALE_TOTAL_PRICE, sale.getTotalPrice());
        values.put(SALE_DATE, sale.getSaleDate());
        
        long result = db.insert(TABLE_SALES, null, values);
        
        // Update product stock
        if (result != -1) {
            updateProductStock(sale.getProductId(), -sale.getQuantity());
        }
        
        db.close();
        return result;
    }

    public List<Sale> getAllSales() {
        List<Sale> sales = new ArrayList<>();
        String query = "SELECT s.*, p." + PRODUCT_NAME + " FROM " + TABLE_SALES + " s " +
                      "JOIN " + TABLE_PRODUCTS + " p ON s." + SALE_PRODUCT_ID + " = p." + PRODUCT_ID + " " +
                      "ORDER BY s." + SALE_DATE + " DESC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor.moveToFirst()) {
            do {
                Sale sale = new Sale();
                sale.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SALE_ID)));
                sale.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(SALE_PRODUCT_ID)));
                sale.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_NAME)));
                sale.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(SALE_QUANTITY)));
                sale.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(SALE_TOTAL_PRICE)));
                sale.setSaleDate(cursor.getLong(cursor.getColumnIndexOrThrow(SALE_DATE)));
                sales.add(sale);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return sales;
    }

    private void updateProductStock(int productId, int quantityChange) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_PRODUCTS + " SET " + PRODUCT_STOCK + " = " + PRODUCT_STOCK + " + ? WHERE " + PRODUCT_ID + " = ?";
        db.execSQL(query, new Object[]{quantityChange, productId});
        db.close();
    }

    // Analytics Methods
    public int getTotalProductsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_PRODUCTS;
        Cursor cursor = db.rawQuery(query, null);
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        
        cursor.close();
        db.close();
        return count;
    }

    public double getTotalRevenue() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + SALE_TOTAL_PRICE + ") FROM " + TABLE_SALES;
        Cursor cursor = db.rawQuery(query, null);
        
        double revenue = 0;
        if (cursor.moveToFirst()) {
            revenue = cursor.getDouble(0);
        }
        
        cursor.close();
        db.close();
        return revenue;
    }

    public int getTotalSalesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_SALES;
        Cursor cursor = db.rawQuery(query, null);
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        
        cursor.close();
        db.close();
        return count;
    }

    public double getTotalInventoryValue() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + PRODUCT_PRICE + " * " + PRODUCT_STOCK + ") FROM " + TABLE_PRODUCTS;
        Cursor cursor = db.rawQuery(query, null);
        
        double value = 0;
        if (cursor.moveToFirst()) {
            value = cursor.getDouble(0);
        }
        
        cursor.close();
        db.close();
        return value;
    }

    public List<Sale> getSalesInDateRange(long startDate, long endDate) {
        List<Sale> sales = new ArrayList<>();
        String query = "SELECT s.*, p." + PRODUCT_NAME + " FROM " + TABLE_SALES + " s " +
                      "JOIN " + TABLE_PRODUCTS + " p ON s." + SALE_PRODUCT_ID + " = p." + PRODUCT_ID + " " +
                      "WHERE s." + SALE_DATE + " BETWEEN ? AND ? ORDER BY s." + SALE_DATE + " DESC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(startDate), String.valueOf(endDate)});
        
        if (cursor.moveToFirst()) {
            do {
                Sale sale = new Sale();
                sale.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SALE_ID)));
                sale.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(SALE_PRODUCT_ID)));
                sale.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_NAME)));
                sale.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(SALE_QUANTITY)));
                sale.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(SALE_TOTAL_PRICE)));
                sale.setSaleDate(cursor.getLong(cursor.getColumnIndexOrThrow(SALE_DATE)));
                sales.add(sale);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return sales;
    }

    public double getRevenueInDateRange(long startDate, long endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + SALE_TOTAL_PRICE + ") FROM " + TABLE_SALES + " WHERE " + SALE_DATE + " BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(startDate), String.valueOf(endDate)});
        
        double revenue = 0;
        if (cursor.moveToFirst()) {
            revenue = cursor.getDouble(0);
        }
        
        cursor.close();
        db.close();
        return revenue;
    }
}
