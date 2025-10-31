package com.example.question4.utils;

import android.content.Context;
import android.os.Environment;
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
    private Context context;
    
    public CSVExporter(Context context) {
        this.context = context;
    }
    
    public boolean exportSalesToCSV(List<Sale> sales, String fileName) {
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File csvFile = new File(downloadsDir, fileName + ".csv");
            
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
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static String generateFileName(String prefix) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return prefix + "_" + dateFormat.format(new Date());
    }
}