package com.teferi.abel.yeneshop.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
@Entity(tableName = "items")
public class Items implements Serializable {
    @PrimaryKey(autoGenerate = true)
    int id = 0;

    @ColumnInfo(name = "name")
    String name;

    @ColumnInfo(name = "category")
    String category;

    @ColumnInfo(name = "quantity")
    double quantity;

    @ColumnInfo(name = "purchasing_price")
    Double Purchasing_price;

    @ColumnInfo(name = "selling_price")
    Double selling_price;

    @ColumnInfo(name = "tax")
    int tax;

    @ColumnInfo(name = "date")
    String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    // This was the problematic method
    public void setCategory(String category) {
        this.category = category;  // Added 'this' keyword
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Double getPurchasing_price() {
        return Purchasing_price;
    }

    public void setPurchasing_price(Double purchasing_price) {
        this.Purchasing_price = purchasing_price;
    }

    public Double getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(Double selling_price) {
        this.selling_price = selling_price;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}