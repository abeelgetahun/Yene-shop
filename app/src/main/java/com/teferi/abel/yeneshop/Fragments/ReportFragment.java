package com.teferi.abel.yeneshop.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.teferi.abel.yeneshop.Database.MainDao;
import com.teferi.abel.yeneshop.Database.RoomDB;
import com.teferi.abel.yeneshop.Models.Sales;
import com.teferi.abel.yeneshop.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ReportFragment extends Fragment {
    private TextView dailyReportTextView, monthlyReportTextView;
    private RoomDB database;
    private MainDao mainDao;
    private Handler mainHandler;
    private boolean isFragmentVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        initializeViews(view);
        initializeDatabase();
        updateProfitReports();
        return view;
    }

    private void initializeViews(View view) {
        dailyReportTextView = view.findViewById(R.id.daily_report);
        monthlyReportTextView = view.findViewById(R.id.monthly_report);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    private void initializeDatabase() {
        database = RoomDB.getInstance(requireContext());
        mainDao = database.mainDao();
    }

    @Override
    public void onResume() {
        super.onResume();
        isFragmentVisible = true;
        updateProfitReports();
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentVisible = false;
    }

    private void updateProfitReports() {
        CompletableFuture.supplyAsync(() -> {
            try {
                // Get all sales records
                List<Sales> allSales = mainDao.getAllSales();

                // Get current date-time
                Calendar now = Calendar.getInstance();
                Date currentDate = now.getTime();

                // Calculate 24 hours ago
                Calendar yesterday = Calendar.getInstance();
                yesterday.add(Calendar.HOUR_OF_DAY, -24);
                Date yesterdayDate = yesterday.getTime();

                // Calculate 30 days ago
                Calendar thirtyDaysAgo = Calendar.getInstance();
                thirtyDaysAgo.add(Calendar.DAY_OF_MONTH, -30);
                Date thirtyDaysAgoDate = thirtyDaysAgo.getTime();

                // Filter and calculate reports
                ProfitReport dailyProfit = calculateProfitForDateRange(allSales, yesterdayDate, currentDate);
                ProfitReport monthlyProfit = calculateProfitForDateRange(allSales, thirtyDaysAgoDate, currentDate);

                return new ProfitReports(dailyProfit, monthlyProfit);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).thenAcceptAsync(reports -> {
            if (isFragmentVisible && reports != null) {
                updateUIWithReports(reports);
            }
        }, runnable -> mainHandler.post(runnable));
    }

    private ProfitReport calculateProfitForDateRange(List<Sales> sales, Date startDate, Date endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy hh:mm a", Locale.ENGLISH);
        double totalProfit = 0;
        double totalTaxAmount = 0;
        double netProfit = 0;

        for (Sales sale : sales) {
            try {
                Date saleDate = sdf.parse(sale.getDate());
                if (saleDate != null && !saleDate.before(startDate) && !saleDate.after(endDate)) {
                    double profit = (sale.getSelling_price() - sale.getPurchasing_price()) * sale.getQuantity();
                    double taxAmount = (sale.getSelling_price() * sale.getQuantity() * sale.getTax()) / 100;

                    totalProfit += profit;
                    totalTaxAmount += taxAmount;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        netProfit = totalProfit - totalTaxAmount;
        return new ProfitReport(totalProfit, totalTaxAmount, netProfit);
    }


    private void updateUIWithReports(ProfitReports reports) {
        String dailyReport = formatProfitReport( reports.dailyProfit);
        String monthlyReport = formatProfitReport( reports.monthlyProfit);

        mainHandler.post(() -> {
            if (isFragmentVisible) {
                dailyReportTextView.setText(dailyReport);
                monthlyReportTextView.setText(monthlyReport);
            }
        });
    }

    private String formatProfitReport(ProfitReport profit) {
        return String.format(Locale.getDefault(),
                        "Gross Profit: %.2f\n" +
                        "Tax Amount: %.2f\n" +
                        "Net Profit: %.2f",
                profit.totalProfit,
                profit.totalTaxAmount,
                profit.netProfit);
    }

    private static class ProfitReport {
        final double totalProfit;
        final double totalTaxAmount;
        final double netProfit;

        ProfitReport(double totalProfit, double totalTaxAmount, double netProfit) {
            this.totalProfit = totalProfit;
            this.totalTaxAmount = totalTaxAmount;
            this.netProfit = netProfit;
        }
    }

    private static class ProfitReports {
        final ProfitReport dailyProfit;
        final ProfitReport monthlyProfit;

        ProfitReports(ProfitReport dailyProfit, ProfitReport monthlyProfit) {
            this.dailyProfit = dailyProfit;
            this.monthlyProfit = monthlyProfit;
        }
    }
}