package com.example.question4.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.question4.R;
import com.example.question4.models.Sale;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.SaleViewHolder> {
    private Context context;
    private List<Sale> sales;
    private SimpleDateFormat dateFormat;

    public SaleAdapter(Context context, List<Sale> sales) {
        this.context = context;
        this.sales = sales;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sale, parent, false);
        return new SaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleViewHolder holder, int position) {
        Sale sale = sales.get(position);
        
        holder.tvSaleProductName.setText(sale.getProductName());
        holder.tvSaleDate.setText(dateFormat.format(new Date(sale.getSaleDate())));
        holder.tvSaleQuantity.setText("Qty: " + sale.getQuantity());
        holder.tvSaleAmount.setText("$" + String.format("%.2f", sale.getTotalPrice()));
        holder.tvSaleId.setText("ID: " + String.format("%03d", sale.getId()));
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    public void updateSales(List<Sale> newSales) {
        this.sales.clear();
        this.sales.addAll(newSales);
        notifyDataSetChanged();
    }

    public static class SaleViewHolder extends RecyclerView.ViewHolder {
        TextView tvSaleProductName, tvSaleDate, tvSaleQuantity, tvSaleAmount, tvSaleId;
        ImageView ivSaleIcon;

        public SaleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSaleProductName = itemView.findViewById(R.id.tv_sale_product_name);
            tvSaleDate = itemView.findViewById(R.id.tv_sale_date);
            tvSaleQuantity = itemView.findViewById(R.id.tv_sale_quantity);
            tvSaleAmount = itemView.findViewById(R.id.tv_sale_amount);
            tvSaleId = itemView.findViewById(R.id.tv_sale_id);
            ivSaleIcon = itemView.findViewById(R.id.iv_sale_icon);
        }
    }
}