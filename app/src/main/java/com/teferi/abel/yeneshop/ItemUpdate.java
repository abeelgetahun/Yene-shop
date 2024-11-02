package com.teferi.abel.yeneshop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.teferi.abel.yeneshop.Database.MainDao;
import com.teferi.abel.yeneshop.Database.RoomDB;
import com.teferi.abel.yeneshop.Models.Items;

import java.util.concurrent.Executors;


public class ItemUpdate extends AppCompatActivity {
    private TextView updateItemId, updateDate;
    private EditText updateName, updateCategory, updateQuantity, updatePP, updateSS, updateTax;
    private ImageButton updateBack, updateDelete;
    private MaterialButton updateBtn;
    private Items currentItem;
    private MainDao mainDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_update);

        initializeViews();
        setupDatabase();
        loadItem();
        setupClickListeners();
        setupDateScroll();

    }

    private void setupDateScroll() {
        updateDate.setSelected(true);
        updateDate.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        updateDate.setSingleLine(true);
        updateDate.setMarqueeRepeatLimit(-1); // -1 means forever
        updateDate.setFocusable(true);
        updateDate.setFocusableInTouchMode(true);
        updateDate.requestFocus();
    }

    private void initializeViews() {
        updateItemId = findViewById(R.id.update_id);
        updateName = findViewById(R.id.update_name);
        updateCategory = findViewById(R.id.update_category);
        updateQuantity = findViewById(R.id.update_quantity);
        updatePP = findViewById(R.id.update_pp);
        updateSS = findViewById(R.id.update_ss);
        updateTax = findViewById(R.id.update_tax);
        updateDate = findViewById(R.id.update_date);
        updateBtn = findViewById(R.id.update_btn);
        updateBack = findViewById(R.id.update_back);
        updateDelete = findViewById(R.id.update_delete);

        if (updateDate != null) {
            updateDate.post(new Runnable() {
                @Override
                public void run() {
                    updateDate.setSelected(true);
                }
            });
        }

    }
    // Create a custom TextView class for automatic scrolling
    public static class MarqueeTextView extends androidx.appcompat.widget.AppCompatTextView {
        public MarqueeTextView(Context context) {
            super(context);
        }

        public MarqueeTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        public boolean isFocused() {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (updateDate != null) {
            updateDate.setSelected(true);
            updateDate.requestFocus();
        }
    }

    private void setupDatabase() {
        RoomDB db = RoomDB.getInstance(this);
        mainDao = db.mainDao();
    }

    private void loadItem() {
        int itemId = getIntent().getIntExtra("item_id", -1);
        if (itemId == -1) {
            Toast.makeText(this, "Error: Invalid item ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            currentItem = mainDao.getItemById(itemId);
            runOnUiThread(() -> {
                if (currentItem != null) {
                    populateFields(currentItem);
                } else {
                    Toast.makeText(this, "Error: Item not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void setupClickListeners() {
        updateBtn.setOnClickListener(v -> updateItem());
        updateBack.setOnClickListener(v -> finish());
        updateDelete.setOnClickListener(v -> deleteItem());
    }

    private void populateFields(Items item) {
        updateItemId.setText(String.format("Item ID: %d", item.getId()));
        updateName.setText(item.getName());
        updateCategory.setText(item.getCategory() != null ? item.getCategory() : "");
        updateQuantity.setText(String.valueOf(item.getQuantity()));
        updatePP.setText(String.valueOf(item.getPurchasing_price()));
        updateSS.setText(String.valueOf(item.getSelling_price()));
        updateTax.setText(String.valueOf(item.getTax()));
        updateDate.setText(item.getDate());

        // Set a longer text to ensure scrolling is needed
        String dateText = item.getDate() + "       " + "       " + item.getDate();
        updateDate.setText(dateText);

        // Setup marquee effect
        updateDate.post(new Runnable() {
            @Override
            public void run() {
                updateDate.setSelected(true);
                updateDate.requestFocus();
            }
        });
    }

    private void updateItem() {
        if (currentItem == null) {
            Toast.makeText(this, "Error: Item not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String categoryInput = updateCategory.getText().toString().trim();
            StringBuilder categoryBuilder = new StringBuilder();
            for (int i = 0; i < categoryInput.length(); i++) {
                char currentChar = categoryInput.charAt(i);
                if (Character.isLetter(currentChar)) {
                    categoryBuilder.append(Character.toUpperCase(currentChar));
                } else {
                    categoryBuilder.append(currentChar);
                }
            }
            String category = categoryBuilder.toString();

            String name = updateName.getText().toString().trim();
            int quantity = Integer.parseInt(updateQuantity.getText().toString().trim());
            double purchasingPrice = Double.parseDouble(updatePP.getText().toString().trim());
            double sellingPrice = Double.parseDouble(updateSS.getText().toString().trim());
            int tax = Integer.parseInt(updateTax.getText().toString().trim());

            if (name.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    mainDao.updateItem(currentItem.getId(), name, category, quantity,
                            purchasingPrice, sellingPrice, tax);

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Error updating item: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteItem() {
        if (currentItem == null) {
            Toast.makeText(this, "Error: Item not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            mainDao.delete(currentItem);
            runOnUiThread(() -> {
                Toast.makeText(this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            });
        });
    }
}