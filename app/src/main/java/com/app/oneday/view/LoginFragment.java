package com.app.oneday.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.app.oneday.MainActivity;
import com.app.oneday.R;
import com.app.oneday.databinding.FragmentLoginBinding;
import com.app.oneday.viewModel.AuthViewModel;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends BaseFragment {
    @Override
    public boolean isBackAvailable() {
        return false;
    }
    private AuthViewModel authViewModel;

    private LoadingDialogFragment loadingDialog;
    private FragmentLoginBinding binding;  // DataBinding을 위한 변수
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        NavController navController = Navigation.findNavController(view);
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.editEmail.getText().toString()==null||binding.editEmail.getText().toString().isEmpty()){
                    Toast.makeText(requireContext(),"아이디를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }else if(binding.editPw.getText().toString()==null||binding.editPw.getText().toString().isEmpty()){
                    Toast.makeText(requireContext(),"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                showLoadingDialog();
                authViewModel.login(binding.editEmail.getText().toString(), binding.editPw.getText().toString());

            }
        });

        authViewModel.getUserLiveData().observe(getViewLifecycleOwner(), new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null) {
                    Toast.makeText(requireContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                    hideLoadingDialog();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        // 인증 에러 관찰
        authViewModel.getAuthErrorLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                if (errorMessage != null) {
                    hideLoadingDialog();
                    Toast.makeText(requireContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_loginFragment_to_signUpFragment);

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
