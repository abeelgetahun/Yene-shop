package com.teferi.abel.yeneshop.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.teferi.abel.yeneshop.Models.Sales;
import com.teferi.abel.yeneshop.R;
import java.util.List;

public class SoldItemsAdapter extends RecyclerView.Adapter<SoldItemsAdapter.SoldItemViewHolder> {
    private List<Sales> soldItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Sales sale);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setSoldItems(List<Sales> soldItems) {
        this.soldItems = soldItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SoldItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your custom card layout (make sure it matches your provided XML)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sold_card, parent, false);
        return new SoldItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoldItemViewHolder holder, int position) {
        Sales sale = soldItems.get(position);
        // Left side content
        holder.itemName.setText(sale.getName());
        holder.itemQuantity.setText("Quantity: " + sale.getQuantity());
        holder.itemDate.setText("Date: " + sale.getDate());
        // Right side content
        holder.itemId.setText("ID: " + sale.getId());
        holder.itemSp.setText("SP: $" + String.format("%.2f", sale.getSelling_price()));
        double profit = (sale.getSelling_price() - sale.getPurchasing_price()) * sale.getQuantity();
        holder.itemProfit.setText("Profit: $" + String.format("%.2f", profit));

        // Set click listener to allow reversal when the user taps the card
        holder.itemView.setOnClickListener(v -> {
            if(listener != null) {
                listener.onItemClick(sale);
            }
        });
    }

    @Override
    public int getItemCount() {
        return soldItems == null ? 0 : soldItems.size();
    }

    static class SoldItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity, itemDate, itemId, itemSp, itemProfit;

        SoldItemViewHolder(@NonNull View itemView) {
            super(itemView);
            // Bind views from your card layout
            itemName = itemView.findViewById(R.id.item_name);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            itemDate = itemView.findViewById(R.id.item_date);
            itemId = itemView.findViewById(R.id.item_id);
            itemSp = itemView.findViewById(R.id.item_sp);
            itemProfit = itemView.findViewById(R.id.item_profit);
        }
    }
}
