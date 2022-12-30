package com.openclassrooms.realestatemanager.fragments;

import static com.openclassrooms.realestatemanager.utils.Utils.castDoubleToInt;
import static com.openclassrooms.realestatemanager.utils.Utils.concatStr;
import static com.openclassrooms.realestatemanager.utils.Utils.convertDollarToEuro;
import static com.openclassrooms.realestatemanager.utils.Utils.convertEuroToDollar;
import static com.openclassrooms.realestatemanager.utils.Utils.convertToString;
import static com.openclassrooms.realestatemanager.utils.Utils.setProfilePicture;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.database.enumeration.Currency;
import com.openclassrooms.realestatemanager.database.model.Image;
import com.openclassrooms.realestatemanager.database.model.RealEstate;
import com.openclassrooms.realestatemanager.databinding.FragmentRealestatesDetailBinding;
import com.openclassrooms.realestatemanager.viewModel.UserViewModel;

import java.util.List;
import java.util.Objects;

public class RealEstateDetailFragment extends Fragment {

    public FragmentRealestatesDetailBinding binding;
    public RealEstate realEstate;
    public List<Image> images;
    public UserViewModel userViewModel;

    public static RealEstateDetailFragment newInstance() {

        RealEstateDetailFragment realEstateDetailFragment = new RealEstateDetailFragment();
        realEstateDetailFragment.realEstate = null;
        realEstateDetailFragment.images = null;

        return realEstateDetailFragment;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRealestatesDetailBinding.inflate(inflater, container, false);
        initViewModel();
        return binding.getRoot();
    }

    private void initViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.realEstate != null) {

            this.setRealEstate(realEstate, images);

        } else {

            binding.fragmentRealEstatesDetail.setVisibility(View.INVISIBLE);
        }
    }

    public void setRealEstate(RealEstate realEstate, List<Image> images) {
        this.realEstate = realEstate;
        this.images = images;
        this.setupPriceListener();

        binding.fragmentRealEstatesDetail.setVisibility(View.VISIBLE);

        this.setPictureUser();

        this.setPrice();

        if (realEstate.getSold()) {
            binding.sold.setText(getResources().getString(R.string.sold));
            binding.itemDateSold.setVisibility(View.VISIBLE);
            binding.dateRealEstateSold.setText(realEstate.getDateOfSell());
        } else {
            binding.sold.setText(getResources().getString(R.string.available));
            binding.itemDateSold.setVisibility(View.GONE);
        }


        binding.dateRealEstateAvailable.setText(realEstate.getDateOfEntry());
        binding.type.setText(String.valueOf(realEstate.getTypeRealEstate()));

        binding.description.setText(realEstate.getDescription());

        binding.surface.setText(convertToString(realEstate.getSurface()));

        binding.bathrooms.setText(convertToString(realEstate.getNbBathRoom()));

        binding.bedrooms.setText(convertToString(realEstate.getNbBedRoom()));

        binding.rooms.setText(convertToString(realEstate.getNbRoom()));

        binding.location.setText(realEstate.getAddress());

        binding.interestPoints.setText(realEstate.getInterestPoint());
    }

    private void setupPriceListener() {
        binding.price.setOnClickListener(v -> {

            assert realEstate.getCurrency() != null;
            boolean isCurrencyDollar = realEstate.getCurrency().equals(Currency.dollar);

            if (isCurrencyDollar) {

                realEstate.setCurrency(Currency.euro);
                double euro = convertDollarToEuro(realEstate.getPrice());


                String priceStr = convertToString(castDoubleToInt(euro));
                String priceEuro = concatStr(priceStr, getResources().getString(R.string.euro_symbole));
                binding.price.setText(priceEuro);

            } else {

                realEstate.setCurrency(Currency.dollar);
                String[] dollarStr = binding.price.getText().toString().split(" ");

                double dollarDouble = Double.parseDouble(dollarStr[0]);
                double dollar = convertEuroToDollar(dollarDouble);

                String priceStr = convertToString(castDoubleToInt(dollar));
                String priceDollar = concatStr(priceStr, getResources().getString(R.string.dollar_symbole));
                binding.price.setText(priceDollar);

            }
        });
    }

    private void setPictureUser() {
        Long userById = realEstate.getAgentId();
        if (userById != null) {
            userViewModel.getUserById(userById).observe(getViewLifecycleOwner(), user -> {
                String name = concatStr(user.getFirstname(), user.getLastname());
                binding.realEstateAgentName.setText(name);
                if (user.getPicture() != null) {
                    setProfilePicture(getContext(), user.getPicture(), binding.userImg);
                }
            });
        }else{
            setProfilePicture(getContext(), getImage("ic_anon_user_48dp"), binding.userImg);
            binding.realEstateAgentName.setText(getResources().getString(R.string.not_attributed));
        }
    }

    public int getImage(String imageName) {

        return this.getResources().getIdentifier(imageName, "drawable", requireActivity().getPackageName());
    }

    private void setPrice() {
        String priceStr = convertToString(realEstate.getPrice().intValue());
        assert realEstate.getCurrency() != null;
        String price;
        if (realEstate.getCurrency().equals(Currency.dollar)) {
            price = concatStr(priceStr, getResources().getString(R.string.dollar_symbole));
        } else {
            price = concatStr(priceStr, getResources().getString(R.string.euro_symbole));
        }
        binding.price.setText(price);
    }

//    private void setProfilePicture(String url, ImageView picture) {
//        Glide.with(this)
//                .load(url)
//                .diskCacheStrategy(DiskCacheStrategy.DATA)
//                .apply(RequestOptions.circleCropTransform())
//                .into(picture);
//    }


}
