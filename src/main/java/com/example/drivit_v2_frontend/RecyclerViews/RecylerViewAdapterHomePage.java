package com.example.drivit_v2_frontend.RecyclerViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.drivit_v2_frontend.Fragment.DetailCarPage;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.models.Cars;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecylerViewAdapterHomePage extends RecyclerView.Adapter<RecylerViewAdapterHomePage.ViewHolder> {

    Context context;
    ArrayList<Cars> arrayList;

    public RecylerViewAdapterHomePage(Context context, ArrayList<Cars> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public RecylerViewAdapterHomePage.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyler_view_item_model,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecylerViewAdapterHomePage.ViewHolder holder, int position) {
        Cars carsItem = arrayList.get(position);
        Glide.with(context).load(carsItem.getCarImage()).into(holder.imageView);
        holder.carName.setText(carsItem.getCarName());
        holder.carDetails.setText("Price :\n" + carsItem.getCarPrix() + " Dhs.");
        holder.carStatus.setText(String.valueOf(carsItem.getStatusDipo()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailCarPage.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("carsItem",carsItem);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView carName,carDetails,carStatus;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            carName = itemView.findViewById(R.id.id_text_carName);
            imageView = itemView.findViewById(R.id.id_image_model);
            carDetails = itemView.findViewById(R.id.id_text_model_details);
            carStatus = itemView.findViewById(R.id.id_text_model_status);
        }
    }
}
