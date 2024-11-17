package com.app.oneday.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.oneday.databinding.FragmentMarketBinding;
import com.app.oneday.model.UserInfo;


public class MarketFragment extends BaseFragment {
    @Override
    public boolean isBackAvailable() {
        return false;
    }
    private FragmentMarketBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMarketBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserInfo userInfo = UserInfo.getInstance();
        Log.d("이름",userInfo.getName());
        if (userInfo.getStatus().equals("선생님")) {
            binding.txtName.setText(userInfo.getName() + " 선생님");
        } else {
            binding.txtName.setText(userInfo.getName() + " 님");
        }
    }

}