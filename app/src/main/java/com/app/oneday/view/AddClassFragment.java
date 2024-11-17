package com.app.oneday.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.app.oneday.MainActivity;
import com.app.oneday.R;
import com.app.oneday.common.PhotoPermissionHandler;
import com.app.oneday.databinding.FragmentAddClassBinding;
import com.app.oneday.model.ShopInfo;
import com.app.oneday.viewModel.ContentViewModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.atomic.AtomicReference;

public class AddClassFragment extends BaseFragment {
    @Override
    public boolean isBackAvailable() {
        return false;
    }

    private FragmentAddClassBinding binding;
    private int permissionDeniedCount = 0;
    private Uri imageUri = null;
    private final ContentViewModel viewModel = new ContentViewModel();
    private LoadingDialogFragment loadingDialog = null;
    private FirebaseAuth firebaseAuth;
    private PhotoPermissionHandler permissionHandler;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            imageUri = result.getData() != null ? result.getData().getData() : null;
            if (imageUri != null) {
                Glide.with(binding.imgTitle.getContext())
                        .load(imageUri)
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .into(binding.imgTitle);
            } else {
                Toast.makeText(requireContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    });

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            openGallery();
        } else {
            Toast.makeText(requireContext(), "사진 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    });



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddClassBinding.inflate(inflater, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.binding.navigationLayout.setVisibility(View.GONE);
        }
        permissionHandler = new PhotoPermissionHandler(requireContext(), requestPermissionLauncher);

        firebaseAuth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);
        AtomicReference<String> classStatus = new AtomicReference<>("offline");
        ShopInfo shopInfo = ShopInfo.getInstance();
        binding.checkboxOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.boxShopName.setVisibility(View.VISIBLE);
                classStatus.set("offline");
                binding.checkboxOn.setChecked(false);
            } else if (!binding.checkboxOn.isChecked()) {
                binding.boxShopName.setVisibility(View.GONE);
                binding.checkboxOff.setChecked(true);
            }
        });

        binding.checkboxOn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                classStatus.set("online");
                binding.boxShopName.setVisibility(View.GONE);
                binding.checkboxOff.setChecked(false);
            } else if (!binding.checkboxOff.isChecked()) {
                binding.boxShopName.setVisibility(View.VISIBLE);
                binding.checkboxOn.setChecked(true);
            }
        });


        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_addClassFragment_to_mainFragment);
            }
        });
        binding.imgTitle.setOnClickListener(v -> checkPermissionAndOpenGallery());
        binding.btnAdd.setOnClickListener(v -> {
            if (isAtBottom(binding.scrollView)) {
                if (imageUri == null) {
                    Toast.makeText(requireContext(), "사진을 선택해주세요!", Toast.LENGTH_SHORT).show();
                    return; // 이후 코드 실행 방지
                }
                if (classStatus.get().equals("online")) {
                    if (binding.editHomepageAddress.getText() == null || binding.editOnedayType.getText() == null || binding.editPhoneNumber.getText() == null || binding.editHomepageAddress.getText().toString().trim().isEmpty() || binding.editOnedayType.getText().toString().trim().isEmpty() || binding.editPhoneNumber.getText().toString().trim().isEmpty()) {
                        Toast.makeText(requireContext(), "빈칸을 모두 채워주세요!", Toast.LENGTH_SHORT).show();
                    } else {
                        showLoadingDialog();
                        shopInfo.setClassStatus(classStatus.get());
                        shopInfo.setId(firebaseAuth.getUid());
                        shopInfo.setUri(imageUri.toString());
                        shopInfo.setPhoneNumber(binding.editPhoneNumber.getText().toString());
                        shopInfo.setOnedayType(binding.editOnedayType.getText().toString());
                        shopInfo.setHomepageAddress(binding.editHomepageAddress.getText().toString());
                        shopInfo.setShopName("");
                        viewModel.addContent(requireContext(),shopInfo);

                    }
                } else {
                    if (binding.editHomepageAddress.getText() == null || binding.editOnedayType.getText() == null || binding.editPhoneNumber.getText() == null || binding.editShopName.getText() == null || binding.editHomepageAddress.getText().toString().trim().isEmpty() || binding.editOnedayType.getText().toString().trim().isEmpty() || binding.editPhoneNumber.getText().toString().trim().isEmpty() || binding.editShopName.getText().toString().trim().isEmpty()) {
                        Toast.makeText(requireContext(), "빈칸을 모두 채워주세요!", Toast.LENGTH_SHORT).show();
                    } else {
                        showLoadingDialog();
                        shopInfo.setClassStatus(classStatus.get());
                        shopInfo.setId(firebaseAuth.getUid());
                        shopInfo.setUri(imageUri.toString());
                        shopInfo.setPhoneNumber(binding.editPhoneNumber.getText().toString());
                        shopInfo.setOnedayType(binding.editOnedayType.getText().toString());
                        shopInfo.setHomepageAddress(binding.editHomepageAddress.getText().toString());
                        shopInfo.setShopName(binding.editShopName.getText().toString());

                        viewModel.addContent(requireContext(),shopInfo);
                    }
                }
            } else {
                binding.scrollView.post(() -> binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN));
            }
        });
        viewModel.getSaveStatus().observe(getViewLifecycleOwner(), isSuccess -> {
            hideLoadingDialog();

            if (isSuccess) {
                NavHostFragment.findNavController(this).navigate(R.id.action_addClassFragment_to_mainFragment);
                Toast.makeText(requireContext(), "내용이 저장되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "저장 실패", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean isAtBottom(ScrollView scrollView) {
        int diff = scrollView.getChildAt(0).getBottom() - (scrollView.getHeight() + scrollView.getScrollY());
        return diff <= 0;
    }

    private void checkPermissionAndOpenGallery() {
        if (permissionHandler.checkPhotoPermission()) {
            openGallery();
        } else {
            permissionHandler.requestPhotoPermission(permissionDeniedCount);
            permissionDeniedCount++;
        }
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialogFragment();
        }
        loadingDialog.show(getParentFragmentManager(), "loading");
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}