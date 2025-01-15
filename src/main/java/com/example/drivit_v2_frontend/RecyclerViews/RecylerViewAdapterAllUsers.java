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
import com.example.drivit_v2_frontend.Fragment.DashBord_Admin.EditUserPage;
import com.example.drivit_v2_frontend.Fragment.DashBord_User.UserCarDetailPage;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.models.CarRental;
import com.example.drivit_v2_frontend.models.Users;

import java.util.ArrayList;
import java.util.List;

public class RecylerViewAdapterAllUsers extends RecyclerView.Adapter<RecylerViewAdapterAllUsers.ViewHolder> {

    ArrayList<Users> arrayList;
    Context context;

    public RecylerViewAdapterAllUsers(Context context,ArrayList<Users> users) {
        this.arrayList = users;
        this.context = context;
    }

    @NonNull
    @Override
    public RecylerViewAdapterAllUsers.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyler_view_users_item_model,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecylerViewAdapterAllUsers.ViewHolder holder, int position) {
        Users users = arrayList.get(position);
        holder.userName.setText(users.getUserName());
        holder.userDetails.setText("CIN : " + users.getCin());
        holder.userStatus.setText(String.valueOf(users.getStatus()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditUserPage.class);
                Bundle bundle_user = new Bundle();
                bundle_user.putSerializable("users", users);
                intent.putExtras(bundle_user);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView userName,userDetails,userStatus;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.id_image_model);
            userName = itemView.findViewById(R.id.id_text_username);
            userDetails = itemView.findViewById(R.id.id_text_model_details);
            userStatus = itemView.findViewById(R.id.id_text_model_status);
        }
    }
}
