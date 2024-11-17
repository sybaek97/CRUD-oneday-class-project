package com.app.oneday.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.airbnb.lottie.L;
import com.app.oneday.MainActivity;
import com.app.oneday.R;
import com.app.oneday.common.PhotoPermissionHandler;
import com.app.oneday.databinding.FragmentClassBinding;
import com.app.oneday.model.ShopInfo;
import com.app.oneday.model.UserInfo;
import com.app.oneday.viewModel.ContentViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.atomic.AtomicReference;

public class ClassFragment extends BaseFragment {
    @Override
    public boolean isBackAvailable() {
        return false;
    }

    private FragmentClassBinding binding;
    private LoadingDialogFragment loadingDialog;
    private final ContentViewModel viewModel = new ContentViewModel();
    private PhotoPermissionHandler permissionHandler;
    private Uri imageUri = null;
    private int permissionDeniedCount = 0;
    private FirebaseAuth firebaseAuth;

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
        binding = FragmentClassBinding.inflate(inflater, container, false);
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

        ShopInfo shopInfo = getShopInfo();
        binding.setShopInfo(shopInfo);
        showLoadingDialog();
        binding.imgTitle.setOnClickListener(v -> checkPermissionAndOpenGallery());
        Log.d("off",shopInfo.getClassStatus());

        if (shopInfo.getClassStatus().equals("offline")) {
            binding.checkboxOff.setChecked(true);
            binding.checkboxOn.setChecked(false);
            binding.boxShopName.setVisibility(View.VISIBLE);
        } else {
            binding.checkboxOff.setChecked(false);
            binding.checkboxOn.setChecked(true);
            binding.boxShopName.setVisibility(View.GONE);

        }
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


