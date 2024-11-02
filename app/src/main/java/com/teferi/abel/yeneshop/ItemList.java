package com.teferi.abel.yeneshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.teferi.abel.yeneshop.Adapter.ItemListAdapter;
import com.teferi.abel.yeneshop.ClickListener.ItemClickListener;
import com.teferi.abel.yeneshop.Database.MainDao;
import com.teferi.abel.yeneshop.Database.RoomDB;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;


public class ItemList extends AppCompatActivity {
    RecyclerView recyclerView;
    ItemListAdapter itemListAdapter;
    List<MainDao.ItemDetails> itemList = new ArrayList<>();
    RoomDB database;
    ImageView fab_back;
    public String category;
    private static final int UPDATE_ITEM_REQUEST_CODE = 102;
    private static final int MAIN_ACTIVITY_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        recyclerView = findViewById(R.id.recycler_home2);
        fab_back = findViewById(R.id.itemlist_back_button);
        database = RoomDB.getInstance(this);

        category = getIntent().getStringExtra("category");
        if (category == null || category.trim().isEmpty()) {
            returnToMain("Invalid category");
            return;
        }

        loadItems();

        fab_back.setOnClickListener(view -> {
            returnToMain(null);
        });
    }

    private void returnToMain(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent();
        setResult(RESULT_OK);
        finish();
    }

    private void loadItems() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<MainDao.ItemDetails> items = database.mainDao().getItemsByCategory(category);
                runOnUiThread(() -> {
                    if (items == null || items.isEmpty()) {
                        returnToMain("No items found in this category");
                    } else {
                        itemList.clear();
                        itemList.addAll(items);
                        updateRecycler(itemList);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    returnToMain("Error loading items: " + e.getMessage());
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == UPDATE_ITEM_REQUEST_CODE) {
                loadItems();
            } else if (requestCode == MAIN_ACTIVITY_REQUEST_CODE) {
                // Refresh category name if it was updated
                Executors.newSingleThreadExecutor().execute(() -> {
                    String updatedCategory = database.mainDao().getCategoryById(category);
                    runOnUiThread(() -> {
                        if (updatedCategory != null && !updatedCategory.equals(category)) {
                            category = updatedCategory;
                        }
                        loadItems();
                    });
                });
            }
        }
    }

    private void updateRecycler(List<MainDao.ItemDetails> itemList) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        itemListAdapter = new ItemListAdapter(ItemList.this, itemList, itemClickListener);
        recyclerView.setAdapter(itemListAdapter);
    }

    private final ItemClickListener itemClickListener = itemId -> {
        Intent intent = new Intent(ItemList.this, ItemUpdate.class);
        intent.putExtra("item_id", itemId);
        startActivityForResult(intent, UPDATE_ITEM_REQUEST_CODE);
    };
}