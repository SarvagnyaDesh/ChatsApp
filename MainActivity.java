package com.example.newactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class MainActivity extends AppCompatActivity {

    private EditText inputEmail,inputPassword;
    private TextView tvForgotPassword,tvRegister;
    private Button button;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputEmail=findViewById(R.id.editEmailAddress);
        inputPassword=findViewById(R.id.editPassword);
        tvForgotPassword=findViewById(R.id.tvForgotPassword);
        tvRegister=findViewById(R.id.tvRegister);
        button=findViewById(R.id.button);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            Intent intent = new Intent(this,MainActivity2.class);
            startActivity(intent);
            finish();
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {validateDataAndDoLogin();}

        });
    }
    public void openActivity(View v) {
        Toast.makeText(this, "Directing to the Registration Interface", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(this,MainActivity3.class);
        startActivity(intent);
    }
    private void validateDataAndDoLogin() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        if(email.isEmpty()){
            inputEmail.setError("Field can't be empty");
            inputEmail.requestFocus();
        }
        else if(password.isEmpty()){
            inputPassword.setError("Field can't be empty");;
            inputPassword.requestFocus();
        }
        else if(email.length()<=10){
            inputEmail.setError("Enter A Valid Email Address");
            inputEmail.requestFocus();
        }
        else if(password.length()<=6){
            inputPassword.setError("Password should contain more than 6 characters");
            inputPassword.requestFocus();
        }
        else{
            doLogin(email,password);
        }
    }

    private void doLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                    isEmailVerified();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof FirebaseAuthInvalidCredentialsException){
                    inputPassword.setError("Invalid Password!");
                    inputPassword.requestFocus();
                }
                else if(e instanceof FirebaseAuthInvalidUserException){
                    inputEmail.setError("Email Not Registered!");
                    inputEmail.requestFocus();
                }
                else{
                    Toast.makeText(MainActivity.this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void isEmailVerified() {
        if(mAuth.getCurrentUser()!=null){
            boolean isEmailVerified = mAuth.getCurrentUser().isEmailVerified();
            if(isEmailVerified){
                sendToHome();
            }
            else{
                Toast.makeText(this, "Please verify your Email first", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendToHome() {
        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(this,MainActivity2.class);
        startActivity(intent);
        finish();
    }
}