package com.example.quizapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ProgressDialog dialog;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        dialog = new ProgressDialog(SignInActivity.this);
        dialog.setTitle("Đang đăng nhập");
        dialog.setMessage("Chờ giây lát");

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                String email = binding.txtUserEmail.getText().toString().trim();
                String password = binding.txtUserPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String userId = task.getResult().getUser().getUid();
                                    database.getReference().child("users").child(userId)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String role = snapshot.child("role").getValue(String.class);
                                                    dialog.dismiss();


                                                    if ("admin".equals(role)) {
                                                        Intent intent = new Intent(SignInActivity.this, AdminMainActivity.class);
                                                        startActivity(intent);
                                                    } else if("user".equals(role)) {
                                                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                    }
                                                    else{
                                                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                    }
                                                    finish();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    dialog.dismiss();
                                                    Toast.makeText(SignInActivity.this, "Lỗi khi lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    dialog.dismiss();
                                    String errorMessage = "Tài khoản hoặc mật khẩu không đúng";
                                    if (task.getException() != null) {
                                        if (task.getException().getMessage().contains("The email address is badly formatted")) {
                                            errorMessage = "Địa chỉ email không hợp lệ";
                                        } else if (task.getException().getMessage().contains("There is no user record corresponding to this identifier")) {
                                            errorMessage = "Tài khoản không tồn tại";
                                        } else if (task.getException().getMessage().contains("The password is invalid or the user does not have a password")) {
                                            errorMessage = "Mật khẩu không đúng";
                                        }
                                    }
                                    Toast.makeText(SignInActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        binding.noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (currentUser != null) {
            String userId = currentUser.getUid();
            database.getReference().child("users").child(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String role = snapshot.child("role").getValue(String.class);


                            if ("admin".equals(role)) {
                                Intent intent = new Intent(SignInActivity.this, AdminMainActivity.class);
                                startActivity(intent);
                            } else if("user".equals(role)) {
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SignInActivity.this, "Lỗi khi lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
}
