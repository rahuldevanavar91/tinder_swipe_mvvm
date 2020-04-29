package com.android.swipe.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.android.swipe.model.User;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserResponseDao {

    @Query("Select * from user")
    LiveData<List<User>> getAllResponse();


    @Insert(onConflict = REPLACE)
    long insertFavorite(User result);

    @Query("delete from user where `key`=:id")
    void deleteFavorite(int id);
}

