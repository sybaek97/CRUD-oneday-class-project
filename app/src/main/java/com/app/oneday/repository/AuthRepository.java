package com.app.oneday.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.oneday.model.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthRepository extends ViewModel {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private MutableLiveData<FirebaseUser> userLiveData;
    private MutableLiveData<String> authErrorLiveData;
    private MutableLiveData<UserInfo> userInfoLiveData;
    private MutableLiveData<Boolean> deleteStatusLiveData;  // 삭제 상태를 나타낼 LiveData


    public AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userLiveData = new MutableLiveData<>();
        authErrorLiveData = new MutableLiveData<>();
        userInfoLiveData = new MutableLiveData<>();
        deleteStatusLiveData = new MutableLiveData<>();
    }
    public MutableLiveData<Boolean> getDeleteStatusLiveData() {
        return deleteStatusLiveData;
    }
    public MutableLiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }
    public MutableLiveData<UserInfo> getUserInfoLiveData() {
        return userInfoLiveData;
    }

    public MutableLiveData<String> getAuthErrorLiveData() {
        return authErrorLiveData;
    }



    // 회원가입 함수
    public void signUp(final String email, final String password, final String name,final String status) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                saveUserToFirestore(user.getUid(), email, name,status);
                            }
                        } else {
                            authErrorLiveData.postValue(task.getException().getMessage());
                        }
                    }
                });
    }

    // Firestore에 사용자 정보 저장
    public void saveUserToFirestore(String uid, String email, String name , String status) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("name", name);
        userMap.put("status", status);

        firestore.collection("users").document(uid)
                .set(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            userLiveData.postValue(firebaseAuth.getCurrentUser());

                        } else {
                            authErrorLiveData.postValue(task.getException().getMessage());
                        }
                    }
                });
    }

    // 로그인 함수
    public void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userLiveData.postValue(firebaseAuth.getCurrentUser());
                        } else {
                            authErrorLiveData.postValue(task.getException().getMessage());
                        }
                    }
                });
    }



    public void getUserInfo(String userId) {
        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            UserInfo userInfo = document.toObject(UserInfo.class);
                            if (userInfo != null) {
                                Log.d("Firestore", userInfo.getStatus());
                                userInfoLiveData.postValue(userInfo);
                            } else {
                                Log.d("Firestore", "UserInfo 변환 실패");
                            }
                        } else {
                            Log.d("Firestore", "해당 사용자 문서가 없습니다.");
                        }
                    } else {
                        Log.w("Firestore", "데이터 가져오기 실패: ", task.getException());
                    }
                });
    }




    public void deleteUserAndData() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            firestore.collection("users").document(userId)
                    .delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Firestore", "사용자 정보 삭제 완료 (users)");
                        } else {
                            Log.e("Firestore", "사용자 정보 삭제 실패 (users)", task.getException());
                            authErrorLiveData.postValue(task.getException().getMessage());
                        }
                    });


            currentUser.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Auth", "계정 삭제 완료");

                            deleteStatusLiveData.postValue(true);
                        } else {
                            Log.e("Auth", "계정 삭제 실패", task.getException());
                            authErrorLiveData.postValue(task.getException().getMessage());
                        }
                    });
        } else {
            Log.e("Auth", "사용자가 로그인되어 있지 않음");
            authErrorLiveData.postValue("No user is logged in.");
        }
    }

    // 로그아웃 처리
    public void signOut() {
        firebaseAuth.signOut();
        Log.d("Auth", "로그아웃 완료");
    }
}