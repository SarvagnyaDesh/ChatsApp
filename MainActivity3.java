package com.example.newactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity3 extends AppCompatActivity {

    private EditText inputName ;
    private EditText inputEmail ;
    private EditText inputPassword ;
    private EditText inputConfoPassword;
    private Button button ;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        inputName=findViewById(R.id.editName);
        inputEmail=findViewById(R.id.editEmailAddress2);
        inputPassword=findViewById(R.id.editPassword2);
        inputConfoPassword=findViewById(R.id.editConformPassword2);
        button=findViewById(R.id.button3);

        mAuth = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateDataAndRegister();
            }
        });
    }
     public void validateDataAndRegister() {
        String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confoPassword = inputConfoPassword.getText().toString().trim();
        if(name.length()==0){
            inputName.setError("Name cannot be Empty");
            inputName.requestFocus();
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            inputEmail.setError("Enter Valid Email Address");
            inputEmail.requestFocus();
            return;
        }
        if(password.length()<=6){
            inputPassword.setError("Password should contain more than 6 characters");
            inputPassword.requestFocus();
            return;
        }
        if(!confoPassword.equals(password)){
            inputConfoPassword.setError("Doesn't match with the password");
            inputConfoPassword.requestFocus();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    FirebaseDatabase.getInstance().getReference("user/" + mAuth.getCurrentUser().getUid()).setValue(new User(name,email,""));
                    assert user != null;
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity3.this, "Verification code sent to email", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity3.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity3.this,"Email is already Registered!", Toast.LENGTH_SHORT).show();
                inputEmail.setError("Email is already Registered!");
                inputEmail.requestFocus();
            }
        });
    }
}