package com.teferi.abel.yeneshop.ClickListener;

import androidx.cardview.widget.CardView;

public interface CategoryClickListener {
    void onClick(String category);  // Changed to accept category name
    void onLongClick(String category, CardView cardView);
}