package com.teferi.abel.yeneshop.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teferi.abel.yeneshop.Models.Items;
import com.teferi.abel.yeneshop.R;

import java.util.ArrayList;
import java.util.List;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private List<Items> items;
    private List<Items> fullList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Items item);
    }

    public SearchAdapter(List<Items> items, OnItemClickListener listener) {
        this.items = items;
        this.fullList = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item_layout, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Items item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateList(List<Items> newList) {
        this.items = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void setFullList(List<Items> fullList) {
        this.fullList = new ArrayList<>(fullList);
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemName;
        private final TextView itemId;
        private final TextView itemCategory;
        private final TextView itemPP;
        private final TextView itemSP;
        private final TextView itemQty;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.search_item_name);
            itemId = itemView.findViewById(R.id.search_item_id);
            itemCategory = itemView.findViewById(R.id.search_item_category);
            itemPP = itemView.findViewById(R.id.search_item_pp);
            itemSP = itemView.findViewById(R.id.search_item_sp);
            itemQty = itemView.findViewById(R.id.search_item_qty);
        }

        public void bind(Items item, OnItemClickListener listener) {
            itemId.setText("ID: " + item.getId());
            itemName.setText(item.getName());
            itemCategory.setText(item.getCategory());
            itemPP.setText(String.valueOf(item.getPurchasing_price()));
            itemSP.setText(String.valueOf(item.getSelling_price()));
            itemQty.setText(String.valueOf(item.getQuantity()));

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}