package com.example.question4;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.question4.Database.DBHandler;
import com.example.question4.models.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    
    private DBHandler dbHandler;
    private TextView tvTotalProductsCount, tvTotalRevenue, tvTotalSalesCount, tvLowStockCount;
    private Button btnAddProduct, btnRecordSale, btnViewReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize database handler
        dbHandler = new DBHandler(this);
        
        // Initialize views
        initViews();
        
        // Setup navigation
        setupBottomNavigation();
        
        // Setup quick action buttons
        setupQuickActions();
        
        // Load dashboard data
        loadDashboardData();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to dashboard
        loadDashboardData();
    }
    
    private void initViews() {
        tvTotalProductsCount = findViewById(R.id.tv_total_products_count);
        tvTotalRevenue = findViewById(R.id.tv_total_revenue);
        tvTotalSalesCount = findViewById(R.id.tv_total_sales_count);
        tvLowStockCount = findViewById(R.id.tv_low_stock_count);
        
        btnAddProduct = findViewById(R.id.btn_add_product);
        btnRecordSale = findViewById(R.id.btn_record_sale);
        btnViewReports = findViewById(R.id.btn_view_reports);
    }
    
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                
                if (itemId == R.id.navigation_dashboard) {
                    return true;
                } else if (itemId == R.id.navigation_inventory) {
                    startActivity(new Intent(getApplicationContext(), InventoryActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.navigation_sales) {
                    startActivity(new Intent(getApplicationContext(), SalesActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.navigation_reports) {
                    startActivity(new Intent(getApplicationContext(), ReportsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }
    
    private void setupQuickActions() {
        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(this, InventoryActivity.class);
            intent.putExtra("action", "add_product");
            startActivity(intent);
        });
        
        btnRecordSale.setOnClickListener(v -> {
            Intent intent = new Intent(this, SalesActivity.class);
            intent.putExtra("action", "record_sale");
            startActivity(intent);
        });
        
        btnViewReports.setOnClickListener(v -> {
            startActivity(new Intent(this, ReportsActivity.class));
        });
    }
    
    private void loadDashboardData() {
        try {
            // Load total products count
            int totalProducts = dbHandler.getTotalProductsCount();
            tvTotalProductsCount.setText(String.valueOf(totalProducts));
            
            // Load total revenue
            double totalRevenue = dbHandler.getTotalRevenue();
            tvTotalRevenue.setText("$" + String.format("%.2f", totalRevenue));
            
            // Load total sales count
            int totalSales = dbHandler.getTotalSalesCount();
            tvTotalSalesCount.setText(String.valueOf(totalSales));
            
            // Load low stock count
            List<Product> lowStockProducts = dbHandler.getLowStockProducts();
            tvLowStockCount.setText(String.valueOf(lowStockProducts.size()));
            
            // Show low stock alert if necessary
            if (lowStockProducts.size() > 0) {
                String message = lowStockProducts.size() + " product(s) are running low on stock!";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading dashboard data", Toast.LENGTH_SHORT).show();
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