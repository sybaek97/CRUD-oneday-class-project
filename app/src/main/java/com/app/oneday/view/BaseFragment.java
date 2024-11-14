package com.app.oneday.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public abstract class BaseFragment extends Fragment {

    private long backKeyPressedTime = 0;
    public abstract boolean isBackAvailable();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 뒤로가기 콜백 처리
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                    Toast.makeText(getContext(), "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                    backKeyPressedTime = System.currentTimeMillis();
                    return;
                } else {
                    if (isBackAvailable()) {
                        NavHostFragment.findNavController(BaseFragment.this).popBackStack();
                    } else {
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }
                }
            }
        });
    }
}
