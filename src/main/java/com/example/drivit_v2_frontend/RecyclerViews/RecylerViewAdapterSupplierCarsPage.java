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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.drivit_v2_frontend.Fragment.DashBord_Admin.DetailAdminEditCarPage;
import com.example.drivit_v2_frontend.Fragment.DashBord_Supplier.DetailSupplierEditCarPage;
import com.example.drivit_v2_frontend.Fragment.DashBord_Supplier.EditCarPage;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.Sessions.SessionManager;
import com.example.drivit_v2_frontend.enums.Status_dispo;
import com.example.drivit_v2_frontend.models.Cars;

import java.util.ArrayList;
import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class RecylerViewAdapterSupplierCarsPage extends RecyclerView.Adapter<RecylerViewAdapterCarsPage.ViewHolder>{

    Context context;
    ArrayList<Cars> arrayList;

    SessionManager sessionManager;

    public RecylerViewAdapterSupplierCarsPage(Context context, ArrayList<Cars> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.sessionManager = new SessionManager(this.context);
    }

    @NonNull
    @Override
    public RecylerViewAdapterCarsPage.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyler_view_item_model,parent,false);
        return new RecylerViewAdapterCarsPage.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecylerViewAdapterCarsPage.ViewHolder holder, int position) {
        Cars carsItem = arrayList.get(position);
        Glide.with(context).load(carsItem.getCarImage()).into(holder.imageView);
        holder.carName.setText(carsItem.getCarName());
        holder.carDetails.setText("Price :\n" + carsItem.getCarPrix() + " Dhs.");
        holder.carStatus.setText(String.valueOf(carsItem.getStatusDipo()) +" | "+  String.valueOf(carsItem.getStatusAdd()));

        HashMap<String,String> userDetails = sessionManager.getUserDetailFromSession();
        String _status_user = userDetails.get(SessionManager.KEY_STATUS);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (_status_user) {
                    case "SUPPLIER":
                        if(carsItem.getStatusDipo() == Status_dispo.AVAILABLE){
                            Intent intent_user = new Intent(context, DetailSupplierEditCarPage.class);
                            Bundle bundle_user = new Bundle();
                            bundle_user.putSerializable("carsItem",carsItem);
                            intent_user.putExtras(bundle_user);
                            context.startActivity(intent_user);
                        }
                        else {
                            StyleableToast.makeText(context, "To Edit A Car It Needs To Be Available", Toast.LENGTH_SHORT, R.style.mytoastinfo).show();
                        }
                        break;
                    case "ADMIN" :
                        Intent intent_user = new Intent(context, DetailAdminEditCarPage.class);
                        Bundle bundle_user = new Bundle();
                        bundle_user.putSerializable("carsItem",carsItem);
                        intent_user.putExtras(bundle_user);
                        context.startActivity(intent_user);
                }

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
