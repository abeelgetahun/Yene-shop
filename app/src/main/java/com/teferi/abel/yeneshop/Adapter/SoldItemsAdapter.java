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

    public void setSoldItems(List<Sales> soldItems) {
        this.soldItems = soldItems;
        notifyDataSetChanged(); // Refresh the RecyclerView
    }

    @NonNull
    @Override
    public SoldItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sold_card, parent, false);
        return new SoldItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoldItemViewHolder holder, int position) {
        Sales sale = soldItems.get(position);
        holder.itemName.setText(sale.getName());
        holder.itemQuantity.setText("Quantity: " + sale.getQuantity());
        holder.itemDate.setText("Date: " + sale.getDate());
    }

    @Override
    public int getItemCount() {
        return soldItems == null ? 0 : soldItems.size();
    }

    static class SoldItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity, itemDate;

        SoldItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            itemDate = itemView.findViewById(R.id.item_date);
        }
    }
}