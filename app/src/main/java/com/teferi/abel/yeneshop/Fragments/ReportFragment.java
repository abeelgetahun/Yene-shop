package com.teferi.abel.yeneshop.Fragments;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.teferi.abel.yeneshop.Adapter.SoldItemsAdapter;
import com.teferi.abel.yeneshop.Database.MainDao;
import com.teferi.abel.yeneshop.Database.RoomDB;
import com.teferi.abel.yeneshop.Models.Items;
import com.teferi.abel.yeneshop.Models.Sales;
import com.teferi.abel.yeneshop.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ReportFragment extends Fragment {
    private TextView dailyReportTextView, monthlyReportTextView;
    private RoomDB database;
    private MainDao mainDao;
    private Handler mainHandler;
    private boolean isFragmentVisible = false;
    private RecyclerView soldItemsRecyclerView;
    private SoldItemsAdapter soldItemsAdapter;
    private Button dailyButton, monthlyButton;
    private boolean isDailySelected = true;
    private ProgressBar loadingIndicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        initializeViews(view);
        initializeDatabase();
        setupRecyclerView();
        setupButtonListeners();
        setDefaultButtonState();
        updateProfitReports();
        updateSoldItemsReport(isDailySelected);
        return view;
    }

    private void initializeViews(View view) {
        dailyReportTextView = view.findViewById(R.id.daily_report);
        monthlyReportTextView = view.findViewById(R.id.monthly_report);
        soldItemsRecyclerView = view.findViewById(R.id.sold_items_recycler_view);
        dailyButton = view.findViewById(R.id.report_page_daily);
        monthlyButton = view.findViewById(R.id.report_page_monthly);
        mainHandler = new Handler(Looper.getMainLooper());
        loadingIndicator = view.findViewById(R.id.loading_progress);
    }

    private void initializeDatabase() {
        database = RoomDB.getInstance(requireContext());
        mainDao = database.mainDao();
    }

    private void setupRecyclerView() {
        soldItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        soldItemsAdapter = new SoldItemsAdapter();
        soldItemsRecyclerView.setAdapter(soldItemsAdapter);
        soldItemsAdapter.setOnItemClickListener(sale -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Reverse Sale")
                    .setMessage("Are you sure you want to reverse this sale?")
                    .setPositiveButton("Yes", (dialog, which) -> reverseSale(sale))
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void setupButtonListeners() {
        dailyButton.setOnClickListener(v -> {
            isDailySelected = true;
            setButtonSelected(dailyButton, true);
            setButtonSelected(monthlyButton, false);
            updateSoldItemsReport(true);
        });

        monthlyButton.setOnClickListener(v -> {
            isDailySelected = false;
            setButtonSelected(monthlyButton, true);
            setButtonSelected(dailyButton, false);
            updateSoldItemsReport(false);
        });
    }

    private void setDefaultButtonState() {
        isDailySelected = true;
        setButtonSelected(dailyButton, true);
        setButtonSelected(monthlyButton, false);
    }

    private void setButtonSelected(Button button, boolean isSelected) {
        if (isSelected) {
            button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
            button.setTextColor(getResources().getColor(R.color.white));
        } else {
            button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            button.setTextColor(getResources().getColor(R.color.black));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isFragmentVisible = true;
        updateProfitReports();
        updateSoldItemsReport(isDailySelected);
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentVisible = false;
    }

    private void updateProfitReports() {
        showLoadingIndicator();

        CompletableFuture.supplyAsync(() -> {
            try {
                List<Sales> allSales = mainDao.getAllSales();
                Calendar now = Calendar.getInstance();
                Date currentDate = now.getTime();
                Calendar yesterday = Calendar.getInstance();
                yesterday.add(Calendar.HOUR_OF_DAY, -24);
                Date yesterdayDate = yesterday.getTime();
                Calendar thirtyDaysAgo = Calendar.getInstance();
                thirtyDaysAgo.add(Calendar.DAY_OF_MONTH, -30);
                Date thirtyDaysAgoDate = thirtyDaysAgo.getTime();
                ProfitReport dailyProfit = calculateProfitForDateRange(allSales, yesterdayDate, currentDate);
                ProfitReport monthlyProfit = calculateProfitForDateRange(allSales, thirtyDaysAgoDate, currentDate);
                return new ProfitReports(dailyProfit, monthlyProfit);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).thenAcceptAsync(reports -> {
            hideLoadingIndicator();
            if (isFragmentVisible && reports != null) {
                updateUIWithReports(reports);
            }
        }, runnable -> mainHandler.post(runnable));
    }

    private void updateSoldItemsReport(boolean isDaily) {
        showLoadingIndicator();

        CompletableFuture.supplyAsync(() -> {
            try {
                List<Sales> allSales = mainDao.getAllSales();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy hh:mm a", Locale.ENGLISH);
                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);
                if (isDaily) {
                    cal.add(Calendar.HOUR_OF_DAY, -24);
                } else {
                    cal.add(Calendar.DAY_OF_MONTH, -30);
                }
                Date fromDate = cal.getTime();
                List<Sales> filteredSales = new ArrayList<>();
                for (Sales sale : allSales) {
                    try {
                        Date saleDate = sdf.parse(sale.getDate());
                        if (saleDate != null && saleDate.after(fromDate) && saleDate.before(now)) {
                            filteredSales.add(sale);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                Collections.sort(filteredSales, (s1, s2) -> {
                    try {
                        Date d1 = sdf.parse(s1.getDate());
                        Date d2 = sdf.parse(s2.getDate());
                        return d2.compareTo(d1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                });
                return filteredSales;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).thenAcceptAsync(sales -> {
            hideLoadingIndicator();
            if (isFragmentVisible && sales != null) {
                mainHandler.post(() -> soldItemsAdapter.setSoldItems(sales));
            }
        }, runnable -> mainHandler.post(runnable));
    }

    private ProfitReport calculateProfitForDateRange(List<Sales> sales, Date startDate, Date endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy hh:mm a", Locale.ENGLISH);
        double totalSales = 0;
        double totalProfit = 0;
        double totalTaxAmount = 0;
        double netProfit = 0;

        for (Sales sale : sales) {
            try {
                Date saleDate = sdf.parse(sale.getDate());
                if (saleDate != null && !saleDate.before(startDate) && !saleDate.after(endDate)) {
                    double saleAmount = sale.getSelling_price() * sale.getQuantity();
                    totalSales += saleAmount;
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
        return new ProfitReport(totalSales, totalProfit, totalTaxAmount, netProfit);
    }

    private void updateUIWithReports(ProfitReports reports) {
        String dailyReport = formatProfitReport(reports.dailyProfit);
        String monthlyReport = formatProfitReport(reports.monthlyProfit);
        mainHandler.post(() -> {
            if (isFragmentVisible) {
                dailyReportTextView.setText(dailyReport);
                monthlyReportTextView.setText(monthlyReport);
            }
        });
    }

    private String formatProfitReport(ProfitReport profit) {
        return String.format(Locale.getDefault(),
                "Total Sales: %.2f\nGross Profit: %.2f\nTax Amount: %.2f\nNet Profit: %.2f",
                profit.totalSales, profit.totalProfit, profit.totalTaxAmount, profit.netProfit);
    }

    private void reverseSale(Sales sale) {
        CompletableFuture.runAsync(() -> {
            Items item = mainDao.getItemById(sale.getItem_id());
            if (item != null) {
                double newQuantity = item.getQuantity() + sale.getQuantity();
                item.setQuantity(newQuantity);
                mainDao.update(item);
            } else {
                Items newItem = new Items();
                newItem.setName(sale.getName());
                newItem.setCategory(sale.getCategory());
                newItem.setQuantity(sale.getQuantity());
                newItem.setPurchasing_price(sale.getPurchasing_price());
                newItem.setSelling_price(sale.getSelling_price());
                newItem.setTax(sale.getTax());
                newItem.setDate(sale.getDate());
                mainDao.insert(newItem);
            }
            mainDao.deleteSale(sale);
        }).thenRunAsync(() -> {
            mainHandler.post(() -> {
                Toast.makeText(getContext(), "Sale reversed successfully", Toast.LENGTH_SHORT).show();
                updateSoldItemsReport(isDailySelected);
            });
        }, runnable -> mainHandler.post(runnable));
    }

    private static class ProfitReport {
        final double totalSales;
        final double totalProfit;
        final double totalTaxAmount;
        final double netProfit;

        ProfitReport(double totalSales, double totalProfit, double totalTaxAmount, double netProfit) {
            this.totalSales = totalSales;
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

    private void showLoadingIndicator() {
        mainHandler.post(() -> loadingIndicator.setVisibility(View.VISIBLE));
    }

    private void hideLoadingIndicator() {
        mainHandler.post(() -> loadingIndicator.setVisibility(View.GONE));
    }
}