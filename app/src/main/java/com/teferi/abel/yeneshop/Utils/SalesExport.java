package com.teferi.abel.yeneshop.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.teferi.abel.yeneshop.Database.RoomDB;
import com.teferi.abel.yeneshop.Models.Sales;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SalesExport {
    private final Context context;
    private final RoomDB database;

    public SalesExport(Context context, RoomDB database) {
        this.context = context;
        this.database = database;
    }

    /**
     * Exports sales data based on the given exportType.
     * exportType can be:
     * "DAILY" - sales from last 24 hours,
     * "MONTHLY" - sales from the last 30 days,
     * "ALL_SALES" - all sales records,
     * "CUSTOM_DATE" - sales from user-selected start date up to now.
     */
    public void exportSales(String exportType, String startDate, String endDate) {
        List<Sales> salesList = getSalesData(exportType, startDate, endDate);

        if (salesList == null || salesList.isEmpty()) {
            Toast.makeText(context, "No sales data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "Sales_Report_" + exportType + "_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".csv";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveToDocuments(fileName, salesList);
        } else {
            Toast.makeText(context, "Export failed: Storage permission required", Toast.LENGTH_LONG).show();
        }
    }

    private List<Sales> getSalesData(String exportType, String startDate, String endDate) {
        try {
            switch (exportType) {
                case "DAILY":
                    return database.mainDao().getDailySales();
                case "MONTHLY":
                    return database.mainDao().getMonthlySales();
                case "ALL_SALES":
                    return database.mainDao().getAllSales();
                case "CUSTOM_DATE":
                    return database.mainDao().getSalesByDateRange(startDate, endDate);
                default:
                    return database.mainDao().getAllSales();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Database error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private void saveToDocuments(String fileName, List<Sales> salesList) {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/YeneShop");

            Uri fileUri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
            if (fileUri != null) {
                try (OutputStream outputStream = context.getContentResolver().openOutputStream(fileUri)) {
                    writeCSV(outputStream, salesList);
                    Toast.makeText(context, "Sales report saved in Documents/YeneShop", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Failed to create file", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error saving file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void writeCSV(OutputStream outputStream, List<Sales> salesList) throws Exception {
        if (outputStream == null) return;

        StringBuilder csvData = new StringBuilder();
        csvData.append("ID,Item ID,Name,Category,Quantity,Purchasing Price,Selling Price,Tax,Date\n");

        for (Sales sale : salesList) {
            csvData.append(sale.getId()).append(",");
            csvData.append(sale.getItem_id()).append(",");
            csvData.append(escapeCsvField(sale.getName())).append(",");
            csvData.append(escapeCsvField(sale.getCategory())).append(",");
            csvData.append(sale.getQuantity()).append(",");
            csvData.append(sale.getPurchasing_price()).append(",");
            csvData.append(sale.getSelling_price()).append(",");
            csvData.append(sale.getTax()).append(",");
            csvData.append(escapeCsvField(sale.getDate())).append("\n");
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
