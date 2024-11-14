package com.app.oneday.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.app.oneday.model.UserInfo;
import com.app.oneday.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {

    private AuthRepository authRepository;
    private LiveData<FirebaseUser> userLiveData;
    private LiveData<String> authErrorLiveData;
    private LiveData<UserInfo> userInfoLiveData;

    public AuthViewModel() {
        authRepository = new AuthRepository();
        userLiveData = authRepository.getUserLiveData();
        authErrorLiveData = authRepository.getAuthErrorLiveData();
        userInfoLiveData = authRepository.getUserInfoLiveData();
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }
    public LiveData<String> getAuthErrorLiveData() {
        return authErrorLiveData;
    }
    public LiveData<UserInfo> getUserInfoLiveData() {
        return userInfoLiveData;
    }

    // 회원가입 함수에 주소 추가
    public void signUp(String email, String password, String name,String status) {
        authRepository.signUp(email, password, name,status);
    }
    public void getNameData(String userId){
        authRepository.getUserInfo(userId);
    }

    //로그인
    public void login(String email, String password) {
        authRepository.login(email, password);
    }

    // 사용자 삭제 요청
    public void deleteUserAndData() {
        authRepository.deleteUserAndData();
    }

    // 로그아웃 요청
    public void signOut() {
        authRepository.signOut();
    }
}
