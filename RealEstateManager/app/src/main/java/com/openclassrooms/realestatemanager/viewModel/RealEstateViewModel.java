package com.openclassrooms.realestatemanager.viewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.database.model.Image;
import com.openclassrooms.realestatemanager.database.model.RealEstate;
import com.openclassrooms.realestatemanager.repository.RealEstateRepository;

import java.util.List;

public class RealEstateViewModel extends AndroidViewModel {

    private final RealEstateRepository repository;


    public RealEstateViewModel(Application application) {
        super(application);
        repository = new RealEstateRepository(application);
    }


    public long insert(RealEstate realEstate) {
        return repository.insert(realEstate);
    }

    public int update(RealEstate realEstate) {
        return repository.update(realEstate);
    }

    public LiveData<RealEstate> getRealEstateById(long id) {
        return repository.getRealEstateById(id);
    }

    public LiveData<List<RealEstate>> getAllRealEstates() {
        return repository.getAllRealEstates();
    }

    public LiveData<List<RealEstate>> getRealEstateByUserId(long id) {
        return repository.getRealEstateByUserId(id);
    }

    public LiveData<List<Image>> getRealEstatesImages(RealEstate realEstate) {
        return repository.getRealEstatesImages(realEstate);
    }
}