package com.example.quizapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quizapp.Adapter.SubCategoryAdaper;
import com.example.quizapp.Models.SubCategoryModel;
import com.example.quizapp.databinding.ActivitySubCategoryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubCategoryActivity extends AppCompatActivity {

    ActivitySubCategoryBinding binding;
    FirebaseDatabase database;
    SubCategoryAdaper adapter;
    ArrayList<SubCategoryModel> list;
    Dialog loadingDialog;
    private String categoryID,categoryName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySubCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        categoryID = getIntent().getStringExtra("catId");
        categoryName = getIntent().getStringExtra("name");
        list = new ArrayList<>();

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.show();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvCategory.setLayoutManager(layoutManager);
        binding.toonBarTitle.setText(categoryName);

        adapter = new SubCategoryAdaper(this, list, categoryID);
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
                                Toast.makeText(SubCategoryActivity.this, "Không có dữ liệu về lĩnh vực", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(SubCategoryActivity.this, "Hiện tại chưa có dữ liệu", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingDialog.dismiss();
                        Toast.makeText(SubCategoryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}