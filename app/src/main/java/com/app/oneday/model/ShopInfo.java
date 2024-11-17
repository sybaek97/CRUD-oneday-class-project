package com.app.oneday.model;

import android.net.Uri;

public class ShopInfo {
    private static ShopInfo instance;
    private String id;
    private String classStatus;
    private String uri;
    private String phoneNumber;
    private String onedayType;
    private String homepageAddress;
    private String shopName;
    private String documentId; // Firestore의 Document ID


    private ShopInfo() {}

    public static synchronized ShopInfo getInstance() {
        if (instance == null) {
            instance = new ShopInfo();
        }
        return instance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassStatus() {
        return classStatus;
    }

    public void setClassStatus(String classStatus) {
        this.classStatus = classStatus;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOnedayType() {
        return onedayType;
    }

    public void setOnedayType(String onedayType) {
        this.onedayType = onedayType;
    }

    public String getHomepageAddress() {
        return homepageAddress;
    }

    public void setHomepageAddress(String homepageAddress) {
        this.homepageAddress = homepageAddress;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
