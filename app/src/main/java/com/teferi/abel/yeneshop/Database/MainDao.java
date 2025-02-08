package com.teferi.abel.yeneshop.Database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.teferi.abel.yeneshop.Models.Items;
import com.teferi.abel.yeneshop.Models.Sales;

import java.util.List;
@Dao
public interface MainDao {
    @Insert(onConflict = REPLACE)
    void insert(Items items);

    @Query("SELECT * FROM items ORDER BY id DESC")
    List<Items> getAll();

    @Update
    void update(Items item); // Add this method

    @Query("UPDATE items SET name=:name, Category=:Category,quantity=:quantity,purchasing_price=:purchasing_price,selling_price=:selling_price,tax=:tax WHERE id=:id")
    // #qty change
    void updateItem(int id,String name,String Category,double quantity,double purchasing_price,double selling_price,int tax);

    @Delete
    void delete(Items items);

    @Query("SELECT * FROM items WHERE id = :id")
    Items getItemById(int id);

    public static class OverallTotals {
        public double totalSellingPrice;
        public double totalPurchasePrice;
        public double totalQuantity;
    }

    @Query("SELECT " +
            "SUM(CAST(selling_price AS DECIMAL) * CAST(quantity AS DECIMAL)) AS totalSellingPrice, " +
            "SUM(CAST(purchasing_price AS DECIMAL) * CAST(quantity AS DECIMAL)) AS totalPurchasePrice, " +
            "SUM(CAST(quantity AS INTEGER)) AS totalQuantity " +
            "FROM items")
    OverallTotals getOverallTotals();

    @Query("SELECT DISTINCT category FROM items ORDER BY category ASC")
    List<String> getUniqueCategories();

    // static class instead of interface for CategoryTotals
    public static class CategoryTotals {
        public String category;
        public double totalSellingPrice;
        public double totalPurchasePrice;
        // #qty change2
        public double totalQuantity;

    }

    @Query("SELECT " +
            "category, " +
            "SUM(CAST(quantity AS DECIMAL)) AS totalQuantity, " +
            "SUM(CAST(purchasing_price AS DECIMAL) * CAST(quantity AS DECIMAL)) AS totalPurchasePrice, " +
            "SUM(CAST(selling_price AS DECIMAL) * CAST(quantity AS DECIMAL)) AS totalSellingPrice " +
            "FROM items " +
            "GROUP BY category " +
            "ORDER BY category COLLATE NOCASE ASC")
    List<CategoryTotals> getAllCategoryTotals();


    public static class ItemDetails {
        public int id;
        public String name;
        // #qty change3
        public double quantity;
        public String category;
        public double purchasing_price;    // single item PP
        public double selling_price;       // single item SP
        public double expected_profit;
        public String date;
    }

        @Query("SELECT id, name, quantity,category, purchasing_price, selling_price, " +
                "(CAST(selling_price AS DECIMAL) - CAST(purchasing_price AS DECIMAL)) AS expected_profit, date " +
                "FROM items WHERE category = :category ORDER BY name COLLATE NOCASE ASC")
        List<ItemDetails> getItemsByCategory(String category);

    @Query("SELECT category FROM items WHERE category = :category LIMIT 1")
    String getCategoryById(String category);




    @Insert(onConflict = REPLACE)
    void insertSale(Sales sale);

    @Query("UPDATE items SET quantity = quantity - :soldQuantity WHERE id = :itemId")
    void updateItemQuantityAfterSale(int itemId, double soldQuantity);

    @Transaction
    default void processSale(Sales sale, int itemId, double soldQuantity) {
        insertSale(sale);
        updateItemQuantityAfterSale(itemId, soldQuantity);
    }


    //////////

    public static class ProfitReport {
        public double totalProfit;
        public double totalTaxAmount;
        public double netProfit;
    }

    @Query("SELECT " +
            "COALESCE(SUM((selling_price - purchasing_price) * quantity), 0) as totalProfit, " +
            "COALESCE(SUM((selling_price * quantity * tax) / 100), 0) as totalTaxAmount, " +
            "COALESCE(SUM((selling_price - purchasing_price) * quantity - (selling_price * quantity * tax / 100)), 0) as netProfit " +
            "FROM sales " +
            "WHERE date BETWEEN :startDate AND :endDate")
    ProfitReport getDailyProfit(String startDate, String endDate);

    @Query("SELECT " +
            "COALESCE(SUM((selling_price - purchasing_price) * quantity), 0) as totalProfit, " +
            "COALESCE(SUM((selling_price * quantity * tax) / 100), 0) as totalTaxAmount, " +
            "COALESCE(SUM((selling_price - purchasing_price) * quantity - (selling_price * quantity * tax / 100)), 0) as netProfit " +
            "FROM sales " +
            "WHERE date BETWEEN :startDate AND :endDate")
    ProfitReport getMonthlyProfit(String startDate, String endDate);

    @Query("SELECT * FROM sales ORDER BY date DESC")
    List<Sales> getAllSales();


    // export settings

    @Query("SELECT * FROM Items WHERE quantity >= :minQty")
    List<Items> getItemsWithQuantityGreaterThan(double minQty);

    @Query("SELECT * FROM Items WHERE quantity = 0")
    List<Items> getSoldOutItems(); // ✅ Fix: No parameter

    // sales export


    //
    @Query("SELECT * FROM Sales WHERE date >= datetime('now', '-1 day')")
    List<Sales> getDailySales();

    @Query("SELECT * FROM Sales WHERE date >= datetime('now', '-30 day')")
    List<Sales> getMonthlySales();


    @Query("SELECT * FROM Sales WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    List<Sales> getSalesByDateRange(String startDate, String endDate);






}