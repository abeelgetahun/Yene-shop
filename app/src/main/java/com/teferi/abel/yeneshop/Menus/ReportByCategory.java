package com.teferi.abel.yeneshop.Menus;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.teferi.abel.yeneshop.Database.RoomDB;
import com.teferi.abel.yeneshop.Models.Items;
import com.teferi.abel.yeneshop.R;
import com.airbnb.lottie.LottieAnimationView;
import com.teferi.abel.yeneshop.Utils.SalesExport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.os.Handler;

public class ReportByCategory extends AppCompatActivity {
    private RoomDB database;
    private static final int REQUEST_CODE_SAVE = 2;
    private int quantityThreshold = 0; // User-defined quantity for export
    private SalesExport salesExport;
    private ProgressDialog progressDialog; // For animation effect

    // Add these as class fields in ReportByCategory.java
    private FrameLayout animationContainer;
    private LottieAnimationView exportAnimation;
    private LottieAnimationView salesExportAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_by_category);

        database = RoomDB.getInstance(this);
        // Initialize the sales export helper
        salesExport = new SalesExport(this, database);

        ImageButton backButton = findViewById(R.id.repcategory);
        backButton.setOnClickListener(v -> finish());

        Button exportButton = findViewById(R.id.item_export);
        exportButton.setOnClickListener(v -> showExportOptions());

        salesExport = new SalesExport(this, database);

        Button salesExportButton = findViewById(R.id.sales_export);
        salesExportButton.setOnClickListener(v -> showSalesExportDialog());

        animationContainer = findViewById(R.id.animation_container);
        exportAnimation = findViewById(R.id.export_animation);
        salesExportAnimation = findViewById(R.id.sales_export_animation);

    }

    /**
     * Show dialog for selecting export option
     */
    private void showExportOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Items Export Option");

        String[] options = {"All Items (qty >= 0)", "Sold Out Items (qty == 0)", "Items Above User Input"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    exportItemsWithAnimation(database.mainDao().getItemsWithQuantityGreaterThan(0), "All_Items");
                    break;
                case 1:
                    exportItemsWithAnimation(database.mainDao().getSoldOutItems(), "Sold_Out_Items");
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

        // Inflate a simple layout with an EditText (your dialog_quantity_input.xml)
        View view = getLayoutInflater().inflate(R.layout.dialog_quantity_input, null);
        EditText input = view.findViewById(R.id.input_quantity);
        builder.setView(view);

        builder.setPositiveButton("Export", (dialog, which) -> {
            String inputValue = input.getText().toString();
            if (!inputValue.isEmpty()) {
                quantityThreshold = Integer.parseInt(inputValue);
                exportItemsWithAnimation(database.mainDao().getItemsWithQuantityGreaterThan(quantityThreshold),
                        "Items_Above_" + quantityThreshold);
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
                    Toast.makeText(this, "Error saving file", Toast.LENGTH_LONG).show();
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



    private void showSalesExportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Sales Report Type");

        String[] options = {"Daily Sales (Last 24 Hours)", "Monthly Sales (Last 30 Days)", "All Sales Data", "Select Start Date"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    exportSalesWithAnimation("DAILY", null, null);
                    break;
                case 1:
                    exportSalesWithAnimation("MONTHLY", null, null);
                    break;
                case 2:
                    exportSalesWithAnimation("ALL_SALES", null, null);
                    break;
                case 3:
                    showDatePicker();
                    break;
            }
        });
        builder.create().show();
    }

    /**
     * Shows a date picker dialog. For custom export, exports data from the selected start date up to now.
     */

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    // Format the selected date as yyyy-MM-dd
                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    // Get the current date in the same format
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    // Export all sales from the selected date up to now
                    exportSalesWithAnimation("CUSTOM_DATE", selectedDate, currentDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePicker.show();
    }





    // Replace the existing exportItemsWithAnimation method with this:
    private void exportItemsWithAnimation(List<Items> itemsList, String filePrefix) {
        if (itemsList.isEmpty()) {
            Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show animation container with fade in
        animationContainer.setVisibility(View.VISIBLE);
        animationContainer.setAlpha(0f);
        animationContainer.animate().alpha(1f).setDuration(300).start();

        // Show and play the export animation
        exportAnimation.setVisibility(View.VISIBLE);
        salesExportAnimation.setVisibility(View.GONE);
        exportAnimation.playAnimation();

        new Handler().postDelayed(() -> {
            exportItemsToCSV(itemsList, filePrefix);

            // Fade out animation container
            animationContainer.animate().alpha(0f).setDuration(300)
                    .withEndAction(() -> {
                        animationContainer.setVisibility(View.GONE);
                        exportAnimation.pauseAnimation();
                        exportAnimation.setVisibility(View.GONE);
                    }).start();

            Toast.makeText(this, "Items export completed successfully!", Toast.LENGTH_LONG).show();
        }, 3000);
    }

    private void exportItemsToCSV(List<Items> itemsList, String filePrefix) {
        if (itemsList.isEmpty()) {
            Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = filePrefix + "_" + timestamp + ".csv";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/Yene-Shop");

            Uri fileUri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
            if (fileUri != null) {
                try (OutputStream outputStream = getContentResolver().openOutputStream(fileUri)) {
                    writeCSV(outputStream, itemsList);
                    Toast.makeText(this, "File saved to Documents/Yene-Shop: " + fileName, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Error saving file: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        } else {
            // For Android versions below Q
            try {
                File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                File yeneShopDir = new File(documentsDir, "Yene-Shop");

                if (!yeneShopDir.exists()) {
                    yeneShopDir.mkdirs(); // Create the directory if it doesn't exist
                }

                File file = new File(yeneShopDir, fileName);
                try (OutputStream outputStream = new FileOutputStream(file)) {
                    writeCSV(outputStream, itemsList);
                    Toast.makeText(this, "File saved to Documents/Yene-Shop: " + fileName, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error saving file: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    // Replace the existing exportSalesWithAnimation method with this:
    private void exportSalesWithAnimation(final String exportType, final String startDate, final String endDate) {
        // Show animation container with fade in
        animationContainer.setVisibility(View.VISIBLE);
        animationContainer.setAlpha(0f);
        animationContainer.animate().alpha(1f).setDuration(300).start();

        // Show and play the sales export animation
        exportAnimation.setVisibility(View.GONE);
        salesExportAnimation.setVisibility(View.VISIBLE);
        salesExportAnimation.playAnimation();

        new Handler().postDelayed(() -> {
            salesExport.exportSales(exportType, startDate, endDate);

            // Fade out animation container
            animationContainer.animate().alpha(0f).setDuration(300)
                    .withEndAction(() -> {
                        animationContainer.setVisibility(View.GONE);
                        salesExportAnimation.pauseAnimation();
                        salesExportAnimation.setVisibility(View.GONE);
                    }).start();

            Toast.makeText(this, "Sales export completed successfully!", Toast.LENGTH_LONG).show();
        }, 3000);
    }

}