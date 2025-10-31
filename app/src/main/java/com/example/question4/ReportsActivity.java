package com.example.question4;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.question4.Database.DBHandler;
import com.example.question4.models.Sale;
import com.example.question4.utils.CSVExporter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {
    
    private DBHandler dbHandler;
    private CSVExporter csvExporter;
    
    private TextInputEditText etStartDate, etEndDate;
    private TextView tvProfitMargin, tvInventoryValue;
    private Button btnGenerateReport, btnExportCSV;
    
    private Calendar startDate, endDate;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        // Initialize components
        dbHandler = new DBHandler(this);
        csvExporter = new CSVExporter(this);
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        
        // Initialize calendars with default date range (last 30 days)
        endDate = Calendar.getInstance();
        startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, -30);
        
        // Initialize views
        initViews();
        
        // Setup components
        setupDatePickers();
        setupButtons();
        setupBottomNavigation();
        
        // Load initial data
        loadReportData();
        
        // Set default dates
        updateDateFields();
    }
    
    private void initViews() {
        etStartDate = findViewById(R.id.et_from_date);
        etEndDate = findViewById(R.id.et_to_date);
        tvProfitMargin = findViewById(R.id.tv_profit_margin);
        tvInventoryValue = findViewById(R.id.tv_inventory_value);
        btnGenerateReport = findViewById(R.id.btn_generate_report);
        btnExportCSV = findViewById(R.id.btn_export_csv);
    }
    
    private void setupDatePickers() {
        etStartDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        startDate.set(year, month, dayOfMonth);
                        updateDateFields();
                    },
                    startDate.get(Calendar.YEAR),
                    startDate.get(Calendar.MONTH),
                    startDate.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
        
        etEndDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        endDate.set(year, month, dayOfMonth);
                        updateDateFields();
                    },
                    endDate.get(Calendar.YEAR),
                    endDate.get(Calendar.MONTH),
                    endDate.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }
    
    private void setupButtons() {
        btnGenerateReport.setOnClickListener(v -> {
            if (validateDateRange()) {
                loadReportData();
            }
        });
        
        btnExportCSV.setOnClickListener(v -> {
            if (validateDateRange()) {
                exportToCSV();
            }
        });
    }
    
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_reports);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                
                if (itemId == R.id.navigation_reports) {
                    return true;
                } else if (itemId == R.id.navigation_dashboard) {
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.navigation_inventory) {
                    startActivity(new Intent(getApplicationContext(), InventoryActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.navigation_sales) {
                    startActivity(new Intent(getApplicationContext(), SalesActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }
    
    private void updateDateFields() {
        etStartDate.setText(dateFormat.format(startDate.getTime()));
        etEndDate.setText(dateFormat.format(endDate.getTime()));
    }
    
    private boolean validateDateRange() {
        if (startDate.after(endDate)) {
            Toast.makeText(this, "Start date cannot be after end date", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    
    private void loadReportData() {
        try {
            long startTime = startDate.getTimeInMillis();
            long endTime = endDate.getTimeInMillis();
            
            // Get sales data for the date range
            List<Sale> salesInRange = dbHandler.getAllSales(); // Temporary - get all sales
            
            if (salesInRange.isEmpty()) {
                clearReportData();
                Toast.makeText(this, "No sales data found", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Calculate basic metrics
            double totalRevenue = 0;
            for (Sale sale : salesInRange) {
                totalRevenue += sale.getTotalPrice();
            }
            
            // Calculate total inventory value
            double inventoryValue = dbHandler.getTotalInventoryValue();
            
            // Update UI with simplified data
            tvProfitMargin.setText(salesInRange.size() + " Sales");
            tvInventoryValue.setText("$" + String.format("%.2f", inventoryValue));
            
            Toast.makeText(this, "Report generated successfully", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading report data", Toast.LENGTH_SHORT).show();
            clearReportData();
        }
    }
    
    private void clearReportData() {
        tvProfitMargin.setText("0 Sales");
        tvInventoryValue.setText("$0.00");
    }
    
    private void exportToCSV() {
        try {
            List<Sale> salesData = dbHandler.getAllSales();
            
            if (salesData.isEmpty()) {
                Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
                return;
            }
            
            boolean success = csvExporter.exportSalesToCSV(salesData, 
                "sales_report_" + dateFormat.format(startDate.getTime()).replace(" ", "_"));
            
            if (success) {
                Toast.makeText(this, "Report exported successfully to Downloads folder", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to export report", Toast.LENGTH_SHORT).show();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error exporting report", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHandler != null) {
            dbHandler.close();
        }
    }
}