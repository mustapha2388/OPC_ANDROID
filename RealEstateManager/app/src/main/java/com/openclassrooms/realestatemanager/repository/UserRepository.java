package com.openclassrooms.realestatemanager.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.database.AppDatabase;
import com.openclassrooms.realestatemanager.database.dao.UserDao;
import com.openclassrooms.realestatemanager.database.model.User;

import java.util.List;

public class UserRepository {

    private final UserDao userDao;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.userDao = db.userDao();
    }

    public long insert(User user) {
        return userDao.insert(user);
    }

    public boolean isUserEmailExistAlready(String email) {
        return userDao.getEmail(email) != null;
    }

    public boolean isUserEmailExistAlready(User user) {
        return userDao.getEmail(user.getId(), user.getEmail()) != null;
    }

    public boolean checkIfPasswordIsCorrect(User user) {
        String passwordFromDb = userDao.getPassword(user.getEmail());
        if (passwordFromDb == null) {
            return false;
        } else {

            return passwordFromDb.equals(user.getPassword());
        }
    }

    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public LiveData<User> getUserById(long id) {
        return userDao.getUserById(id);
    }

    public LiveData<List<User>> getUsersForPrepopulateDB() {
        return userDao.getUsersForPrepopulateDB();
    }

    public int update(User user){
        return userDao.update(user);
    }
}
