package com.example.quizapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quizapp.Adapter.CategoryAdaper;
import com.example.quizapp.Adapter.QuestionAdaper;
import com.example.quizapp.Models.CategoryModel;
import com.example.quizapp.Models.QuestionsModel;
import com.example.quizapp.databinding.ActivityAdminQuestionsBinding;
import com.example.quizapp.databinding.ActivityMainBinding;
import com.example.quizapp.databinding.ActivityQuestionsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class AdminQuestionsActivity extends AppCompatActivity {
    ActivityAdminQuestionsBinding binding;
    FirebaseDatabase database;
    QuestionAdaper adapter;
    FirebaseStorage storage;
    ArrayList<QuestionsModel> list;
    Dialog loadingDialog;

    private  String catId;
    private  String subCatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        list = new ArrayList<>();

        catId = getIntent().getStringExtra("catId");
        subCatId = getIntent().getStringExtra("subCatId");

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.show();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvQuestions.setLayoutManager(layoutManager);
        adapter = new QuestionAdaper(this, list,catId,subCatId);
        binding.rvQuestions.setAdapter(adapter);


        database.getReference().child("chuDe").child(catId)
                .child("linhVuc").child(subCatId)
                .child("cauHoi").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                QuestionsModel model = dataSnapshot.getValue(QuestionsModel.class);
                                if (model != null && dataSnapshot.getKey() != null) {
                                    model.setKey(dataSnapshot.getKey());
                                    list.add(model);
                                }
                            }
                            adapter.notifyDataSetChanged();
                            loadingDialog.dismiss();
                            if (list.isEmpty()) {
                                Toast.makeText(AdminQuestionsActivity.this, "Hiện tại chưa có dữ liệuc", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(AdminQuestionsActivity.this, "Hiện tại chưa có dữ liệu", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingDialog.dismiss();
                        Toast.makeText(AdminQuestionsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        binding.uploadCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminQuestionsActivity.this, UploadQuestionActivity.class);
                intent.putExtra("catId",catId);
                intent.putExtra("subCatId",subCatId);
                startActivity(intent);
            }
        });
    }
}