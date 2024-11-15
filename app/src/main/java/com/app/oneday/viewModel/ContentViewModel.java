package com.app.oneday.viewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.oneday.model.ShopInfo;
import com.app.oneday.repository.ContentRepository;

import java.util.List;

public class ContentViewModel extends ViewModel {

    private ContentRepository contentRepository;
    private LiveData<Boolean> saveStatusLiveData;
    private LiveData<List<ShopInfo>> userShopsLiveData;

    public ContentViewModel() {
        contentRepository = new ContentRepository();
        saveStatusLiveData = contentRepository.getStatusLiveData();
        userShopsLiveData = contentRepository.getUserShopsLiveData();

    }

    public LiveData<Boolean> getSaveStatus() {
        return saveStatusLiveData;
    }
    public LiveData<List<ShopInfo>> getUserShopsLiveData() {
        return userShopsLiveData;
    }
    public  void addContent(Context context, ShopInfo shopInfo){
        if(shopInfo.getUri() !=null){
            contentRepository.addContent(context, shopInfo);

        }
    }
    public void getUserShops(String uid) {
       contentRepository.getUserShops(uid);
    }

}
