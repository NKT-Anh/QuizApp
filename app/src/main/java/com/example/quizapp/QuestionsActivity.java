package com.example.quizapp;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizapp.Models.QuestionsModel;
import com.example.quizapp.databinding.ActivityQuestionsBinding;
import com.google.android.material.color.utilities.Score;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class QuestionsActivity extends AppCompatActivity {

    ActivityQuestionsBinding binding;
    FirebaseDatabase database;
    ArrayList<QuestionsModel>list;

    private  int count=0;
    private  int position = 0;
    private  int correct = 0;
    private  int wrong = 0;
    private long questionTime =450;
    private long timeLeft ;
    private String catId , subCatId;
    CountDownTimer timer;
    Dialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        list = new ArrayList<>();


        catId = getIntent().getStringExtra("catId");
        subCatId = getIntent().getStringExtra("subCatId");

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.show();

        startTimer();

        database.getReference().child("chuDe").child(catId).child("linhVuc").child(subCatId)
                .child("cauHoi").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){Toast.makeText(QuestionsActivity.this, " thúc", Toast.LENGTH_SHORT).show();}
                    if(snapshot.exists())
                    {
                        timer.start();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            QuestionsModel model = dataSnapshot.getValue(QuestionsModel.class);
                            if (model != null) {
                                model.setKey(dataSnapshot.getKey());
                                list.add(model);
                                loadingDialog.dismiss();
                            }

                        }
                        if(list.size()>0){
                            for (int i =0; i<4;i++){
                                binding.questionContent.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        checkAnswer((Button)view);
                                    }
                                });
                            }

                            playAnimation(binding.txtQuestion,0,list.get(position).getQuestion());

                            binding.btnNext.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    enableOption(true);
                                    position++;
                                    if(position==list.size()){
                                        long totalTime =questionTime*60*100;
                                        Intent intent = new Intent(QuestionsActivity.this,ScoreActivity.class);
                                        intent.putExtra("time_taken",totalTime-timeLeft);
                                        intent.putExtra("correct",correct);
                                        intent.putExtra("wrong",wrong);
                                        intent.putExtra("total_questions",list.size());
                                        startActivity(intent);
                                        finish();
                                        Toast.makeText(QuestionsActivity.this, "Ấn hoàn thành để kết thúc", Toast.LENGTH_SHORT).show();
                                        return;

                                    }
                                    count=0;
                                    playAnimation(binding.txtQuestion,0,list.get(position).getQuestion());
                                }
                            });

                            binding.button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    timer.cancel();
                                    long totalTime =questionTime*60*100;
                                    Intent intent = new Intent(QuestionsActivity.this,ScoreActivity.class);
                                    intent.putExtra("time_taken",totalTime-timeLeft);
                                    intent.putExtra("correct",correct);
                                    intent.putExtra("wrong",wrong);
                                    intent.putExtra("total_questions",list.size());
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                    else {
                        loadingDialog.dismiss();
                        Toast.makeText(QuestionsActivity.this, "Hiện tại chưa có câu hỏi", Toast.LENGTH_SHORT).show();
                    }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void startTimer() {
        long time = questionTime*60*100;
        timer =new CountDownTimer(time +1000,1000) {
            @Override
            public void onTick(long l) {
                timeLeft= l;
                String remainingTime = String.format("%02d:%02d min",
                        TimeUnit.MILLISECONDS.toMinutes(l),
                        TimeUnit.MILLISECONDS.toSeconds(l)-
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l))
                        );
                binding.time.setText(remainingTime);
            }

            @Override
            public void onFinish() {
                endQuiz();
            }
        };
        timer.start();

    }
    private void endQuiz() {
        timer.cancel();
        long totalTime = questionTime * 60 * 100;
        Intent intent = new Intent(QuestionsActivity.this, ScoreActivity.class);
        intent.putExtra("time_taken", totalTime - timeLeft);
        intent.putExtra("correct", correct);
        intent.putExtra("wrong", wrong);
        intent.putExtra("total_questions", list.size());
        startActivity(intent);
        finish();
    }

    private void enableOption(boolean enable) {

        for(int i=0;i<4;i++){
            binding.questionContent.getChildAt(i).setEnabled(enable);
            if(enable){
                binding.questionContent.getChildAt(i).setBackgroundResource(R.drawable.btn_option_bg);
            }
        }
    }

    private void playAnimation(View view, int value, final String data) {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {
                        if(value==0 && count <4){
                            String option ="";
                            if(count==0){
                                option = list.get(position).getOptionA();

                            } else if (count ==1 ) {
                                option = list.get(position).getOptionB();
                            }
                            else if (count ==2 ) {
                                option = list.get(position).getOptionC();
                            }
                            else if (count ==3 ) {
                                option = list.get(position).getOptionD();
                            }
                            playAnimation(binding.questionContent.getChildAt(count),0,option);
                            count++;
                        }
                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        if(value==0){
                            try {
                                ((TextView)view).setText(data);
                                binding.questionCount.setText(position+1+"/"+list.size());

                            }catch (Exception e){
                                ((Button)view).setText(data);
                            }
                            view.setTag(data);
                            playAnimation(view,1,data);
                        }
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {

                    }
                });
    }

    private void checkAnswer(Button selectOption) {

        enableOption(false);

        if(selectOption.getText().toString().equals(list.get(position).getCorrectAnswer())){
            correct++;
            selectOption.setBackgroundResource(R.drawable.correct_option_bg);
        }
        else {
            wrong++;
            selectOption.setBackgroundResource(R.drawable.wrong_option_bg);

            Button correctOption = (Button) binding.questionContent.findViewWithTag(list.get(position).getCorrectAnswer());
            correctOption.setBackgroundResource(R.drawable.correct_option_bg);
        }

    }
}