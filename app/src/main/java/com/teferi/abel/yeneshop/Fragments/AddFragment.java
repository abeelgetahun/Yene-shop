package com.teferi.abel.yeneshop.Fragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.teferi.abel.yeneshop.Database.RoomDB;
import com.teferi.abel.yeneshop.Models.Items;
import com.teferi.abel.yeneshop.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddFragment extends Fragment {
    EditText add_name, add_category, add_quantity, add_pp, add_sp, add_tax;
    MaterialButton add_btn;
    Items item;
    private Map<EditText, TextView> errorTextViews = new HashMap<>();

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // Initialize views
        add_btn = view.findViewById(R.id.item_save_button);
        add_name = view.findViewById(R.id.item_name_input);
        add_category = view.findViewById(R.id.item_category_input);
        add_quantity = view.findViewById(R.id.item_quantity_input);
        add_pp = view.findViewById(R.id.item_purchasingprice_input);
        add_sp = view.findViewById(R.id.item_sellingprice_input);
        add_tax = view.findViewById(R.id.item_tax_input);

        // Initialize error TextViews
        initializeErrorTextViews(view);

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateAndSaveItem()) {
                    clearInputs();
                }
            }
        });

        return view;
    }

    private void initializeErrorTextViews(View view) {
        // Create error TextViews for each EditText
        EditText[] editTexts = {add_name, add_category, add_quantity, add_pp, add_sp, add_tax};

        for (EditText editText : editTexts) {
            TextView errorText = new TextView(requireContext());
            errorText.setTextColor(Color.RED);
            errorText.setTextSize(12);
            errorText.setVisibility(View.GONE);

            // Add error TextView right after the EditText in the layout
            ViewGroup parent = (ViewGroup) editText.getParent();
            int index = parent.indexOfChild(editText);
            parent.addView(errorText, index + 1);

            errorTextViews.put(editText, errorText);
        }
    }

    private boolean validateAndSaveItem() {
        boolean isValid = true;
        String name = add_name.getText().toString().trim();
        String categoryInput = add_category.getText().toString().trim();

        // Reset all error states
        for (EditText editText : errorTextViews.keySet()) {
            resetErrorStyle(editText);
        }

        // Name validation
        if (name.isEmpty()) {
            setErrorStyle(add_name, "Name is required");
            isValid = false;
        }

        // Category processing
        String category = "null";
        if (!categoryInput.isEmpty()) {
            StringBuilder categoryBuilder = new StringBuilder();
            for (int i = 0; i < categoryInput.length(); i++) {
                char currentChar = categoryInput.charAt(i);
                if (Character.isLetter(currentChar)) {
                    categoryBuilder.append(Character.toUpperCase(currentChar));
                } else {
                    categoryBuilder.append(currentChar);
                }
            }
            category = categoryBuilder.toString();
        }

        // Quantity validation
        int quantity = 0;
        try {
            if (!add_quantity.getText().toString().isEmpty()) {
                quantity = Integer.parseInt(add_quantity.getText().toString().trim());
            }
        } catch (NumberFormatException e) {
            setErrorStyle(add_quantity, "Invalid number format");
            isValid = false;
        }

        // Price validation
        double purchasing_price = 0;
        try {
            if (!add_pp.getText().toString().isEmpty()) {
                purchasing_price = Double.parseDouble(add_pp.getText().toString().trim());
            }
        } catch (NumberFormatException e) {
            setErrorStyle(add_pp, "Invalid price format");
            isValid = false;
        }

        double selling_price = 0;
        try {
            if (!add_sp.getText().toString().isEmpty()) {
                selling_price = Double.parseDouble(add_sp.getText().toString().trim());
            }
        } catch (NumberFormatException e) {
            setErrorStyle(add_sp, "Invalid price format");
            isValid = false;
        }

        int tax = 0;
        try {
            if (!add_tax.getText().toString().isEmpty()) {
                tax = Integer.parseInt(add_tax.getText().toString().trim());
            }
        } catch (NumberFormatException e) {
            setErrorStyle(add_tax, "Invalid tax format");
            isValid = false;
        }

        if (!isValid) {
            return false;
        }

        // Save item if validation passed
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy hh:mm a", Locale.getDefault());
        Date date = new Date();

        item = new Items();
        item.setName(name);
        item.setCategory(category);
        item.setQuantity(quantity);
        item.setPurchasing_price(purchasing_price);
        item.setSelling_price(selling_price);
        item.setTax(tax);
        item.setDate(formatter.format(date));

        // Add item to database
        RoomDB database = RoomDB.getInstance(requireContext());
        database.mainDao().insert(item);

        if (getActivity() != null) {
            Toast.makeText(getActivity(), "Item saved successfully", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    private void setErrorStyle(EditText editText, String errorMessage) {
        editText.getBackground().mutate().setColorFilter(
                ContextCompat.getColor(requireContext(), android.R.color.holo_red_light),
                PorterDuff.Mode.SRC_ATOP
        );

        TextView errorText = errorTextViews.get(editText);
        if (errorText != null) {
            errorText.setText(errorMessage);
            errorText.setVisibility(View.VISIBLE);
        }
    }

    private void resetErrorStyle(EditText editText) {
        editText.getBackground().mutate().setColorFilter(
                ContextCompat.getColor(requireContext(), android.R.color.darker_gray),
                PorterDuff.Mode.SRC_ATOP
        );

        TextView errorText = errorTextViews.get(editText);
        if (errorText != null) {
            errorText.setVisibility(View.GONE);
        }
    }

    private void clearInputs() {
        add_name.setText("");
        add_category.setText("");
        add_quantity.setText("");
        add_pp.setText("");
        add_sp.setText("");
        add_tax.setText("");

        // Reset all styles
        for (EditText editText : errorTextViews.keySet()) {
            resetErrorStyle(editText);
        }
    }
}