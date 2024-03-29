package com.openclassrooms.realestatemanager.database.dao;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.openclassrooms.realestatemanager.database.model.User;

import java.util.List;

@Dao
public interface UserDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(User user);

    @Query("DELETE  FROM user_table WHERE id = :id")
    int delete(long id);

    @Update
    int update(User user);

    @Query("SELECT email FROM user_table WHERE email = :email")
    String getEmail(String email);

    @Query("SELECT email FROM user_table WHERE email = :email AND id !=:id ")
    String getEmail(long id, String email);

    @Query("SELECT * FROM user_table WHERE id = :id")
    LiveData<User> getUserById(long id);

    @Query("SELECT * FROM user_table WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT password FROM user_table WHERE email = :email")
    String getPassword(String email);

    @Query("SELECT * FROM user_table WHERE id = :id")
    Cursor getUserWithCursor(long id);

    @Query("SELECT * FROM user_table")
    LiveData<List<User>> getUsersForPrepopulateDB();

}
