package com.example.quizapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.quizapp.Adapter.AdminCategoryAdaper;
import com.example.quizapp.Models.CategoryModel;
import com.example.quizapp.databinding.ActivityAdminMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class AdminMainActivity extends AppCompatActivity {



    ActivityAdminMainBinding binding;
    FirebaseDatabase database;
    AdminCategoryAdaper adapter;
    FirebaseStorage storage;
    ArrayList<CategoryModel> list;
    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        list = new ArrayList<>();

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.show();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.rvCategory.setLayoutManager(layoutManager);
        adapter = new AdminCategoryAdaper(this, list);
        binding.rvCategory.setAdapter(adapter);


        database.getReference().child("chuDe").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        CategoryModel model = dataSnapshot.getValue(CategoryModel.class);
                        if (model != null && dataSnapshot.getKey() != null) {
                            model.setKey(dataSnapshot.getKey());
                            list.add(model);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    loadingDialog.dismiss();
                    if (list.isEmpty()) {
                        Toast.makeText(AdminMainActivity.this, "Hiện tại chưa có dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(AdminMainActivity.this, "Hiện tại chưa có dữ liệu", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(AdminMainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AdminMainActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.uploadCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainActivity.this, UploadCategoryActivity.class);
                startActivity(intent);
            }
        });
    }
}
