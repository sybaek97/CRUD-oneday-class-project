package com.app.oneday.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

public class PhotoPermissionHandler {

    private final Context context;
    private final ActivityResultLauncher<String> requestPermissionLauncher;

    public PhotoPermissionHandler(Context context, ActivityResultLauncher<String> requestPermissionLauncher) {
        this.context = context;
        this.requestPermissionLauncher = requestPermissionLauncher;
    }

    // 권한 체크 메서드
    public boolean checkPhotoPermission() {
        String permission = getPhotoPermission();
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    // 권한 요청 메서드
    public void requestPhotoPermission(int deniedCount) {
        String permission = getPhotoPermission();
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            if ((!shouldShowRequestPermissionRationale(permission) && deniedCount > 0) || deniedCount >= 2) {
                // 설정 화면으로 이동
                Toast.makeText(context, "설정에서 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            } else {
                // 권한 요청
                requestPermissionLauncher.launch(permission);
            }
        }
    }

    // Android 버전에 따라 권한 반환
    private String getPhotoPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            return Manifest.permission.READ_EXTERNAL_STORAGE;
        }
    }

    // Rational(설명) 필요 여부 확인
    private boolean shouldShowRequestPermissionRationale(String permission) {
        if (context instanceof Activity) {
            return ((Activity) context).shouldShowRequestPermissionRationale(permission);
        }
        return false;
    }
}