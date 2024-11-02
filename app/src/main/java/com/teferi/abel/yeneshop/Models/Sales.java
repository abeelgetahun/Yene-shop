package com.teferi.abel.yeneshop.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Sales implements Serializable {
    @PrimaryKey(autoGenerate = true)
    int id = 0;

    @ColumnInfo(name = "item_id")
    int item_id;

    @ColumnInfo(name = "name")
    String name;

    @ColumnInfo(name = "category")
    String category;

    @ColumnInfo(name = "quantity")
    int quantity;

    @ColumnInfo(name = "purchasing_price")
    Double purchasing_price;

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

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
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

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getPurchasing_price() {
        return purchasing_price;
    }

    public void setPurchasing_price(Double purchasing_price) {
        this.purchasing_price = purchasing_price;
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
