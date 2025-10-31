package com.example.question4;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.question4.Database.DBHandler;
import com.example.question4.adapters.ProductAdapter;
import com.example.question4.models.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity implements ProductAdapter.OnProductActionListener {
    
    private DBHandler dbHandler;
    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private EditText etSearch;
    private FloatingActionButton fabAddProduct;
    private LinearLayout layoutEmptyState;
    
    private String[] categories = {"Electronics", "Clothing", "Food", "Books", "Home & Garden", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Initialize database handler
        dbHandler = new DBHandler(this);
        
        // Initialize views
        initViews();
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup search functionality
        setupSearch();
        
        // Setup navigation
        setupBottomNavigation();
        
        // Load products
        loadProducts();
        
        // Check if we should show add product dialog
        if (getIntent().hasExtra("action") && "add_product".equals(getIntent().getStringExtra("action"))) {
            showAddProductDialog();
        }
    }
    
    private void initViews() {
        rvProducts = findViewById(R.id.rv_products);
        etSearch = findViewById(R.id.et_search);
        fabAddProduct = findViewById(R.id.fab_add_product);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        
        fabAddProduct.setOnClickListener(v -> showAddProductDialog());
    }
    
    private void setupRecyclerView() {
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        productAdapter.setOnProductActionListener(this);
        
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(productAdapter);
    }
    
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchTerm = s.toString().trim();
                if (searchTerm.isEmpty()) {
                    loadProducts();
                } else {
                    searchProducts(searchTerm);
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_inventory);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                
                if (itemId == R.id.navigation_inventory) {
                    return true;
                } else if (itemId == R.id.navigation_dashboard) {
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
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
    
    private void loadProducts() {
        try {
            productList.clear();
            productList.addAll(dbHandler.getAllProducts());
            productAdapter.notifyDataSetChanged();
            
            // Show/hide empty state
            if (productList.isEmpty()) {
                rvProducts.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.VISIBLE);
            } else {
                rvProducts.setVisibility(View.VISIBLE);
                layoutEmptyState.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading products", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void searchProducts(String searchTerm) {
        try {
            List<Product> searchResults = dbHandler.searchProducts(searchTerm);
            productAdapter.updateProducts(searchResults);
            
            // Show/hide empty state
            if (searchResults.isEmpty()) {
                rvProducts.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.VISIBLE);
            } else {
                rvProducts.setVisibility(View.VISIBLE);
                layoutEmptyState.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error searching products", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showAddProductDialog() {
        showProductDialog(null);
    }
    
    private void showProductDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null);
        
        // Get views from dialog
        TextInputEditText etProductName = dialogView.findViewById(R.id.et_product_name);
        AutoCompleteTextView spinnerCategory = dialogView.findViewById(R.id.spinner_category);
        TextInputEditText etPrice = dialogView.findViewById(R.id.et_price);
        TextInputEditText etStock = dialogView.findViewById(R.id.et_stock);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        
        // Setup category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        spinnerCategory.setAdapter(categoryAdapter);
        
        // If editing, populate fields
        if (product != null) {
            etProductName.setText(product.getName());
            spinnerCategory.setText(product.getCategory(), false);
            etPrice.setText(String.valueOf(product.getPrice()));
            etStock.setText(String.valueOf(product.getStock()));
            btnSave.setText("Update Product");
        }
        
        AlertDialog dialog = builder.setView(dialogView).create();
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        btnSave.setOnClickListener(v -> {
            if (validateProductInput(etProductName, spinnerCategory, etPrice, etStock)) {
                String name = etProductName.getText().toString().trim();
                String category = spinnerCategory.getText().toString().trim();
                double price = Double.parseDouble(etPrice.getText().toString().trim());
                int stock = Integer.parseInt(etStock.getText().toString().trim());
                
                if (product == null) {
                    // Add new product
                    Product newProduct = new Product(name, price, stock, category);
                    long result = dbHandler.addProduct(newProduct);
                    
                    if (result != -1) {
                        Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
                        loadProducts();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Error adding product", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Update existing product
                    product.setName(name);
                    product.setCategory(category);
                    product.setPrice(price);
                    product.setStock(stock);
                    
                    int result = dbHandler.updateProduct(product);
                    
                    if (result > 0) {
                        Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                        loadProducts();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Error updating product", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        
        dialog.show();
    }
    
    private boolean validateProductInput(TextInputEditText etName, AutoCompleteTextView spinnerCategory, 
                                       TextInputEditText etPrice, TextInputEditText etStock) {
        boolean isValid = true;
        
        // Validate name
        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError("Product name is required");
            isValid = false;
        }
        
        // Validate category
        if (spinnerCategory.getText().toString().trim().isEmpty()) {
            spinnerCategory.setError("Category is required");
            isValid = false;
        }
        
        // Validate price
        try {
            double price = Double.parseDouble(etPrice.getText().toString().trim());
            if (price <= 0) {
                etPrice.setError("Price must be greater than 0");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            etPrice.setError("Invalid price format");
            isValid = false;
        }
        
        // Validate stock
        try {
            int stock = Integer.parseInt(etStock.getText().toString().trim());
            if (stock < 0) {
                etStock.setError("Stock cannot be negative");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            etStock.setError("Invalid stock format");
            isValid = false;
        }
        
        return isValid;
    }
    
    @Override
    public void onEditProduct(Product product) {
        showProductDialog(product);
    }
    
    @Override
    public void onDeleteProduct(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete " + product.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int result = dbHandler.deleteProduct(product.getId());
                    if (result > 0) {
                        Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                        loadProducts();
                    } else {
                        Toast.makeText(this, "Error deleting product", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHandler != null) {
            dbHandler.close();
        }
    }
}