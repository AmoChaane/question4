package com.example.question4.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.example.question4.models.Sale;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CSVExporter {
    private static final String CSV_HEADER = "Sale ID,Product Name,Quantity,Unit Price,Total Price,Sale Date\n";
    private static final String TAG = "CSVExporter";
    private Context context;
    
    public CSVExporter(Context context) {
        this.context = context;
    }
    
    public boolean exportSalesToCSV(List<Sale> sales, String fileName) {
        try {
            // Use app's external files directory which doesn't require permissions
            File documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (documentsDir == null) {
                // Fallback to internal storage
                documentsDir = new File(context.getFilesDir(), "exports");
                if (!documentsDir.exists()) {
                    documentsDir.mkdirs();
                }
            }
            
            File csvFile = new File(documentsDir, fileName + ".csv");
            Log.d(TAG, "Saving CSV to: " + csvFile.getAbsolutePath());
            
            FileWriter writer = new FileWriter(csvFile);
            writer.append(CSV_HEADER);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            
            for (Sale sale : sales) {
                writer.append(String.valueOf(sale.getId())).append(",");
                writer.append(sale.getProductName()).append(",");
                writer.append(String.valueOf(sale.getQuantity())).append(",");
                writer.append(String.format("%.2f", sale.getUnitPrice())).append(",");
                writer.append(String.format("%.2f", sale.getTotalPrice())).append(",");
                writer.append(dateFormat.format(new Date(sale.getSaleDate()))).append("\n");
            }
            
            writer.flush();
            writer.close();
            
            Log.d(TAG, "CSV file created successfully: " + csvFile.getName());
            Log.d(TAG, "File size: " + csvFile.length() + " bytes");
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error creating CSV file", e);
            e.printStackTrace();
            return false;
        }
    }
    
    public String getLastExportPath() {
        File documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (documentsDir == null) {
            documentsDir = new File(context.getFilesDir(), "exports");
        }
        return documentsDir.getAbsolutePath();
    }
    
    public static String generateFileName(String prefix) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return prefix + "_" + dateFormat.format(new Date());
    }
}