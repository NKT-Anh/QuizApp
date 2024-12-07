package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.quizapp.databinding.ActivityScoreBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.concurrent.TimeUnit;

public class ScoreActivity extends AppCompatActivity {

    ActivityScoreBinding binding;
    private long time;
    private int question, correct, wrong, skip;
    private double score;
    String catId,subCatId,userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lấy thông tin người dùng đã đăng nhập
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        // Lấy catId và subCatId từ Intent

        catId = getIntent().getStringExtra("catId");
        subCatId = getIntent().getStringExtra("subCatId");


        question = getIntent().getIntExtra("total_questions", 0);
        wrong = getIntent().getIntExtra("wrong", 0);
        time = getIntent().getLongExtra("time_taken", 0);
        correct = getIntent().getIntExtra("correct", 0);
        skip = question - (correct + wrong);

        String remainingTime = String.format("%02d:%02d min",
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
        );

        score = ((double) correct / question) * 10;

        binding.txtQuestion.setText(question + "");
        binding.txtCorrect.setText(correct + "");
        binding.txtWrong.setText(wrong + "");
        binding.txtSkip.setText(skip + "");
        binding.time.setText(remainingTime + "");
        binding.score.setText(String.format("%.2f", score));



        // Kiểm tra và log giá trị của các biến
        Log.d("ScoreActivity", "catId: " + (catId != null ? catId : "null"));
        Log.d("ScoreActivity", "subCatId: " + (subCatId != null ? subCatId : "null"));
        Log.d("ScoreActivity", "userId: " + (userId != null ? userId : "null"));

        // Kiểm tra các giá trị có null không
        if (catId == null || subCatId == null || userId == null) {
            // Hiển thị lỗi hoặc thông báo nếu có bất kỳ giá trị nào là null
            Log.e("ScoreActivity", "catId, subCatId, or userId is null");
            Toast.makeText(ScoreActivity.this, "Dữ liệu bị thiếu. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            return; // Dừng lại không thực hiện tiếp tục nếu dữ liệu bị thiếu
        }

        // Kiểm tra và lưu điểm số vào Firebase nếu người dùng đã đăng nhập
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child("chuDe").child(catId)
                .child("linhVuc").child(subCatId)
                .child("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Nếu người dùng chưa có, tạo mới và lưu điểm số
                        if (!dataSnapshot.exists()) {
                            // Người dùng chưa có, tạo mới và lưu thông tin
                            database.child("chuDe").child(catId)
                                    .child("linhVuc").child(subCatId)
                                    .child("users").child(userId)
                                    .child("score").setValue(score);
                            database.child("chuDe").child(catId)
                                    .child("linhVuc").child(subCatId)
                                    .child("users").child(userId)
                                    .child("time").setValue(remainingTime);
                            database.child("chuDe").child(catId)
                                    .child("linhVuc").child(subCatId)
                                    .child("users").child(userId)
                                    .child("correct").setValue(correct);
                        } else {
                            // Người dùng đã có thông tin, chỉ cập nhật
                            database.child("chuDe").child(catId)
                                    .child("linhVuc").child(subCatId)
                                    .child("users").child(userId)
                                    .child("score").setValue(score);
                            database.child("chuDe").child(catId)
                                    .child("linhVuc").child(subCatId)
                                    .child("users").child(userId)
                                    .child("time").setValue(remainingTime);
                            database.child("chuDe").child(catId)
                                    .child("linhVuc").child(subCatId)
                                    .child("users").child(userId)
                                    .child("correct").setValue(correct);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ScoreActivity.this, "Lỗi lưu", Toast.LENGTH_SHORT).show();
                    }
                });

        binding.btnThuLai.setOnClickListener(view -> {
            Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        binding.btnExit.setOnClickListener(view -> {
            Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }
}
