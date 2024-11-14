package com.app.oneday;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.app.oneday.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    public ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_main);
        NavController navController = navHostFragment.getNavController();



        binding.btnHome.setSelected(true);

        binding.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnHome.setSelected(true);
                binding.btnMypage.setSelected(false);
                navController.navigate(R.id.mainFragment);
            }
        });
        binding.btnMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnHome.setSelected(false);
                binding.btnMypage.setSelected(true);
                navController.navigate(R.id.marketFragment);
            }
        });



    }

}