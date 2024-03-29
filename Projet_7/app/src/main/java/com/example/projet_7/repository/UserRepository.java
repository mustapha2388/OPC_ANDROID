package com.example.projet_7.repository;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projet_7.model.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public final class UserRepository {

    private static final String COLLECTION_NAME = "users";

    private final MutableLiveData<ArrayList<User>> muLiveDataOtherUsers;
    private final MutableLiveData<ArrayList<User>> muLiveDataUsersWhoHasBooked;
    private final MutableLiveData<String> muLiveDataUsersByQuery;

    private static volatile UserRepository instance;


    public UserRepository() {

        this.muLiveDataOtherUsers = new MutableLiveData<>();
        this.muLiveDataUsersWhoHasBooked = new MutableLiveData<>();
        this.muLiveDataUsersByQuery = new MutableLiveData<>();
    }

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return AuthUI.getInstance().delete(context);
    }

    ////FIRESTORE

    // Get User Data from Firestore
    public Task<DocumentSnapshot> getUserData() {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            return this.getUsersCollection().document(uid).get();
        } else {
            return null;
        }
    }

    // Delete the User from Firestore
    public void deleteUserFromFirestore() {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            this.getUsersCollection().document(uid).delete();
        }
    }

    private String getCurrentUserUID() {
        FirebaseUser user = getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    // Get the Collection Reference
    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Create User in Firestore
    public void createUser() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String uid = user.getUid();
            String restaurantBookedId = "";

            User userToCreate = new User(uid, username, urlPicture, restaurantBookedId);

            Task<DocumentSnapshot> userData = getUserData();
            // If the user already exist in Firestore, we get his data (isMentor)
            assert userData != null;
            userData.addOnSuccessListener(documentSnapshot -> this.getUsersCollection().document(uid).set(userToCreate));
        }
    }

    public Task<DocumentSnapshot> isUserExists() {
        String uid = this.getCurrentUserUID();
        assert uid != null;
        return this.getUsersCollection().document(uid).get();
    }

    public LiveData<ArrayList<User>> getLiveDataOtherUsers() {
        return muLiveDataOtherUsers;
    }

    public void getUsersData() {

        ArrayList<User> users = new ArrayList<>();

        this.getUsersCollection().whereNotEqualTo("uid", getCurrentUserUID()).get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot user : task.getResult()) {
                users.add(user.toObject(User.class));
            }
            muLiveDataOtherUsers.postValue(users);
        });
    }

    public void updateUser(User user) {
        FirebaseUser currentUser = getCurrentUser();
        if (user != null) {
            Task<DocumentSnapshot> userData = getUserData();
            assert currentUser != null;
            String uid = currentUser.getUid();
            assert userData != null;
            userData.addOnSuccessListener(documentSnapshot -> this.getUsersCollection().document(uid).update("restaurantBookedId", user.getRestaurantBookedId()));
        }
    }

    public void getWorkmatesBookedRestaurant(String restaurantId) {
        ArrayList<User> users = new ArrayList<>();

        this.getUsersCollection()
                .whereNotEqualTo("uid", getCurrentUserUID())
                .whereEqualTo("restaurantBookedId", restaurantId).get().addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot user : task.getResult()) {
                        users.add(user.toObject(User.class));
                    }
                    muLiveDataUsersWhoHasBooked.setValue(users);
                });
    }

    public LiveData<ArrayList<User>> getLiveDataUsersWhoHasBooked() {
        return muLiveDataUsersWhoHasBooked;
    }

    public void getWorkmatesByQuery(String query) {
        muLiveDataUsersByQuery.setValue(query);
    }

    public LiveData<String> getMutableLiveDataWorkmateByQuery() {
        return muLiveDataUsersByQuery;
    }
}