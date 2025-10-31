package com.example.question4.models;

public class Product {
    private int id;
    private String name;
    private double price;
    private int stock;
    private String category;

    // Constructors
    public Product() {}

    public Product(String name, double price, int stock, String category) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    public Product(int id, String name, double price, int stock, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    // Utility methods
    public boolean isLowStock() {
        return stock <= 10; // Consider low stock when 10 or fewer items
    }

    public double getTotalValue() {
        return price * stock;
    }

    @Override
    public String toString() {
        return name + " - $" + String.format("%.2f", price);
    }
}