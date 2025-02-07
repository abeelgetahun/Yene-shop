package com.teferi.abel.yeneshop.Menus;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.teferi.abel.yeneshop.Database.RoomDB;
import com.teferi.abel.yeneshop.Models.Items;
import com.teferi.abel.yeneshop.R;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportByCategory extends AppCompatActivity {
    private RoomDB database;
    private static final int REQUEST_CODE_SAVE = 2;
    private int quantityThreshold = 0; // User-defined quantity for export

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_by_category);

        database = RoomDB.getInstance(this);

        ImageButton backButton = findViewById(R.id.repcategory);
        backButton.setOnClickListener(v -> finish());

        Button exportButton = findViewById(R.id.export_test);
        exportButton.setOnClickListener(v -> showExportOptions());
    }

    /**
     * Show dialog for selecting export option
     */
    private void showExportOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Export Option");

        String[] options = {"All Items (qty > 0)", "Sold Out Items (qty == 0)", "Items Above User Input"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    exportToCSV(database.mainDao().getItemsWithQuantityGreaterThan(0), "All_Items");
                    break;
                case 1:
                    exportToCSV(database.mainDao().getSoldOutItems(), "Sold_Out_Items");
                    break;
                case 2:
                    showQuantityInputDialog();
                    break;
            }
        });

        builder.create().show();
    }

    /**
     * Show dialog for user to enter minimum quantity
     */
    private void showQuantityInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Minimum Quantity");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_quantity_input, null);
        EditText input = view.findViewById(R.id.input_quantity);
        builder.setView(view);

        builder.setPositiveButton("Export", (dialog, which) -> {
            String inputValue = input.getText().toString();
            if (!inputValue.isEmpty()) {
                quantityThreshold = Integer.parseInt(inputValue);
                exportToCSV(database.mainDao().getItemsWithQuantityGreaterThan(quantityThreshold), "Items_Above_" + quantityThreshold);
            } else {
                Toast.makeText(this, "Please enter a valid number!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    /**
     * Export selected items to CSV file
     */
    private void exportToCSV(List<Items> itemsList, String filePrefix) {
        if (itemsList.isEmpty()) {
            Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = filePrefix + "_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".csv";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveToDocuments(fileName, itemsList);
        } else {
            saveWithSAF(fileName);
        }
    }

    private void saveToDocuments(String fileName, List<Items> itemsList) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/YeneShop");

        Uri fileUri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
        if (fileUri != null) {
            try (OutputStream outputStream = getContentResolver().openOutputStream(fileUri)) {
                writeCSV(outputStream, itemsList);
                Toast.makeText(this, "File saved in Documents/YeneShop", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error saving file: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Failed to create file", Toast.LENGTH_LONG).show();
        }
    }

    private void saveWithSAF(String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, REQUEST_CODE_SAVE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SAVE && resultCode == RESULT_OK) {
            Uri fileUri = data != null ? data.getData() : null;
            if (fileUri != null) {
                try (OutputStream outputStream = getContentResolver().openOutputStream(fileUri)) {
                    writeCSV(outputStream, database.mainDao().getAll());
                    Toast.makeText(this, "File saved successfully!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Error saving file: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void writeCSV(OutputStream outputStream, List<Items> itemsList) throws Exception {
        if (outputStream == null) return;

        StringBuilder csvData = new StringBuilder();
        csvData.append("ID,Name,Category,Quantity,Purchasing Price,Selling Price,Tax,Date\n");

        for (Items item : itemsList) {
            csvData.append(item.getId()).append(",");
            csvData.append(escapeCsvField(item.getName())).append(",");
            csvData.append(escapeCsvField(item.getCategory())).append(",");
            csvData.append(item.getQuantity()).append(",");
            csvData.append(item.getPurchasing_price()).append(",");
            csvData.append(item.getSelling_price()).append(",");
            csvData.append(item.getTax()).append(",");
            csvData.append(escapeCsvField(item.getDate())).append("\n");
        }

        outputStream.write(csvData.toString().getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private String escapeCsvField(String field) {
        if (field == null) return "";
        field = field.replace("\"", "\"\"");
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field + "\"";
        }
        return field;
    }
}
