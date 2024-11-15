package com.example.quizapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.Models.SubCategoryModel;
import com.example.quizapp.databinding.ActivityUploadSubCategoryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

public class UploadSubCategoryActivity extends AppCompatActivity {

    ActivityUploadSubCategoryBinding binding;
    FirebaseDatabase database;
    Dialog loadingDialog;
    private  String categoryID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadSubCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        categoryID = getIntent().getStringExtra("catId");

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCancelable(true);

        binding.btnUploadCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subCatName = binding.editCategorySubName.getText().toString();
                if(subCatName.isEmpty()){
                    binding.editCategorySubName.setError("Nhập tên danh mục");

                }
                else {
                    storeData(subCatName);
                }
            }
        });

    }

    private void storeData(String subCatName) {
        loadingDialog.show();
        SubCategoryModel model = new SubCategoryModel(subCatName);
        database.getReference().child("chuDe").child(categoryID)
                .child("linhVuc")
                .push()
                .setValue(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        loadingDialog.dismiss();
                        Toast.makeText(UploadSubCategoryActivity.this,"Tạo thành công",Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadSubCategoryActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}