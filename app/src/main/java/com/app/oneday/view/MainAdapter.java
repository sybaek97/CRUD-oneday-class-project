package com.app.oneday.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.app.oneday.R;
import com.app.oneday.model.ShopInfo;
import com.app.oneday.viewModel.ContentViewModel;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private final List<ShopInfo> shopList;
    private final NavController navController;

    public MainAdapter(List<ShopInfo> shopList, NavController navController, ContentViewModel viewModel) {
        this.shopList = shopList;
        this.navController = navController;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShopInfo shop = shopList.get(position);
        holder.bind(shop);
        holder.textTitle.setText(shop.getOnedayType());
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;
        LinearLayout itemMain;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title); // TextView 바인딩
            itemMain = itemView.findViewById(R.id.item_main);
        }
        public void bind(ShopInfo shop){
            itemMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("documentId", shop.getDocumentId());
                    bundle.putString("shopId", shop.getId());
                    bundle.putString("classStatus", shop.getClassStatus());
                    bundle.putString("uri", shop.getUri());
                    bundle.putString("phoneNumber", shop.getPhoneNumber());
                    bundle.putString("onedayType", shop.getOnedayType());
                    bundle.putString("homepageAddress", shop.getHomepageAddress());
                    bundle.putString("shopName", shop.getShopName());
                    navController.navigate(R.id.action_mainFragment_to_classFragment,bundle);
                }
            });
        }

    }



}