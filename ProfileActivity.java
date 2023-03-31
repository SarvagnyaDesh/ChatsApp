package com.example.newactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {
    private Button button;
    private ImageView profilImg ;
    private String imgurl;
    private String urname;
    private ProgressBar progressBar;
    private TextView textView;
    private Uri imagePath;
    FirebaseAuth mAuth;
    int flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profilImg = findViewById(R.id.ProfileImage);
        button = findViewById(R.id.btn_upload);
        progressBar = findViewById(R.id.progressBar2);
        textView = findViewById(R.id.textView);
        progressBar.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("user/"+ Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("username");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                urname = Objects.requireNonNull(snapshot.getValue()).toString();
                textView.setText("User Name : "+urname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
         db = FirebaseDatabase.getInstance().getReference("user/"+ Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("profilepicture");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imgurl = Objects.requireNonNull(snapshot.getValue()).toString();
                if(!imgurl.equals(""))
                    Picasso.get().load(imgurl).into(profilImg);
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        profilImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag =1;
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent,1);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                if(flag==1)
                    uploadImage();
                else
                    Toast.makeText(ProfileActivity.this, "Select Image to Upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null){
            imagePath = data.getData();
            getImageInImageView();
        }
    }

    private void getImageInImageView() {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
            profilImg.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void uploadImage(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        FirebaseStorage.getInstance().getReference("image/"+ UUID.randomUUID().toString()).putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                updateProfilePicture(task.getResult().toString());
                            }
                        }
                    });
                    Toast.makeText(ProfileActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ProfileActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = 100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount();
                progressDialog.setMessage("Uploading "+(int)progress+"%");
            }
        });
    }
    private void updateProfilePicture(String url){
        FirebaseDatabase.getInstance().getReference("user/"+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+"/profilepicture").setValue(url);
    }
    public void logout(View v){
        mAuth.signOut();
        startActivity(new Intent(ProfileActivity.this,MainActivity.class));
        finish();
    }
}
