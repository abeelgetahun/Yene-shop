package com.teferi.abel.yeneshop.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.teferi.abel.yeneshop.ItemUpdate;
import com.teferi.abel.yeneshop.Models.Items;
import com.teferi.abel.yeneshop.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AllItemsAdapter extends RecyclerView.Adapter<AllItemsAdapter.ViewHolder> {
    private List<Items> itemsList;
    private Activity activity;
    private static final int UPDATE_ITEM_REQUEST_CODE = 100;

    public AllItemsAdapter(Activity activity, List<Items> itemsList) {
        this.activity = activity;
        this.itemsList = itemsList;
        // Sort items by name in ascending order
        Collections.sort(this.itemsList, new Comparator<Items>() {
            @Override
            public int compare(Items item1, Items item2) {
                return item1.getName().compareToIgnoreCase(item2.getName());
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Items item = itemsList.get(position);

        holder.name.setText(item.getName());
        holder.id.setText(String.valueOf(item.getId()));
        holder.quantity.setText("Quantity: " + item.getQuantity());
        holder.date.setText("Date: " + item.getDate());
        holder.pp.setText("PP: " + item.getPurchasing_price());
        holder.sp.setText("SP: " + item.getSelling_price());

        // Calculate expected profit
        double expectedProfit = (item.getSelling_price() - item.getPurchasing_price()) * item.getQuantity();
        holder.ep.setText("EP: " + String.format("%.2f", expectedProfit));

        // Set click listener for the card
        holder.itemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ItemUpdate.class);
                intent.putExtra("item_id", item.getId());
                activity.startActivityForResult(intent, UPDATE_ITEM_REQUEST_CODE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void updateItems(List<Items> newItems) {
        this.itemsList = newItems;
        Collections.sort(this.itemsList, new Comparator<Items>() {
            @Override
            public int compare(Items item1, Items item2) {
                return item1.getName().compareToIgnoreCase(item2.getName());
            }
        });
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView itemContainer;
        TextView name, id, quantity, date, pp, sp, ep;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemContainer = itemView.findViewById(R.id.item_container);
            name = itemView.findViewById(R.id.textview_item_name);
            id = itemView.findViewById(R.id.textview_item_id);
            quantity = itemView.findViewById(R.id.textview_item_qty);
            date = itemView.findViewById(R.id.textview_items_date);
            pp = itemView.findViewById(R.id.textview_item_pp);
            sp = itemView.findViewById(R.id.textview_item_sp);
            ep = itemView.findViewById(R.id.textview_item_ep);
        }
    }
}