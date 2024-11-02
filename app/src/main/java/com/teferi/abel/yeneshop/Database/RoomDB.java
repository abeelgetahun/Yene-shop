package com.teferi.abel.yeneshop.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.teferi.abel.yeneshop.Models.Items;
import com.teferi.abel.yeneshop.Models.Sales;

@Database(entities = {Items.class, Sales.class}, version = 1, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    private static RoomDB database;
    private static final String DATABASE_NAME = "ShopApp";

    public synchronized static RoomDB getInstance(Context context){
        if (database==null){
            database= Room.databaseBuilder(context.getApplicationContext(),RoomDB.class,DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();}
        return database;
    }
    public abstract MainDao mainDao();
}
