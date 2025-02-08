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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SalesExport {
    private final Context context;
    private final RoomDB database;
    private final SimpleDateFormat saleSdf = new SimpleDateFormat("EEE, d MMM yyyy hh:mm a", Locale.ENGLISH);

    public SalesExport(Context context, RoomDB database) {
        this.context = context;
        this.database = database;
    }

    /**
     * Exports sales data based on the given exportType.
     * exportType can be:
     * - "DAILY"    : Sales from the last 24 hours.
     * - "MONTHLY"  : Sales from the last 30 days.
     * - "ALL_SALES": All sales records.
     * - "CUSTOM_DATE": Sales from the user-selected start date up to now.
     */
    public void exportSales(String exportType, String startDate, String endDate) {
        List<Sales> salesList;
        String filePrefix;

        try {
            switch (exportType) {
                case "DAILY":
                    salesList = database.mainDao().getDailySales();
                    filePrefix = "Daily_Sales";
                    break;
                case "MONTHLY":
                    salesList = database.mainDao().getMonthlySales();
                    filePrefix = "Monthly_Sales";
                    break;
                case "ALL_SALES":
                    salesList = database.mainDao().getAllSales();
                    filePrefix = "All_Sales";
                    break;
                case "CUSTOM_DATE":
                    // For custom date, we need to filter records from the user-selected start date to now.
                    // The user-selected startDate and endDate are in "yyyy-MM-dd" format.
                    filePrefix = "Sales_" + startDate + "_to_" + endDate;
                    // First, get all sales (or you can limit to those before "endDate" using a different query)
                    List<Sales> allSales = database.mainDao().getAllSales();

                    // Parse the start and end dates
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    Date start = sdf.parse(startDate);
                    Date end = sdf.parse(endDate);

                    // Adjust the end date to include the entire day (set time to 23:59:59)
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(end);
                    cal.set(Calendar.HOUR_OF_DAY, 23);
                    cal.set(Calendar.MINUTE, 59);
                    cal.set(Calendar.SECOND, 59);
                    end = cal.getTime();

                    // Filter allSales: include any sale whose date is between start and end (inclusive)
                    List<Sales> filteredSales = new ArrayList<>();
                    for (Sales sale : allSales) {
                        // Parse the sale's date (stored as "EEE, d MMM yyyy hh:mm a")
                        Date saleDate = saleSdf.parse(sale.getDate());
                        // Include if saleDate is >= start and <= end
                        if (!saleDate.before(start) && !saleDate.after(end)) {
                            filteredSales.add(sale);
                        }
                    }
                    salesList = filteredSales;
                    break;
                default:
                    return;
            }

            if (salesList == null || salesList.isEmpty()) {
                Toast.makeText(context, "No sales data found for the selected period", Toast.LENGTH_SHORT).show();
                return;
            }

            exportToCSV(salesList, filePrefix);

        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void exportToCSV(List<Sales> salesList, String filePrefix) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = filePrefix + "_" + timestamp + ".csv";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
            // Save in Documents/Yene-Shop folder
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/Yene-Shop");

            Uri fileUri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
            if (fileUri != null) {
                try (OutputStream outputStream = context.getContentResolver().openOutputStream(fileUri)) {
                    writeSalesCSV(outputStream, salesList);
                    Toast.makeText(context, "Sales data exported to Documents/Yene-Shop", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(context, "Error exporting sales data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        } else {
            // For older Android versions, you can implement a similar method to write to external storage.
            Toast.makeText(context, "Export failed: Storage permission required", Toast.LENGTH_LONG).show();
        }
    }

    private void writeSalesCSV(OutputStream outputStream, List<Sales> salesList) throws Exception {
        StringBuilder csvData = new StringBuilder();
        // CSV Header
        csvData.append("ID,Item ID,Name,Category,Quantity,Purchasing Price,Selling Price,Tax,Date\n");

        // CSV Data
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
