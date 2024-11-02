package com.teferi.abel.yeneshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.teferi.abel.yeneshop.ClickListener.ItemClickListener;
import com.teferi.abel.yeneshop.Database.MainDao;
import com.teferi.abel.yeneshop.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemViewHolder> {

    Context context;
    List<MainDao.ItemDetails> itemList;
    ItemClickListener listener;

    public ItemListAdapter(Context context, List<MainDao.ItemDetails> itemList, ItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        MainDao.ItemDetails item = itemList.get(position);

        // Set item details in views
        holder.textview_item_name.setText(String.valueOf(item.name));
        holder.textview_item_id.setText(String.valueOf(item.id));
        holder.textview_item_quantity.setText("Qty: " + item.quantity);

        SimpleDateFormat originalFormat = new SimpleDateFormat("EEE, d MMM yyyy hh:mm a", Locale.getDefault());
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date parsedDate = originalFormat.parse(item.date); // Parse original date string
            String formattedDate = targetFormat.format(parsedDate); // Format to dd/MM/yyyy
            holder.textview_item_date.setText(formattedDate); // Set formatted date
        } catch (ParseException e) {
            e.printStackTrace();
            holder.textview_item_date.setText("Invalid Date"); // Handle parsing error
        }

        // Format currency
        DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
        holder.textview_item_purchasing_price.setText("PP: " + currencyFormat.format(item.purchasing_price));
        holder.textview_item_selling_price.setText("SP: " + currencyFormat.format(item.selling_price));
        holder.textview_item_profit.setText("EP: " + currencyFormat.format(item.expected_profit));

        holder.item_container.setOnClickListener(view -> {
            if (listener != null) {
                listener.onClick(item.id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ViewHolder class for RecyclerView
    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        CardView item_container;
        TextView textview_item_name, textview_item_id, textview_item_quantity,
                textview_item_purchasing_price, textview_item_selling_price,
                textview_item_profit, textview_item_date;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views from item_list.xml
            item_container = itemView.findViewById(R.id.item_container);
            textview_item_name = itemView.findViewById(R.id.textview_item_name);
            textview_item_id = itemView.findViewById(R.id.textview_item_id);
            textview_item_quantity = itemView.findViewById(R.id.textview_item_qty);
            textview_item_purchasing_price = itemView.findViewById(R.id.textview_item_pp);
            textview_item_selling_price = itemView.findViewById(R.id.textview_item_sp);
            textview_item_profit = itemView.findViewById(R.id.textview_item_ep);
            textview_item_date = itemView.findViewById(R.id.textview_items_date);
        }
    }
}