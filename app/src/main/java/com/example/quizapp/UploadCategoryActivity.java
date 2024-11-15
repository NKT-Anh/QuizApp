package com.example.quizapp;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.Models.CategoryModel;
import com.example.quizapp.databinding.ActivityUploadCategoryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UploadCategoryActivity extends AppCompatActivity {

    ActivityUploadCategoryBinding binding;
    FirebaseDatabase database;
    SQLiteDatabase sqLiteDatabase;
    Dialog loadingDialog;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCancelable(false);

        sqLiteDatabase = openOrCreateDatabase("QuizApp", MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS CategoryImages (id INTEGER PRIMARY KEY AUTOINCREMENT, image BLOB, categoryName TEXT)");

        binding.feachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        binding.btnUploadCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String categoryName = binding.editCategoryName.getText().toString();
                if (categoryName.isEmpty()) {
                    binding.editCategoryName.setError("Vui lòng nhập chủ đề");
                } else if (imageUri == null) {
                    Toast.makeText(UploadCategoryActivity.this, "Hãy chọn ảnh", Toast.LENGTH_SHORT).show();
                } else {
                    uploadData(categoryName, imageUri);
                }
            }

            private void uploadData(String categoryName, Uri imageUri) {
                loadingDialog.show();

                long imageId = saveImageToSQLite(categoryName, imageUri);
                if (imageId != -1) {
                    String imageLink = String.valueOf(imageId);
                    CategoryModel model = new CategoryModel(categoryName, imageLink);

                    database.getReference().child("chuDe")
                            .push()
                            .setValue(model)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(UploadCategoryActivity.this, "Upload thành công", Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismiss();
                                    onBackPressed();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingDialog.dismiss();
                                    Toast.makeText(UploadCategoryActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(UploadCategoryActivity.this, "Lỗi khi lưu ảnh vào SQLite", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            }

            private long saveImageToSQLite(String categoryName, Uri imageUri) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] imageBytes = byteArrayOutputStream.toByteArray();

                    sqLiteDatabase.execSQL("INSERT INTO CategoryImages (image, categoryName) VALUES (?, ?)", new Object[]{imageBytes, categoryName});

                    Cursor cursor = sqLiteDatabase.rawQuery("SELECT last_insert_rowid()", null);
                    cursor.moveToFirst();
                    long imageId = cursor.getLong(0);
                    cursor.close();

                    return imageId;
                } catch (IOException e) {
                    e.printStackTrace();
                    return -1;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                binding.categoryImage.setImageURI(imageUri);
            }
        }
    }
}
