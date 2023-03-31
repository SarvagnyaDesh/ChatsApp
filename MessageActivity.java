package com.example.newactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.ImageFilterButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editMessageInput;
    private TextView txtChattingWith;
    private ProgressBar progressBar;
    private ArrayList<Message> messages;
    private MessageAdapter messageAdapter;
    private ImageView imageToolbar,imgSend;

    String usernameOfTheRoommate, emailOfRoommate, chatRoomID,time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        usernameOfTheRoommate=getIntent().getStringExtra("username_of_roommate");
        emailOfRoommate=getIntent().getStringExtra("email_of_roommate");

        recyclerView=findViewById(R.id.recyclerMessages);
        editMessageInput=findViewById(R.id.editText);
        txtChattingWith=findViewById(R.id.txtChattingWith);
        progressBar=findViewById(R.id.progressMessages);
        imageToolbar=findViewById(R.id.img_toolbar);
        imgSend=findViewById(R.id.imageSendView);
        txtChattingWith.setText(usernameOfTheRoommate);
        messages = new ArrayList<>();

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat dateFormat = new SimpleDateFormat("h:mm aa");
                time = dateFormat.format(new Date());
                String mes_inp=editMessageInput.getText().toString();
                if(!mes_inp.equals("")) {
                    FirebaseDatabase.getInstance().getReference("messages/" + chatRoomID).push().setValue(new Message(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(), emailOfRoommate, mes_inp,time));
                    editMessageInput.setText("");
                }else{
                    Toast.makeText(MessageActivity.this, "Blank Message", Toast.LENGTH_SHORT).show();
                }
            }
        });

        messageAdapter=new MessageAdapter(messages,getIntent().getStringExtra("my_img"),getIntent().getStringExtra("image_of_roommate"),MessageActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        Glide.with(MessageActivity.this).load(getIntent().getStringExtra("image_of_roommate")).error(R.drawable.account_img).placeholder(R.drawable.account_img).into(imageToolbar);

        setupChatRoom();
    }

    private  void setupChatRoom(){
        FirebaseDatabase.getInstance().getReference("user/"+ FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String myUsername = snapshot.getValue(User.class).getUsername();
                if(usernameOfTheRoommate.compareTo(myUsername)>=0)
                    chatRoomID = myUsername+usernameOfTheRoommate;
                else
                    chatRoomID = usernameOfTheRoommate+myUsername;
                attachMessageListener(chatRoomID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void attachMessageListener(String chatRoomID){
        FirebaseDatabase.getInstance().getReference("messages/"+chatRoomID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    messages.add(dataSnapshot.getValue(Message.class));
                }
                messageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size()-1);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}