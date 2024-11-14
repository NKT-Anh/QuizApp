package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizapp.databinding.ActivityScoreBinding;

import java.util.concurrent.TimeUnit;

public class ScoreActivity extends AppCompatActivity {

    ActivityScoreBinding binding;
    private long time;
    private int question,correct,wrong,skip;
    private double score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        question = getIntent().getIntExtra("total_questions",0);
        wrong = getIntent().getIntExtra("wrong",0);
        time = getIntent().getLongExtra("time_taken",0);
        correct = getIntent().getIntExtra("correct",0);
        skip = question- (correct+wrong);
        String remainingTime = String.format("%02d:%02d min",
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time)-
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
        );
        score = ((double) correct / question) * 10;
        binding.txtQuestion.setText(question+"");
        binding.txtCorrect.setText(correct+"");
        binding.txtWrong.setText(wrong+"");
        binding.txtSkip.setText(skip+"");
        binding.time.setText(remainingTime+"");
        binding.score.setText(String.format("%.2f", score));

        binding.btnThuLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScoreActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        binding.btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScoreActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }
}