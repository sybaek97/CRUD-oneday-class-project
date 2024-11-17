package com.app.oneday.view;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.oneday.MainActivity;
import com.app.oneday.R;
import com.app.oneday.databinding.FragmentMainBinding;
import com.app.oneday.model.UserInfo;
import com.app.oneday.viewModel.AuthViewModel;
import com.app.oneday.viewModel.ContentViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainFragment extends BaseFragment {
    @Override
    public boolean isBackAvailable() {
        return false;
    }

    private AuthViewModel authViewModel;
    private ContentViewModel contentViewModel;
    private FragmentMainBinding binding;
    private LoadingDialogFragment loadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.binding.navigationLayout.setVisibility(View.VISIBLE);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showLoadingDialog();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        contentViewModel = new ViewModelProvider(this).get(ContentViewModel.class);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        NavController navController = Navigation.findNavController(view);
        String uid = currentUser.getUid();
        authViewModel.getNameData(uid);

        authViewModel.getNameData(uid);

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (currentUser != null) {

            contentViewModel.getUserShopsLiveData().observe(getViewLifecycleOwner(), shopList -> {
                MainAdapter adapter = new MainAdapter(shopList, navController, contentViewModel);
                recyclerView.setAdapter(adapter);
            });
        }
        authViewModel.getUserInfoLiveData().observe(getViewLifecycleOwner(), new Observer<UserInfo>() {
            @Override
            public void onChanged(UserInfo info) {
                String status = info.getStatus();
                hideLoadingDialog();
                if (status.equals("선생님")) {
                    contentViewModel.getUserShops(uid);
                    binding.txtName.setText(info.getName() + " 선생님");
                    binding.btnCreate.setVisibility(View.VISIBLE);
                } else {
                    contentViewModel.getAllShops();
                    binding.txtName.setText(info.getName()+"님");
                    binding.btnCreate.setVisibility(View.GONE);
                }

            }
        });

        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_mainFragment_to_addClassFragment);
            }
        });

    }

    private void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialogFragment();
        }
        if (!loadingDialog.isAdded()) {
            loadingDialog.show(getParentFragmentManager(), "loading");
        }
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}
