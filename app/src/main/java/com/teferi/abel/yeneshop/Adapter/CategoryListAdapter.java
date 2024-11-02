package com.teferi.abel.yeneshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.teferi.abel.yeneshop.ClickListener.CategoryClickListener;
import com.teferi.abel.yeneshop.Database.MainDao;
import com.teferi.abel.yeneshop.R;

import java.text.DecimalFormat;
import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryViewHolder>{

    Context context;
    List<MainDao.CategoryTotals> categoryList;
    CategoryClickListener listener;

    public CategoryListAdapter(Context context, List<MainDao.CategoryTotals> categoryList, CategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.category_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        MainDao.CategoryTotals category = categoryList.get(position);  // Get the CategoryTotals object
        // Format with labels and proper number formatting
        holder.textview_item_category.setText( category.category);
        holder.textview_category_quantity.setText("Qty: " + category.totalQuantity);

        // Format currency with 2 decimal places
        DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
        holder.textview_category_purchasing_price.setText("PP: " +
                currencyFormat.format(category.totalPurchasePrice));
        holder.textview_category_selling_price.setText("SP: " +
                currencyFormat.format(category.totalSellingPrice));

        double potentialProfit = category.totalSellingPrice - category.totalPurchasePrice;
        holder.profit.setText("Profit: " +
                currencyFormat.format(potentialProfit));



        holder.category_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(category.category);
            }
        });

        holder.category_container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(category.category, holder.category_container);
                return true;
            }
        });
    }
    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}
class CategoryViewHolder extends RecyclerView.ViewHolder{
    CardView category_container;
    TextView textview_item_category, textview_category_quantity
            , textview_category_purchasing_price, textview_category_selling_price
            ,profit ;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        category_container = itemView.findViewById(R.id.category_container);
        textview_item_category = itemView.findViewById(R.id.textview_item_category);
        textview_category_quantity = itemView.findViewById(R.id.textview_category_quantity);
        textview_category_purchasing_price = itemView.findViewById(R.id.textview_category_Purchasing_price);
        textview_category_selling_price = itemView.findViewById(R.id.textview_category_selling_price);
        profit=itemView.findViewById(R.id.textview_category_profit);

    }
}
