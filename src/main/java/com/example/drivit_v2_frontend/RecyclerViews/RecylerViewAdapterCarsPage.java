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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.drivit_v2_frontend.Fragment.DetailCarPage;
import com.example.drivit_v2_frontend.Fragment.UserCarDetailPage;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.models.CarRental;
import com.example.drivit_v2_frontend.models.Cars;

import java.util.ArrayList;

public class RecylerViewAdapterCarsPage extends RecyclerView.Adapter<RecylerViewAdapterCarsPage.ViewHolder> {

    Context context;
    ArrayList<CarRental> arrayList;

    public RecylerViewAdapterCarsPage(Context context, ArrayList<CarRental> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public RecylerViewAdapterCarsPage.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyler_view_item_model,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecylerViewAdapterCarsPage.ViewHolder holder, int position) {
        CarRental CarRentalItem = arrayList.get(position);
        Glide.with(context).load(CarRentalItem.getCars().getCarImage()).into(holder.imageView);
        holder.carName.setText(CarRentalItem.getCars().getCarName());
        holder.carDetails.setText("Price :\n" + CarRentalItem.getCars().getCarPrix() + " Dhs.");
        holder.carStatus.setText(String.valueOf(CarRentalItem.getStatusRental()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserCarDetailPage.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("CarRentalItem",CarRentalItem);
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
