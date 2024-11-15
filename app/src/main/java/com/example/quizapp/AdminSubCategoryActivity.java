package com.example.quizapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quizapp.Adapter.AdminSubCategoryAdaper;
import com.example.quizapp.Models.SubCategoryModel;
import com.example.quizapp.databinding.ActivityAdminSubCategoryBinding;
import com.example.quizapp.databinding.ActivitySubCategoryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class AdminSubCategoryActivity extends AppCompatActivity {
    ActivityAdminSubCategoryBinding binding;
    FirebaseDatabase database;
    AdminSubCategoryAdaper adapter;
    FirebaseStorage storage;
    ArrayList<SubCategoryModel> list;
    Dialog loadingDialog;
    private String categoryID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminSubCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        categoryID = getIntent().getStringExtra("catId");
        list = new ArrayList<>();

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.show();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvCategory.setLayoutManager(layoutManager);
        adapter = new AdminSubCategoryAdaper(this, list,categoryID);
        binding.rvCategory.setAdapter(adapter);


        database.getReference().child("chuDe").child(categoryID)
                .child("linhVuc").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        SubCategoryModel model = dataSnapshot.getValue(SubCategoryModel.class);
                        if (model != null && dataSnapshot.getKey() != null) {
                            model.setKey(dataSnapshot.getKey());
                            list.add(model);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    loadingDialog.dismiss();
                    if (list.isEmpty()) {
                        Toast.makeText(AdminSubCategoryActivity.this, "Không có dữ liệu về lĩnh vực", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(AdminSubCategoryActivity.this, "Hiện tại chưa có dữ liệu", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(AdminSubCategoryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.uploadCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminSubCategoryActivity.this, UploadSubCategoryActivity.class);
                intent.putExtra("catId",categoryID);
                startActivity(intent);
            }
        });
    }
}