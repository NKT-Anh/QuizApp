package com.example.quizapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        dialog = new ProgressDialog (SignUpActivity.this);
        dialog.setTitle("Đang tạo tài khoản");
        dialog.setMessage("Tài khoản của bạn đang được tạo");

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                dialog.show();
                String email = binding.txtUserEmail.getText().toString().trim();
                String password = binding.txtUserPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.createUserWithEmailAndPassword(binding.txtUserEmail.getText().toString()
                        ,binding.txtUserPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    dialog.dismiss();
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("userName",binding.txtUserName.getText().toString());
                                    map.put("userEmail",binding.txtUserEmail.getText().toString());
                                    map.put("userPassword",binding.txtUserPassword.getText().toString());
                                    map.put("role", "user");
                                    String id = task.getResult().getUser().getUid();
                                    database.getReference().child("users").child(id).setValue(map);

                                    Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
                                    startActivity(intent);
                                }
                                else {

                                    Toast.makeText(SignUpActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        binding.loginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}