package com.example.newactivity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Printer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserHolder> {
    private ArrayList<User> user;
    private Context context;
    private OnUserClickListener onUserClickListener;

    public UsersAdapter(ArrayList<User> user, Context context, OnUserClickListener onUserClickListener){
        this.user = user;
        this.context = context;
        this.onUserClickListener = onUserClickListener;
    }

    interface OnUserClickListener{
        void onUserClicked(int position);
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_holder,parent,false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        holder.txtUserName.setText(user.get(position).getUsername());
        Glide.with(context).load(user.get(position).getProfilepicture()).error(R.drawable.account_img).placeholder(R.drawable.account_img).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return user.size();
    }

    class UserHolder extends RecyclerView.ViewHolder{
        TextView txtUserName;
        ImageView imageView;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onUserClickListener.onUserClicked(getAdapterPosition());
                }
            });
            txtUserName = itemView.findViewById(R.id.txtUserName);
            imageView = itemView.findViewById(R.id.img_pro);
        }
    }
}
