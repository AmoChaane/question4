package com.example.question4.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.question4.R;
import com.example.question4.models.Product;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> products;
    private OnProductActionListener listener;

    public interface OnProductActionListener {
        void onEditProduct(Product product);
        void onDeleteProduct(Product product);
    }

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    public void setOnProductActionListener(OnProductActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        
        holder.tvProductName.setText(product.getName());
        holder.tvProductCategory.setText(product.getCategory());
        holder.tvProductStock.setText("Stock: " + product.getStock());
        holder.tvProductPrice.setText("$" + String.format("%.2f", product.getPrice()));
        
        // Set stock indicator color
        if (product.isLowStock()) {
            holder.viewStockIndicator.setBackgroundColor(context.getResources().getColor(R.color.error_red));
        } else if (product.getStock() < 20) {
            holder.viewStockIndicator.setBackgroundColor(context.getResources().getColor(R.color.warning_orange));
        } else {
            holder.viewStockIndicator.setBackgroundColor(context.getResources().getColor(R.color.success_green));
        }
        
        // Set click listeners
        holder.btnEditProduct.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditProduct(product);
            }
        });
        
        holder.btnDeleteProduct.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteProduct(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.products.clear();
        this.products.addAll(newProducts);
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductCategory, tvProductStock, tvProductPrice;
        View viewStockIndicator;
        Button btnEditProduct, btnDeleteProduct;
        ImageView ivProductIcon;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductCategory = itemView.findViewById(R.id.tv_product_category);
            tvProductStock = itemView.findViewById(R.id.tv_product_stock);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            viewStockIndicator = itemView.findViewById(R.id.view_stock_indicator);
            btnEditProduct = itemView.findViewById(R.id.btn_edit_product);
            btnDeleteProduct = itemView.findViewById(R.id.btn_delete_product);
            ivProductIcon = itemView.findViewById(R.id.iv_product_icon);
        }
    }
}