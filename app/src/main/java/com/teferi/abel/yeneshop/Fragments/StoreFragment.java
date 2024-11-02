package com.teferi.abel.yeneshop.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.teferi.abel.yeneshop.Adapter.CategoryListAdapter;
import com.teferi.abel.yeneshop.ClickListener.CategoryClickListener;
import com.teferi.abel.yeneshop.Database.MainDao;
import com.teferi.abel.yeneshop.Database.RoomDB;
import com.teferi.abel.yeneshop.ItemList;
import com.teferi.abel.yeneshop.Models.Items;
import com.teferi.abel.yeneshop.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class StoreFragment extends Fragment {
    RecyclerView recyclerView;
    CategoryListAdapter categoryListAdapter;
    List<MainDao.CategoryTotals> categoryList;
    MainDao.OverallTotals overallTotals;
    RoomDB database;
    private TextView storeTotalPP, storeTotalSP;
    private static final int ADD_ITEM_REQUEST_CODE = 101;
    private static final int ITEM_LIST_REQUEST_CODE = 103;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        recyclerView = view.findViewById(R.id.recycler_home);
        database = RoomDB.getInstance(requireContext());
        categoryList = new ArrayList<>();
        storeTotalPP = view.findViewById(R.id.total_store_pp);
        storeTotalSP = view.findViewById(R.id.total_store_sp);

        // Initialize RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        categoryListAdapter = new CategoryListAdapter(requireContext(), categoryList, categoryClickListener);
        recyclerView.setAdapter(categoryListAdapter);

        loadData();
        return view;
    }



    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Load categories
            List<MainDao.CategoryTotals> categories = database.mainDao().getAllCategoryTotals();
            // Load overall totals
            MainDao.OverallTotals totals = database.mainDao().getOverallTotals();

            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // Update categories in RecyclerView
                    categoryList.clear();
                    if (categories != null && !categories.isEmpty()) {
                        categoryList.addAll(categories);
                    }
                    categoryListAdapter.notifyDataSetChanged();

                    // Update store totals
                    DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
                    if (totals != null) {
                        storeTotalPP.setText("Total Store PP: " +
                                currencyFormat.format(totals.totalPurchasePrice));
                        storeTotalSP.setText("Total Store SP: " +
                                currencyFormat.format(totals.totalSellingPrice));
                    }
                });
            }
        });
    }

    private final CategoryClickListener categoryClickListener = new CategoryClickListener() {
        @Override
        public void onClick(String category) {
            if (category != null && !categoryList.isEmpty()) {
                Intent intent = new Intent(requireContext(), ItemList.class);
                intent.putExtra("category", category);
                startActivityForResult(intent, ITEM_LIST_REQUEST_CODE);
            } else {
                Toast.makeText(requireContext(), "Please add items first", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onLongClick(String category, CardView cardView) {
            // Handle long click if needed
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}