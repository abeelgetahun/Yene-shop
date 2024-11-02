package com.teferi.abel.yeneshop.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teferi.abel.yeneshop.Database.MainDao;
import com.teferi.abel.yeneshop.Database.RoomDB;
import com.teferi.abel.yeneshop.R;

import java.util.Locale;


public class ReportFragment extends Fragment {
    private TextView dailyReportTextView, monthlyReportTextView;
    private RoomDB database;
    private Handler handler;
    private static final long UPDATE_INTERVAL = 60000; // Update every minute

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        // Initialize views
        dailyReportTextView = view.findViewById(R.id.daily_report);
        monthlyReportTextView = view.findViewById(R.id.monthly_report);

        // Initialize database and handler
        database = RoomDB.getInstance(requireContext());
        handler = new Handler(Looper.getMainLooper());

        // Start periodic updates
        startPeriodicUpdates();

        return view;
    }

    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            updateProfitReports();
            handler.postDelayed(this, UPDATE_INTERVAL);
        }
    };

    private void startPeriodicUpdates() {
        updateProfitReports(); // Initial update
        handler.postDelayed(updateRunnable, UPDATE_INTERVAL);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateRunnable);
    }

    private void updateProfitReports() {
        // Run database queries in background thread
        new Thread(() -> {
            MainDao.ProfitReport dailyProfit = database.mainDao().getDailyProfit();
            MainDao.ProfitReport monthlyProfit = database.mainDao().getMonthlyProfit();

            // Update UI on main thread
            requireActivity().runOnUiThread(() -> {
                updateDailyProfitDisplay(dailyProfit);
                updateMonthlyProfitDisplay(monthlyProfit);
            });
        }).start();
    }

    private void updateDailyProfitDisplay(MainDao.ProfitReport profit) {
        String dailyReport = String.format(Locale.getDefault(),
                "Daily Profit:\n" +
                        "Gross Profit: $%.2f\n" +
                        "Tax Amount: $%.2f\n" +
                        "Net Profit: $%.2f",
                profit.totalProfit,
                profit.totalTaxAmount,
                profit.netProfit);
        dailyReportTextView.setText(dailyReport);
    }

    private void updateMonthlyProfitDisplay(MainDao.ProfitReport profit) {
        String monthlyReport = String.format(Locale.getDefault(),
                "Monthly Profit:\n" +
                        "Gross Profit: $%.2f\n" +
                        "Tax Amount: $%.2f\n" +
                        "Net Profit: $%.2f",
                profit.totalProfit,
                profit.totalTaxAmount,
                profit.netProfit);
        monthlyReportTextView.setText(monthlyReport);
    }
}