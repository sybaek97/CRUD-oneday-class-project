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

import com.app.oneday.R;
import com.app.oneday.databinding.FragmentMainBinding;
import com.app.oneday.model.UserInfo;
import com.app.oneday.viewModel.AuthViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainFragment extends BaseFragment {
    @Override
    public boolean isBackAvailable() {
        return false;
    }
    private AuthViewModel authViewModel;
    private FragmentMainBinding binding;
    private LoadingDialogFragment loadingDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);



        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showLoadingDialog();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        authViewModel.getNameData(currentUser.getUid());
//        Log.d("tag",currentUser.getUid());

        authViewModel.getNameData("0f3s4JSDBWTHpraAXQ9csuOUhJ02");
        NavController navController = Navigation.findNavController(view);

        authViewModel.getUserInfoLiveData().observe(getViewLifecycleOwner(), new Observer<UserInfo>() {
            @Override
            public void onChanged(UserInfo info) {
               String status= info.getStatus();
               hideLoadingDialog();
               if(status.equals("선생님")){
                   binding.txtName.setText(info.getName()+"선생님");
                   binding.btnCreate.setVisibility(View.VISIBLE);
               }else{
                   binding.txtName.setText(info.getName());
               }

            }
        });

        binding.btnCreate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                navController.navigate(R.id.action_mainFragment_to_classFragment);
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
