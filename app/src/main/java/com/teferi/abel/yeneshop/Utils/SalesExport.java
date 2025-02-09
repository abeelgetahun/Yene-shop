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
    // This date format matches your stored sales date format, e.g., "Sat, 8 Feb 2025 11:13 AM"
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
                    // Retrieve all sales and filter to only include those within the last 24 hours.
                    List<Sales> allSalesDaily = database.mainDao().getAllSales();
                    Date now = new Date();
                    Date cutoff = new Date(now.getTime() - (24L * 60 * 60 * 1000));  // 24 hours ago
                    List<Sales> dailySales = new ArrayList<>();
                    for (Sales sale : allSalesDaily) {
                        Date saleDate = saleSdf.parse(sale.getDate());
                        if (saleDate.after(cutoff)) {
                            dailySales.add(sale);
                        }
                    }
                    salesList = dailySales;
                    filePrefix = "Daily_Sales";
                    break;
                case "MONTHLY":
                    // Retrieve all sales and filter to only include those within the last 30 days.
                    List<Sales> allSalesMonthly = database.mainDao().getAllSales();
                    Date nowMonthly = new Date();
                    Date cutoffMonthly = new Date(nowMonthly.getTime() - (30L * 24 * 60 * 60 * 1000));  // 30 days ago
                    List<Sales> monthlySales = new ArrayList<>();
                    for (Sales sale : allSalesMonthly) {
                        Date saleDate = saleSdf.parse(sale.getDate());
                        if (saleDate.after(cutoffMonthly)) {
                            monthlySales.add(sale);
                        }
                    }
                    salesList = monthlySales;
                    filePrefix = "Monthly_Sales";
                    break;
                case "ALL_SALES":
                    salesList = database.mainDao().getAllSales();
                    filePrefix = "All_Sales";
                    break;
                case "CUSTOM_DATE":
                    // For custom date, we need to filter records from the user-selected start date to now.
                    filePrefix = "Sales_" + startDate + "_to_" + endDate;
                    List<Sales> allSalesCustom = database.mainDao().getAllSales();
                    // Parse the start and end dates (assuming they are passed in "yyyy-MM-dd" format)
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    Date customStart = sdf.parse(startDate);
                    Date customEnd = sdf.parse(endDate);
                    // Adjust the end date to include the entire day (set time to 23:59:59)
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(customEnd);
                    cal.set(Calendar.HOUR_OF_DAY, 23);
                    cal.set(Calendar.MINUTE, 59);
                    cal.set(Calendar.SECOND, 59);
                    customEnd = cal.getTime();

                    List<Sales> filteredSales = new ArrayList<>();
                    for (Sales sale : allSalesCustom) {
                        Date saleDate = saleSdf.parse(sale.getDate());
                        if (!saleDate.before(customStart) && !saleDate.after(customEnd)) {
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
            // For older Android versions, you can implement similar logic to write to external storage.
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
