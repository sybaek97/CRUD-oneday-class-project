package com.app.oneday.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.oneday.common.ImageUtils;
import com.app.oneday.model.ShopInfo;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
    private MutableLiveData<Boolean> statusDeleteLiveData;
    private MutableLiveData<List<ShopInfo>> userShopsLiveData;
    private FirebaseStorage storage;


    public ContentRepository() {
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        statusDeleteLiveData = new MutableLiveData<>();
        statusLiveData = new MutableLiveData<>();
        userShopsLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getStatusLiveData() {
        return statusLiveData;
    }
    public MutableLiveData<Boolean> getStatusDeleteLiveData() {
        return statusDeleteLiveData;
    }
    public MutableLiveData<List<ShopInfo>> getUserShopsLiveData() {
        return userShopsLiveData;
    }


    public void addContent(Context context, ShopInfo shopInfo) {
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
        } else {
            saveToContent(shopInfo);
        }
    }

    public void editContent(Context context, ShopInfo shopInfo) {
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
        } else {
            saveToContent(shopInfo);
        }
    }

    public void getUserShops(String uid) {
        Log.e("AddContentRepository", uid);

        firestore.collection("shops")
                .whereEqualTo("id", uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ShopInfo> shopList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            ShopInfo shop = document.toObject(ShopInfo.class);
                            shop.setDocumentId(document.getId());

                            shopList.add(shop);
                        }
                        userShopsLiveData.postValue(shopList); // 데이터를 LiveData로 설정
                    } else {
                        Log.e("AddContentRepository", "Error fetching shops", task.getException());
                    }
                });

    }
    public void getAllShops() {

        firestore.collection("shops")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ShopInfo> shopList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            ShopInfo shop = document.toObject(ShopInfo.class); // ShopInfo 객체로 변환
                            shop.setDocumentId(document.getId());
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
        shopData.put("createdAt", System.currentTimeMillis());

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
    public void deleteContent(String documentId) {

        firestore.collection("shops")
                .document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imageUri = documentSnapshot.getString("uri");

                        if (imageUri != null && !imageUri.isEmpty()) {
                            FirebaseStorage.getInstance()
                                    .getReferenceFromUrl(imageUri)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("ContentRepository", "Storage 이미지 삭제 성공");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("ContentRepository", "Storage 이미지 삭제 실패", e);
                                    });
                        }

                        firestore.collection("shops")
                                .document(documentId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    statusDeleteLiveData.postValue(true);
                                    Log.d("ContentRepository", "ShopInfo 삭제 성공: " + documentId);
                                })
                                .addOnFailureListener(e -> {
                                    statusDeleteLiveData.postValue(false);
                                    Log.e("ContentRepository", "ShopInfo 삭제 실패", e);
                                });
                    } else {
                        statusDeleteLiveData.postValue(false);
                        Log.e("ContentRepository", "문서가 존재하지 않습니다: " + documentId);
                    }
                })
                .addOnFailureListener(e -> {
                    statusDeleteLiveData.postValue(false);
                    Log.e("ContentRepository", "문서 가져오기 실패", e);
                });
    }


    public void updateContent(Context context, ShopInfo shopInfo) {
        // 기존 이미지 URL을 가져옵니다.
        String existingUri = shopInfo.getUri();

        if (existingUri != null && existingUri.startsWith("https://firebasestorage.googleapis.com")) {
            // 기존 Firebase Storage 이미지 삭제
            StorageReference existingImageRef = storage.getReferenceFromUrl(existingUri);
            existingImageRef.delete().addOnSuccessListener(aVoid -> {
                Log.d("UpdateContentRepository", "기존 이미지 삭제 성공");
                uploadNewImageAndUpdate(context, shopInfo); // 새 이미지 업로드 및 Firestore 업데이트
            }).addOnFailureListener(e -> {
                Log.e("UpdateContentRepository", "기존 이미지 삭제 실패", e);
                uploadNewImageAndUpdate(context, shopInfo); // 실패해도 새 이미지 업로드 및 Firestore 업데이트 진행
            });
        } else {
            // 기존 이미지가 없거나 로컬 URI인 경우
            uploadNewImageAndUpdate(context, shopInfo);
        }
    }

    private void uploadNewImageAndUpdate(Context context, ShopInfo shopInfo) {
        if (shopInfo.getUri() != null && shopInfo.getUri().startsWith("content://")) {
            byte[] compressedImage = ImageUtils.compressImage(Uri.parse(shopInfo.getUri()), context);

            if (compressedImage != null) {
                StorageReference newImageRef = storage.getReference()
                        .child("images/" + UUID.randomUUID().toString() + ".jpg");

                newImageRef.putBytes(compressedImage)
                        .addOnSuccessListener(taskSnapshot ->
                                newImageRef.getDownloadUrl().addOnSuccessListener(newUri -> {
                                    shopInfo.setUri(newUri.toString());
                                    updateFirestoreDocument(shopInfo); // Firestore 문서 업데이트
                                })
                        )
                        .addOnFailureListener(e -> Log.e("UpdateContentRepository", "새 이미지 업로드 실패", e));
            } else {
                Log.e("UpdateContentRepository", "새 이미지 압축 실패");
            }
        } else {
            // 이미지 변경이 없는 경우 Firestore 업데이트만 수행
            updateFirestoreDocument(shopInfo);
        }
    }

    public void updateFirestoreDocument(ShopInfo shopInfo) {
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("classStatus", shopInfo.getClassStatus());
        updatedData.put("uri", shopInfo.getUri() != null ? shopInfo.getUri().toString() : null);
        updatedData.put("phoneNumber", shopInfo.getPhoneNumber());
        updatedData.put("onedayType", shopInfo.getOnedayType());
        updatedData.put("homepageAddress", shopInfo.getHomepageAddress());
        updatedData.put("shopName", shopInfo.getShopName());
        updatedData.put("updatedAt", System.currentTimeMillis());

        firestore.collection("shops")
                .document(shopInfo.getDocumentId())
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    statusLiveData.postValue(true);
                    Log.d("UpdateContentRepository", "ShopInfo 업데이트 성공: " + shopInfo.getDocumentId());
                })
                .addOnFailureListener(e -> {
                    statusLiveData.postValue(false);
                    Log.e("UpdateContentRepository", "ShopInfo 업데이트 실패", e);
                });
    }
}