package com.example.projet_7.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.projet_7.model.User;
import com.example.projet_7.model.matrix_api.Response;
import com.example.projet_7.model.matrix_api.RowsItem;
import com.example.projet_7.service.matrix_api.MatrixApiClient;
import com.example.projet_7.ui.DetailActivity;
import com.example.projet_7.ui.MainActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class Utils {

    public static final String  RESTAURANT_ID = "id_restaurant";

    public static void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void getDistanceBetweenLocationAndRestaurant(OnMatrixApiListReceivedCallback callback, Context context, StringBuilder currentLocationLatLng, StringBuilder destinationLatLng, String restaurantId) {
        Call<Response> call = MatrixApiClient.matrixApiService().getResult(currentLocationLatLng, destinationLatLng);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {

                    assert response.body() != null;
                    List<RowsItem> rowsItem = response.body().getRows();
                    callback.onMatrixApiListReceivedCallback(rowsItem, restaurantId);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response> call, @NonNull Throwable t) {
                Toast.makeText(context, "Failure distance request", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static StringBuilder getLatLngForMatrixApi(Location position) {
        return new StringBuilder().append(position.getLatitude()).append(",").append(position.getLongitude());

    }

    public static StringBuilder getLatLngForMatrixApi(LatLng position) {
        return new StringBuilder().append(position.latitude).append(",").append(position.longitude);
    }

    public static int getIndexDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static double convertRatingToPurcentage(double rating_restaurant) {
        return rating_restaurant * 100 / 5;
    }

    public static float convertPurcentageToRating(double purcentage) {
        return (float) (purcentage * 3 / 100);
    }

    public static void getPictureCroppedWithGlide(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    public static void getBitmapWithGlide(Context context, Bitmap bitmap, ImageView imageView) {

        Glide.with(context)
                .asBitmap()
                .load(bitmap)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    public static StringBuilder concatFirstnameAndSentence(User workmate, String sentence) {
        return new StringBuilder().append(workmate.getUsername().split(" ")[0]).append(" ").append(sentence);
    }

    public static void startDetailActivity(Context context, String id) {
        Intent intent = new Intent(context, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(RESTAURANT_ID, id);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static boolean isSearchBoxLengthAtleast3(Context context) {
        return ((MainActivity)context).querySearchView.length() >= 3;
    }

    public static boolean isSearchBoxLengthAtleast1(Context context) {
        return ((MainActivity)context).querySearchView.length() >= 1;
    }

    public static boolean isQuerySearchEmpty(Context context) {
        return ((MainActivity)context).querySearchView.isEmpty();
    }

    public static HashSet<String> getIdsOfRestaurantsWithoutDuplicate(ArrayList<User> workmates){

        List<String> uidRestaurantsBooked = new ArrayList<>();
        for (User workmate : workmates) {
            uidRestaurantsBooked.add(workmate.getRestaurantBookedId());
        }

        return new HashSet<>(uidRestaurantsBooked);
    }
}
