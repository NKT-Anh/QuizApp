package com.example.quizapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.Models.QuestionsModel;
import com.example.quizapp.databinding.ActivityUploadQuestionBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

public class UploadQuestionActivity extends AppCompatActivity {

    ActivityUploadQuestionBinding binding;
    FirebaseDatabase database;
    RadioGroup options;
    LinearLayout answers;
    Dialog loadingDialog;

    private  String catId;
    private  String subCatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityUploadQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        catId = getIntent().getStringExtra("catId");
        subCatId = getIntent().getStringExtra("subCatId");

        database = FirebaseDatabase.getInstance();

        options = findViewById(R.id.options);
        answers = findViewById(R.id.answers);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCancelable(true);

        binding.btnUploadQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int correct = -1;
                for(int i = 0;i<options.getChildCount();i++){
                    EditText cauhoi =(EditText) answers.getChildAt(i);
                    if(cauhoi.getText().toString().isEmpty()){
                        cauhoi.setError("Nhập câu hỏi");
                        return;

                    }
                    RadioButton rad =(RadioButton) options.getChildAt(i);
                    if(rad.isChecked()){
                        correct=i;
                        break;
                    }
                }
                if(correct == -1){
                    Toast.makeText(UploadQuestionActivity.this,"Chọn đáp án đúng",Toast.LENGTH_SHORT).show();
                    return;
                }
                loadingDialog.show();

                QuestionsModel model = new QuestionsModel();
                model.setQuestion(binding.editQuestion.getText().toString());
                model.setOptionA(((EditText)answers.getChildAt(0)).getText().toString());
                model.setOptionB(((EditText)answers.getChildAt(1)).getText().toString());
                model.setOptionC(((EditText)answers.getChildAt(2)).getText().toString());
                model.setOptionD(((EditText)answers.getChildAt(3)).getText().toString());
                model.setCorrectAnswer(((EditText)answers.getChildAt(correct)).getText().toString());

                database.getReference().child("chuDe").child(catId)
                        .child("linhVuc").child(subCatId)
                        .child("cauHoi")
                        .push()
                        .setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                loadingDialog.dismiss();
                                Toast.makeText(UploadQuestionActivity.this,"Tạo thành công",Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingDialog.dismiss();
                                Toast.makeText(UploadQuestionActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

    }
}