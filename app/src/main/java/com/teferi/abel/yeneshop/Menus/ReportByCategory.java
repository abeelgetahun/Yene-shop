package com.teferi.abel.yeneshop.Menus;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_by_category);

        database = RoomDB.getInstance(this);

        ImageButton backButton = findViewById(R.id.repcategory);
        backButton.setOnClickListener(v -> finish());

        Button exportButton = findViewById(R.id.export_test);
        exportButton.setOnClickListener(v -> exportToCSV());
    }

    private void exportToCSV() {
        List<Items> itemsList = database.mainDao().getAll();
        if (itemsList.isEmpty()) {
            Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "inventory_report_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".csv";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ (Scoped Storage) - Use MediaStore API (No permission required)
            saveToDocuments(fileName, itemsList);
        } else {
            // Android 9 and below - Use Storage Access Framework (SAF)
            saveWithSAF(fileName);
        }
    }

    /**
     * ✅ Saves CSV file to Documents folder without needing permissions on Android 10+
     */
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

    /**
     * ✅ Allows the user to choose a save location via the system file picker
     */
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

    /**
     * Writes CSV data to an output stream
     */
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
        field = field.replace("\"", "\"\""); // Escape quotes
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field + "\"";
        }
        return field;
    }
}
