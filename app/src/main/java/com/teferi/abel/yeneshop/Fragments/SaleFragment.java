package com.teferi.abel.yeneshop.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.teferi.abel.yeneshop.Adapter.SearchAdapter;
import com.teferi.abel.yeneshop.Database.MainDao;
import com.teferi.abel.yeneshop.Database.RoomDB;
import com.teferi.abel.yeneshop.Models.Items;
import com.teferi.abel.yeneshop.Models.Sales;
import com.teferi.abel.yeneshop.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SaleFragment extends Fragment {

    // UI Components
    private EditText salesNameEdit, salesQuantityEdit, salesSpEdit;
    private TextView salesItemLeft;
    private MaterialButton saleBtn;
    private RecyclerView searchRecyclerView;
    private SearchAdapter searchAdapter;
    private CardView searchCardView;

    // Data-related variables
    private List<Items> itemsList;
    private MainDao mainDao;
    private Items currentItem = null;

    // Threading components
    private ExecutorService executorService;
    private Handler mainHandler;

    // Constants for styling
    private static final int INSUFFICIENT_COLOR = Color.RED;
    private static int NORMAL_COLOR;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sale, container, false);

        // Initialize components
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        NORMAL_COLOR = ContextCompat.getColor(requireContext(), R.color.btn_color);

        initViews(view);
        setupDatabase();
        setupSearchFunctionality();
        setupQuantityValidation();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh items list when returning to fragment
        executorService.execute(() -> {
            itemsList = mainDao.getAll();
            mainHandler.post(() -> {
                if (searchAdapter != null) {
                    searchAdapter.setFullList(itemsList);
                }
            });
        });
    }

    private void initViews(View view) {
        // Find all views
        salesNameEdit = view.findViewById(R.id.sales_name);
        salesQuantityEdit = view.findViewById(R.id.sales_quantity);
        salesSpEdit = view.findViewById(R.id.sales_sp);
        salesItemLeft = view.findViewById(R.id.sales_item_left_);
        saleBtn = view.findViewById(R.id.sale_btn);
        searchCardView = view.findViewById(R.id.search_card_view);
        searchRecyclerView = view.findViewById(R.id.search_recycler_view);

        saleBtn.setEnabled(false);
        setupSaleButton();


        salesNameEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showSearchCardWithAnimation();
                String searchText = salesNameEdit.getText().toString().trim();
                if (!searchText.isEmpty()) {
                    executorService.execute(() -> filterItems(searchText));
                } else {
                    showAllItems();
                }
            }
        });

        salesQuantityEdit.setFilters(new InputFilter[]{
                (source, start, end, dest, dstart, dend) -> {
                    try {
                        String newVal = dest.toString().substring(0, dstart) +
                                source.toString().substring(start, end) +
                                dest.toString().substring(dend);
                        int input = Integer.parseInt(newVal);
                        if (input >= 0) return null;
                    } catch (NumberFormatException nfe) {
                    }
                    return "";
                }
        });


        // Modify the salesNameEdit click listener
        salesNameEdit.setOnClickListener(v -> {
            if (searchCardView.getVisibility() != View.VISIBLE) {
                showSearchCardWithAnimation();
                showAllItems();
            }
        });
        // Modify the focus change listener
        salesNameEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showSearchCardWithAnimation();
                String searchText = salesNameEdit.getText().toString().trim();
                if (!searchText.isEmpty()) {
                    executorService.execute(() -> filterItems(searchText));
                } else {
                    showAllItems();
                }
            }
        });

        searchRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        searchRecyclerView.setHasFixedSize(true);
    }

    private void refreshSearchData() {
        executorService.execute(() -> {
            List<Items> freshItems = mainDao.getAll();
            mainHandler.post(() -> {
                itemsList = freshItems;
                if (searchAdapter != null) {
                    searchAdapter.setFullList(itemsList);
                }
            });
        });
    }

    private void setupSearchFunctionality() {
        searchAdapter = new SearchAdapter(new ArrayList<>(), item -> {
            currentItem = item;
            updateUIForSelectedItem(item);
            hideSearchCardWithAnimation(() -> {
                salesNameEdit.clearFocus();
                salesQuantityEdit.requestFocus();
                showKeyboard(salesQuantityEdit);
            });
        });

        searchRecyclerView.setAdapter(searchAdapter);
        refreshSearchData();

        salesNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().toLowerCase().trim();
                if (searchCardView.getVisibility() != View.VISIBLE) {
                    showSearchCardWithAnimation();
                }
                if (searchText.isEmpty()) {
                    showAllItems();
                } else {
                    executorService.execute(() -> filterItems(searchText));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupSaleButton() {
        saleBtn.setOnClickListener(v -> {
            if (currentItem == null) {
                showToast("Please select an item first");
                return;
            }

            String quantityStr = salesQuantityEdit.getText().toString();
            String sellingPriceStr = salesSpEdit.getText().toString();

            if (quantityStr.isEmpty()) {
                showToast("Please enter quantity");
                return;
            }

            if (sellingPriceStr.isEmpty()) {
                showToast("Please enter selling price");
                return;
            }

            int quantity = Integer.parseInt(quantityStr);
            double sellingPrice = Double.parseDouble(sellingPriceStr);

            if (quantity <= 0) {
                showToast("Please enter a valid quantity");
                return;
            }

            if (sellingPrice <= 0) {
                showToast("Please enter a valid selling price");
                return;
            }

            if (quantity > currentItem.getQuantity()) {
                showToast("Insufficient quantity available");
                return;
            }

            // Create sale record
            Sales sale = new Sales();
            sale.setItem_id(currentItem.getId());
            sale.setName(currentItem.getName());
            sale.setCategory(currentItem.getCategory());
            sale.setQuantity(quantity);
            sale.setPurchasing_price(currentItem.getPurchasing_price());
            sale.setSelling_price(sellingPrice);
            sale.setTax(currentItem.getTax());
            sale.setDate(getCurrentDate());

            // Process sale in background
            executorService.execute(() -> {
                mainDao.processSale(sale, currentItem.getId(), quantity);

                // Update the current item's quantity in memory
                currentItem.setQuantity(currentItem.getQuantity() - quantity);

                // Refresh the items list
                itemsList = mainDao.getAll();

                mainHandler.post(() -> {
                    // Update search adapter with fresh data
                    if (searchAdapter != null) {
                        searchAdapter.setFullList(itemsList);
                    }
                    showToast("Sale completed successfully");
                    resetFields();
                });
            });
        });
    }


    private void setupQuantityValidation() {
        salesQuantityEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateQuantity(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void validateQuantity(String quantityStr) {
        if (currentItem == null) return;

        try {
            int enteredQuantity = quantityStr.isEmpty() ? 0 : Integer.parseInt(quantityStr);
            int remainingQuantity = currentItem.getQuantity() - enteredQuantity;

            if (remainingQuantity >= 0) {
                updateUIForValidQuantity(remainingQuantity);
            } else {
                updateUIForInsufficientQuantity();
            }
        } catch (NumberFormatException e) {
            resetQuantityUI();
        }
    }

    private void updateUIForValidQuantity(int remainingQuantity) {
        salesItemLeft.setTextColor(Color.BLACK);
        salesItemLeft.setText("Left items: " + remainingQuantity);
        saleBtn.setEnabled(true);
        saleBtn.setBackgroundTintList(ColorStateList.valueOf(NORMAL_COLOR));
    }

    private void updateUIForInsufficientQuantity() {
        salesItemLeft.setTextColor(INSUFFICIENT_COLOR);
        salesItemLeft.setText("NOT SUFFICIENT QUANTITY!");
        saleBtn.setEnabled(false);
        saleBtn.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
    }

    private void resetQuantityUI() {
        salesItemLeft.setTextColor(Color.BLACK);
        salesItemLeft.setText("Left items: " + currentItem.getQuantity());
        saleBtn.setEnabled(false);
        saleBtn.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
    }

    private void setupDatabase() {
        RoomDB database = RoomDB.getInstance(requireContext());
        mainDao = database.mainDao();
        itemsList = new ArrayList<>();

        executorService.execute(() -> {
            itemsList = mainDao.getAll();
            mainHandler.post(() -> {
                if (searchAdapter != null) {
                    searchAdapter.setFullList(itemsList);
                }
            });
        });
    }


    private void updateUIForSelectedItem(Items item) {
        salesNameEdit.setText(item.getName());
        salesSpEdit.setText(String.valueOf(item.getSelling_price()));
        salesItemLeft.setTextColor(Color.BLACK);
        salesItemLeft.setText("Left items: " + item.getQuantity());
        salesQuantityEdit.setText("");
        saleBtn.setEnabled(false);
        saleBtn.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
    }

    private void showSearchCardWithAnimation() {
        searchCardView.setAlpha(0f);
        searchCardView.setVisibility(View.VISIBLE);
        searchCardView.animate()
                .alpha(1f)
                .setDuration(200)
                .setListener(null)
                .start();
    }

    private void hideSearchCardWithAnimation(Runnable onComplete) {
        searchCardView.animate()
                .alpha(0f)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        searchCardView.setVisibility(View.GONE);
                        searchCardView.setAlpha(1f);
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                })
                .start();
    }

    private void showKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }


    private void showAllItems() {
        mainHandler.post(() -> {
            if (itemsList != null && !itemsList.isEmpty()) {
                searchAdapter.updateList(itemsList);
                if (searchCardView.getVisibility() != View.VISIBLE) {
                    showSearchCardWithAnimation();
                }
            }
        });
    }

    private void filterItems(String searchText) {
        if (itemsList == null) return;

        List<Items> filteredList = new ArrayList<>();
        for (Items item : itemsList) {
            if (item.getName().toLowerCase().contains(searchText) ||
                    String.valueOf(item.getId()).contains(searchText)) {
                filteredList.add(item);
            }
        }

        mainHandler.post(() -> {
            searchAdapter.updateList(filteredList);
            if (filteredList.isEmpty()) {
                hideSearchCardWithAnimation(null);
            } else if (searchCardView.getVisibility() != View.VISIBLE) {
                showSearchCardWithAnimation();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executorService.shutdown();
    }

    private void resetFields() {
        salesNameEdit.setText("");
        salesQuantityEdit.setText("");
        salesSpEdit.setText("");
        salesItemLeft.setText("");
        currentItem = null;
        saleBtn.setEnabled(false);
        saleBtn.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy hh:mm a", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Add new method to hide keyboard
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Reset the search card visibility when leaving the fragment
        if (searchCardView.getVisibility() == View.VISIBLE) {
            hideSearchCardWithAnimation(null);
        }
    }
}