package com.teferi.abel.yeneshop.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import com.teferi.abel.yeneshop.Database.RoomDB;
import com.teferi.abel.yeneshop.Models.Sales;

import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SalesExport {
    private final Context context;
    private final RoomDB database;
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEE, d MMM yyyy hh:mm a", Locale.ENGLISH);
    private final SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    public SalesExport(Context context, RoomDB database) {
        this.context = context;
        this.database = database;
    }

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
                    // Format the end date to include the entire day
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    cal.set(Calendar.HOUR_OF_DAY, 23);
                    cal.set(Calendar.MINUTE, 59);
                    cal.set(Calendar.SECOND, 59);
                    String formattedEndDate = sqlDateFormat.format(cal.getTime());

                    // Ensure startDate is in correct format
                    salesList = database.mainDao().getSalesByDateRange(startDate, formattedEndDate);
                    filePrefix = "Sales_" + startDate + "_to_" + formattedEndDate;
                    break;
                default:
                    return;
            }

            if (salesList.isEmpty()) {
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
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
            values.put(MediaStore.Downloads.RELATIVE_PATH, "Download");

            Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
                    writeSalesCSV(outputStream, salesList);
                    Toast.makeText(context, "Sales data exported successfully to Downloads folder", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(context, "Error exporting sales data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
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