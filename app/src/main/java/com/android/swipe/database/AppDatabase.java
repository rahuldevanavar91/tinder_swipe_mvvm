package com.android.swipe.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.android.swipe.model.User;


@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "app_data_base";
    private static AppDatabase appDatabase;

    public abstract UserResponseDao responseDao();


    public static synchronized AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration().build();
        }
        return appDatabase;
    }
}
