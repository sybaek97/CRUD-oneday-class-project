package com.app.oneday.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.oneday.R;
import com.app.oneday.model.ShopInfo;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<ShopInfo> shopList;
    private ViewHolder.OnItemClickListener listener;

    public MainAdapter(List<ShopInfo> shopList, ViewHolder.OnItemClickListener listener) {
        this.shopList = shopList;
        this.listener = listener;
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
        holder.textTitle.setText(shop.getOnedayType());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(shop));
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title); // TextView 바인딩
        }

        public interface OnItemClickListener {
            void onItemClick(ShopInfo shopInfo);
        }
    }
}