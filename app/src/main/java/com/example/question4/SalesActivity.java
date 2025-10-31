package com.example.question4;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.question4.Database.DBHandler;
import com.example.question4.adapters.SaleAdapter;
import com.example.question4.models.Product;
import com.example.question4.models.Sale;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SalesActivity extends AppCompatActivity {
    
    private DBHandler dbHandler;
    private AutoCompleteTextView spinnerProducts;
    private TextInputEditText etQuantity, etSaleDate;
    private TextView tvTotalAmount;
    private Button btnRecordSale;
    private RecyclerView rvSales;
    private SaleAdapter saleAdapter;
    
    private List<Product> productList;
    private List<Sale> salesList;
    private Product selectedProduct;
    private Calendar selectedDate;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        // Initialize database handler
        dbHandler = new DBHandler(this);
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        selectedDate = Calendar.getInstance();
        
        // Initialize views
        initViews();
        
        // Setup components
        setupProductSpinner();
        setupDatePicker();
        setupQuantityInput();
        setupRecordSaleButton();
        setupSalesRecyclerView();
        setupBottomNavigation();
        
        // Load data
        loadProducts();
        loadSales();
        
        // Set today's date as default
        etSaleDate.setText(dateFormat.format(new Date()));
    }
    
    private void initViews() {
        spinnerProducts = findViewById(R.id.spinner_products);
        etQuantity = findViewById(R.id.et_quantity);
        etSaleDate = findViewById(R.id.et_sale_date);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        btnRecordSale = findViewById(R.id.btn_record_sale);
        rvSales = findViewById(R.id.rv_sales);
    }
    
    private void setupProductSpinner() {
        spinnerProducts.setOnItemClickListener((parent, view, position, id) -> {
            selectedProduct = productList.get(position);
            calculateTotal();
        });
    }
    
    private void setupDatePicker() {
        etSaleDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        etSaleDate.setText(dateFormat.format(selectedDate.getTime()));
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }
    
    private void setupQuantityInput() {
        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotal();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void setupRecordSaleButton() {
        btnRecordSale.setOnClickListener(v -> recordSale());
    }
    
    private void setupSalesRecyclerView() {
        salesList = new ArrayList<>();
        saleAdapter = new SaleAdapter(this, salesList);
        rvSales.setLayoutManager(new LinearLayoutManager(this));
        rvSales.setAdapter(saleAdapter);
    }
    
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_sales);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                
                if (itemId == R.id.navigation_sales) {
                    return true;
                } else if (itemId == R.id.navigation_dashboard) {
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.navigation_inventory) {
                    startActivity(new Intent(getApplicationContext(), InventoryActivity.class));
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
    
    private void loadProducts() {
        try {
            productList = dbHandler.getAllProducts();
            
            // Create adapter for spinner
            List<String> productNames = new ArrayList<>();
            for (Product product : productList) {
                productNames.add(product.getName() + " - R" + String.format("%.2f", product.getPrice()));
            }
            
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, productNames);
            spinnerProducts.setAdapter(adapter);
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading products", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadSales() {
        try {
            salesList.clear();
            salesList.addAll(dbHandler.getAllSales());
            saleAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading sales", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void calculateTotal() {
        if (selectedProduct != null && !etQuantity.getText().toString().isEmpty()) {
            try {
                int quantity = Integer.parseInt(etQuantity.getText().toString());
                double total = selectedProduct.getPrice() * quantity;
                tvTotalAmount.setText("R" + String.format("%.2f", total));
            } catch (NumberFormatException e) {
                tvTotalAmount.setText("R0.00");
            }
        } else {
            tvTotalAmount.setText("R0.00");
        }
    }
    
    private void recordSale() {
        if (validateSaleInput()) {
            try {
                int quantity = Integer.parseInt(etQuantity.getText().toString().trim());
                
                // Check if enough stock is available
                if (selectedProduct.getStock() < quantity) {
                    Toast.makeText(this, "Insufficient stock! Available: " + selectedProduct.getStock(), Toast.LENGTH_SHORT).show();
                    return;
                }
                
                double totalPrice = selectedProduct.getPrice() * quantity;
                long saleDate = selectedDate.getTimeInMillis();
                
                Sale sale = new Sale(selectedProduct.getId(), selectedProduct.getName(), quantity, totalPrice, saleDate);
                long result = dbHandler.addSale(sale);
                
                if (result != -1) {
                    Toast.makeText(this, "Sale recorded successfully", Toast.LENGTH_SHORT).show();
                    
                    // Clear form
                    clearForm();
                    
                    // Refresh data
                    loadProducts();
                    loadSales();
                } else {
                    Toast.makeText(this, "Error recording sale", Toast.LENGTH_SHORT).show();
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error recording sale", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private boolean validateSaleInput() {
        boolean isValid = true;
        
        // Validate product selection
        if (selectedProduct == null) {
            spinnerProducts.setError("Please select a product");
            isValid = false;
        }
        
        // Validate quantity
        String quantityStr = etQuantity.getText().toString().trim();
        if (quantityStr.isEmpty()) {
            etQuantity.setError("Quantity is required");
            isValid = false;
        } else {
            try {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    etQuantity.setError("Quantity must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etQuantity.setError("Invalid quantity format");
                isValid = false;
            }
        }
        
        // Validate date
        if (etSaleDate.getText().toString().trim().isEmpty()) {
            etSaleDate.setError("Sale date is required");
            isValid = false;
        }
        
        return isValid;
    }
    
    private void clearForm() {
        spinnerProducts.setText("");
        etQuantity.setText("");
        etSaleDate.setText(dateFormat.format(new Date()));
        tvTotalAmount.setText("R0.00");
        selectedProduct = null;
        selectedDate = Calendar.getInstance();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHandler != null) {
            dbHandler.close();
        }
    }
}