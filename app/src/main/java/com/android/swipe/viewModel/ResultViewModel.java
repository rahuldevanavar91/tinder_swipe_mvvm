package com.android.swipe.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.swipe.model.User;
import com.android.swipe.network.Resource;
import com.android.swipe.repository.UserResultRepository;

import java.util.List;

public class ResultViewModel extends AndroidViewModel {

    private UserResultRepository mRepository;
    private LiveData<Resource<List<User>>> mResultLiveData;

    public ResultViewModel(@NonNull Application application) {
        super(application);
        mResultLiveData = new MutableLiveData<>();
        mRepository = new UserResultRepository(application.getApplicationContext());
    }

    public LiveData<Resource<List<User>>> getResultLiveData() {
        return mResultLiveData;
    }

    public void getResult() {
        mResultLiveData = mRepository.getResult();
    }

    public void addTodb(int position, boolean save) {
        mRepository.addToDb(position, save);
    }

    @Override
    protected void onCleared() {
        mRepository.clear();
    }
}
