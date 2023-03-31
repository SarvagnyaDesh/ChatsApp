package com.example.newactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<User> user;
    private ProgressBar progressBar;
    private UsersAdapter usersAdapter;
    UsersAdapter.OnUserClickListener onUserClickListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    String img_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        progressBar = findViewById(R.id.progressBar);
        user = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsers();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        onUserClickListener = new UsersAdapter.OnUserClickListener() {
            @Override
            public void onUserClicked(int position) {
                startActivity(new Intent(MainActivity2.this,MessageActivity.class)
                        .putExtra("username_of_roommate",user.get(position).getUsername())
                        .putExtra("email_of_roommate",user.get(position).getEmail())
                        .putExtra("image_of_roommate",user.get(position).getProfilepicture())
                        .putExtra("my_img",img_url)
                );
                Toast.makeText(MainActivity2.this, "Creating room with "+user.get(position).getUsername(), Toast.LENGTH_SHORT).show();
            }
        };

        getUsers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_item_profile){
            startActivity(new Intent(MainActivity2.this,ProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private  void getUsers(){
        user.clear();
        FirebaseDatabase.getInstance().getReference("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    user.add(dataSnapshot.getValue(User.class));
                }
                usersAdapter = new UsersAdapter(user,MainActivity2.this,onUserClickListener);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity2.this));
                recyclerView.setAdapter(usersAdapter);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                for(User i : user){
                    if(i.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                        img_url = i.getProfilepicture();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}