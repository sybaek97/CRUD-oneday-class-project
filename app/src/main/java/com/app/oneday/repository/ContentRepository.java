package com.app.oneday.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.oneday.common.ImageUtils;
import com.app.oneday.model.ShopInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ContentRepository extends ViewModel {

    private FirebaseFirestore firestore;
    private MutableLiveData<Boolean> statusLiveData;
    private MutableLiveData<List<ShopInfo>> userShopsLiveData;
    private FirebaseStorage storage;


    public ContentRepository() {
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        statusLiveData = new MutableLiveData<>();
        userShopsLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getStatusLiveData() {
        return statusLiveData;
    }
    public MutableLiveData<List<ShopInfo>> getUserShopsLiveData() {
        return userShopsLiveData;
    }


    public void addContent(Context context,ShopInfo shopInfo) {
        if (shopInfo.getUri() != null) {
            byte[] compressedImage = ImageUtils.compressImage(Uri.parse(shopInfo.getUri()), context);

            if (compressedImage != null) {
                StorageReference imageRef = storage.getReference()
                        .child("images/" + UUID.randomUUID().toString() + ".jpg");

                imageRef.putBytes(compressedImage)
                        .addOnSuccessListener(taskSnapshot ->
                                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    shopInfo.setUri(uri.toString());
                                    saveToContent(shopInfo);
                                })
                        )
                        .addOnFailureListener(e ->
                                Log.e("AddContentRepository", "Image upload failed", e)
                        );
            } else {
                Log.e("AddContentRepository", "Image compression failed");
            }
        }
    }
    public void getUserShops(String uid) {
        Log.e("AddContentRepository", uid);

        firestore.collection("shops")
                .whereEqualTo("id", uid) // "id" 필드가 uid와 일치하는 데이터만 필터링
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ShopInfo> shopList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            ShopInfo shop = document.toObject(ShopInfo.class); // ShopInfo 객체로 변환
                            shopList.add(shop);
                        }
                        userShopsLiveData.postValue(shopList); // 데이터를 LiveData로 설정
                    } else {
                        Log.e("AddContentRepository", "Error fetching shops", task.getException());
                    }
                });

    }

    private void saveToContent(ShopInfo shopInfo){
        Map<String, Object> shopData = new HashMap<>();
        shopData.put("id", shopInfo.getId());
        shopData.put("classStatus", shopInfo.getClassStatus());
        shopData.put("uri", shopInfo.getUri() != null ? shopInfo.getUri().toString() : null); // Uri를 String으로 변환
        shopData.put("phoneNumber", shopInfo.getPhoneNumber());
        shopData.put("onedayType", shopInfo.getOnedayType());
        shopData.put("homepageAddress", shopInfo.getHomepageAddress());
        shopData.put("shopName", shopInfo.getShopName());


        firestore.collection("shops")
                .add(shopData)
                .addOnSuccessListener(documentReference ->{
                        statusLiveData.postValue(true);

                        Log.d("AddContentRepository", "ShopInfo 저장 성공: " + documentReference.getId());
                    }
                )
                .addOnFailureListener(e ->
                        Log.e("AddContentRepository", "ShopInfo 저장 실패", e)
                );
    }
}