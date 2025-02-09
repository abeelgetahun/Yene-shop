package com.teferi.abel.yeneshop.Menus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teferi.abel.yeneshop.Adapters.AllItemsAdapter;
import com.teferi.abel.yeneshop.Database.RoomDB;
import com.teferi.abel.yeneshop.Models.Items;
import com.teferi.abel.yeneshop.R;

import java.util.List;

public class AllItemsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AllItemsAdapter adapter;
    private RoomDB database;
    private static final int UPDATE_ITEM_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_items);

        // Initialize database
        database = RoomDB.getInstance(this);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView_all_items);
        ImageButton backButton = findViewById(R.id.allitemback);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get all items from database
        List<Items> itemsList = database.mainDao().getAll();

        // Initialize and set adapter
        adapter = new AllItemsAdapter(this, itemsList);
        recyclerView.setAdapter(adapter);

        // Set back button click listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_ITEM_REQUEST_CODE && resultCode == RESULT_OK) {
            // Refresh the list when returning from ItemUpdate activity
            List<Items> itemsList = database.mainDao().getAll();
            adapter.updateItems(itemsList);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when activity resumes
        List<Items> itemsList = database.mainDao().getAll();
        adapter.updateItems(itemsList);
    }
}