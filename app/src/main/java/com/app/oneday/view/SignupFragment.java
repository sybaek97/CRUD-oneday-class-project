package com.app.oneday.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.app.oneday.R;
import com.app.oneday.databinding.FragmentSignupBinding;
import com.app.oneday.model.UserInfo;
import com.app.oneday.viewModel.AuthViewModel;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class SignupFragment extends Fragment {

    private FragmentSignupBinding binding;  // DataBinding을 위한 변수
    private AuthViewModel authViewModel;
    private String searchKeyword = "선생님";
    private LoadingDialogFragment loadingDialog;
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);
        ArrayList<String> itemList = new ArrayList<>();
        itemList.add("선생님");
        itemList.add("학생");


        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_dropdown_item,
                itemList
        );


        binding.searchKeyword.setAdapter(spinnerAdapter);
        binding.searchKeyword.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                searchKeyword = selectedItem;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무 것도 선택되지 않았을 때 처리할 내용이 있으면 추가 가능
            }
        });
        // 버튼 클릭 리스너 설정
        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.editEmail.getText().toString().isEmpty() ||
                        binding.editPw.getText().toString().isEmpty() ||
                        binding.editPwCheck.getText().toString().isEmpty() ||
                        binding.editName.getText().toString().isEmpty()) {

                    // 하나라도 입력하지 않았을 때
                    Toast.makeText(requireContext(), "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();

                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.editEmail.getText().toString()).matches()) {

                    // 이메일 형식이 잘못되었을 때
                    Toast.makeText(requireContext(), "이메일 형식을 확인해주세요.", Toast.LENGTH_SHORT).show();

                } else if (binding.editPw.getText().toString().length() < 6) {

                    // 비밀번호가 6자리 미만일 때
                    Toast.makeText(requireContext(), "비밀번호는 최소 6자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show();

                } else if (!binding.editPw.getText().toString().equals(binding.editPwCheck.getText().toString())) {

                    // 비밀번호 확인이 일치하지 않을 때
                    Toast.makeText(requireContext(), "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();

                } else {
                    showLoadingDialog();
                    // 모든 조건이 만족되었을 때 회원가입 진행



                    authViewModel.signUp(
                            binding.editEmail.getText().toString(),
                            binding.editPw.getText().toString(),
                            binding.editName.getText().toString(),
                            searchKeyword
                    );
                }

            }
        });

        authViewModel.getAuthErrorLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                if (errorMessage != null) {
                    hideLoadingDialog();
                    Toast.makeText(requireContext(), "회원가입에 실패했습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
        authViewModel.getUserLiveData().observe(getViewLifecycleOwner(), new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                hideLoadingDialog();
                if (firebaseUser != null) {
                    Toast.makeText(requireContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.action_signUpFragment_to_loginFragment);

                }else{
                    Toast.makeText(requireContext(), "회원가입에 실패했습니다", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}