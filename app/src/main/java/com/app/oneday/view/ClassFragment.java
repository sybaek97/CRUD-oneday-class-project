package com.app.oneday.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.oneday.MainActivity;
import com.app.oneday.databinding.FragmentClassBinding;
import com.app.oneday.databinding.FragmentMarketBinding;

public class ClassFragment  extends BaseFragment {
    @Override
    public boolean isBackAvailable() {
        return false;
    }
    private FragmentClassBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentClassBinding.inflate(inflater, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.binding.navigationLayout.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}