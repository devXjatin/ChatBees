package com.example.chatbees;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    Context homeActivity;
    ArrayList<Users> usersArrayList;
    public UserAdapter(HomeActivity homeActivity, ArrayList<Users> usersArrayList) {
    this.homeActivity = homeActivity;
    this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(homeActivity).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Users users = usersArrayList.get(position);
    holder.userName.setText(users.name);
    holder.userStatus.setText(users.status);
        Picasso.get().load(users.imageUri).into(holder.userProfile);

        //navigate to chatting by click cheat head
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(homeActivity, ChatActivity.class);
                intent.putExtra("name", users.getName());
                intent.putExtra("receiverImage", users.getImageUri());
                intent.putExtra("uid", users.uid);
                homeActivity.startActivity(intent);
            }
        });

    }





    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView userProfile;
        TextView userName;
        TextView userStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.user_name);
            userStatus = itemView.findViewById(R.id.user_status);
        }
    }
}