        setUserState(UserInfo.getInstance().getStatus());

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showLoadingDialog();
                viewModel.deleteContent(shopInfo.getDocumentId());
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_classFragment_to_mainFragment);
            }
        });

        binding.btnEdit.setOnClickListener(v -> {
            if (isAtBottom(binding.scrollView)) {
                String currentImageUrl = shopInfo.getUri();
                String newImageUrl = (imageUri != null) ? imageUri.toString() : currentImageUrl;

                if (newImageUrl.equals(currentImageUrl)) {
                    if (validateInputs(classStatus.get())) {
                        showLoadingDialog();
                        shopInfo.setClassStatus(classStatus.get());
                        shopInfo.setDocumentId(shopInfo.getDocumentId());
                        shopInfo.setId(firebaseAuth.getUid());
                        shopInfo.setUri(currentImageUrl); // 기존 이미지 URI 유지
                        shopInfo.setPhoneNumber(binding.editNumber.getText().toString());
                        shopInfo.setOnedayType(binding.editOnedayType.getText().toString());
                        shopInfo.setHomepageAddress(binding.editHomepageAddress.getText().toString());

                        if (classStatus.get().equals("online")) {
                            shopInfo.setShopName("online");
                        } else {
                            shopInfo.setShopName(binding.editShopName.getText().toString());
                        }

                        viewModel.editContent(requireContext(), shopInfo);
                    }
                } else {
                    if (validateInputs(classStatus.get())) {
                        showLoadingDialog();
                        shopInfo.setClassStatus(classStatus.get());
                        shopInfo.setDocumentId(shopInfo.getDocumentId());
                        shopInfo.setId(firebaseAuth.getUid());
                        shopInfo.setUri(newImageUrl); // 새로운 이미지 URI 설정
                        shopInfo.setPhoneNumber(binding.editNumber.getText().toString());
                        shopInfo.setOnedayType(binding.editOnedayType.getText().toString());
                        shopInfo.setHomepageAddress(binding.editHomepageAddress.getText().toString());

                        if (classStatus.get().equals("online")) {
                            shopInfo.setShopName("online");
                        } else {
                            shopInfo.setShopName(binding.editShopName.getText().toString());
                        }

                        viewModel.editContentImage(requireContext(), shopInfo);
                    }
                }
            } else {
                binding.scrollView.post(() -> binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN));
            }
        });
        viewModel.getSaveStatus().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccess) {
                hideLoadingDialog();
                if (isSuccess) {
                    navController.navigate(R.id.action_classFragment_to_mainFragment);
                    Toast.makeText(requireContext(), "수정 성공", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "수정 실패", Toast.LENGTH_SHORT).show();

                }
            }
        });

        viewModel.getDeleteStatus().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccess) {
                hideLoadingDialog();
                if (isSuccess) {
                    navController.navigate(R.id.action_classFragment_to_mainFragment);
                    Toast.makeText(requireContext(), "삭제 성공", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "삭제 실패", Toast.LENGTH_SHORT).show();

                }
            }
        });


        Glide.with(binding.imgTitle.getContext()).load(shopInfo.getUri()) // URI로 이미지 로드
                .placeholder(R.drawable.default_image) // 기본 이미지 설정
                .error(R.drawable.default_image) // 에러 발생 시 기본 이미지
                .diskCacheStrategy(DiskCacheStrategy.ALL) // 캐싱 전략
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        hideLoadingDialog();

                        Toast.makeText(requireContext(), "이미지 로드 실패", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        hideLoadingDialog();
                        return false;
                    }
                }).into(binding.imgTitle);
    }
    private boolean validateInputs(String classStatus) {
        if (classStatus.equals("online")) {
            if (binding.editHomepageAddress.getText() == null ||
                    binding.editOnedayType.getText() == null ||
                    binding.editNumber.getText() == null ||
                    binding.editHomepageAddress.getText().toString().trim().isEmpty() ||
                    binding.editOnedayType.getText().toString().trim().isEmpty() ||
                    binding.editNumber.getText().toString().trim().isEmpty()) {
                Toast.makeText(requireContext(), "빈칸을 모두 채워주세요!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            if (binding.editHomepageAddress.getText() == null ||
                    binding.editOnedayType.getText() == null ||
                    binding.editNumber.getText() == null ||
                    binding.editShopName.getText() == null ||
                    binding.editHomepageAddress.getText().toString().trim().isEmpty() ||
                    binding.editOnedayType.getText().toString().trim().isEmpty() ||
                    binding.editNumber.getText().toString().trim().isEmpty() ||
                    binding.editShopName.getText().toString().trim().isEmpty()) {
                Toast.makeText(requireContext(), "빈칸을 모두 채워주세요!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
    private ShopInfo getShopInfo() {
        ShopInfo shopInfo = ShopInfo.getInstance();
        shopInfo.setId(getArguments().getString("shopId"));
        shopInfo.setDocumentId(getArguments().getString("documentId"));
        shopInfo.setClassStatus(getArguments().getString("classStatus"));
        shopInfo.setUri(getArguments().getString("uri"));
        shopInfo.setPhoneNumber(getArguments().getString("phoneNumber"));
        shopInfo.setOnedayType(getArguments().getString("onedayType"));
        shopInfo.setHomepageAddress(getArguments().getString("homepageAddress"));
        shopInfo.setShopName(getArguments().getString("shopName"));

        return shopInfo;
    }

    private void setUserState(String status) {
        if (status.equals("선생님")) {
            binding.btnDelete.setVisibility(View.VISIBLE);
            binding.btnEdit.setVisibility(View.VISIBLE);
            binding.btnEnrollment.setVisibility(View.GONE);

            binding.editClassStatus.setVisibility(View.VISIBLE);
            binding.editHomepageAddress.setVisibility(View.VISIBLE);
            binding.editOnedayType.setVisibility(View.VISIBLE);
            binding.editNumber.setVisibility(View.VISIBLE);
            binding.editShopName.setVisibility(View.VISIBLE);

            binding.txtClassStatus.setVisibility(View.GONE);
            binding.txtHomepageAddress.setVisibility(View.GONE);
            binding.txtOnedayType.setVisibility(View.GONE);
            binding.txtNumber.setVisibility(View.GONE);
            binding.txtShopName.setVisibility(View.GONE);
            binding.imgTitle.setClickable(true);
            binding.imgTitle.setFocusable(true);

        } else {
            binding.btnDelete.setVisibility(View.GONE);
            binding.btnEdit.setVisibility(View.GONE);
            binding.btnEnrollment.setVisibility(View.VISIBLE);

            binding.editClassStatus.setVisibility(View.GONE);
            binding.editHomepageAddress.setVisibility(View.GONE);
            binding.editOnedayType.setVisibility(View.GONE);
            binding.editNumber.setVisibility(View.GONE);
            binding.editShopName.setVisibility(View.GONE);

            binding.txtClassStatus.setVisibility(View.VISIBLE);
            binding.txtHomepageAddress.setVisibility(View.VISIBLE);
            binding.txtOnedayType.setVisibility(View.VISIBLE);
            binding.txtNumber.setVisibility(View.VISIBLE);
            binding.txtShopName.setVisibility(View.VISIBLE);
            binding.imgTitle.setClickable(false);
            binding.imgTitle.setFocusable(false);

        }
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

    private boolean isAtBottom(ScrollView scrollView) {
        int diff = scrollView.getChildAt(0).getBottom() - (scrollView.getHeight() + scrollView.getScrollY());
        return diff <= 0;
    }

    private void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialogFragment();
        }
        if (!loadingDialog.isAdded() && !loadingDialog.isVisible()) {
            loadingDialog.show(getParentFragmentManager(), "loading");
        }
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}