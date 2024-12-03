package com.example.quizapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.Models.Question;
import com.example.quizapp.R;

public class QuestionPagerAdapter extends RecyclerView.Adapter<QuestionPagerAdapter.QuestionViewHolder>{
    private final Question[] questions;

    public QuestionPagerAdapter(Question[] questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questions[position];
        holder.question.setText(question.getQuestion());
        holder.option1.setText(question.getOption1());
        holder.option2.setText(question.getOption2());
        holder.option3.setText(question.getOption3());
        holder.option4.setText(question.getOption4());

        holder.option1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) question.setSelectedAnswer(1);
        });
        holder.option2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) question.setSelectedAnswer(2);
        });
        holder.option3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) question.setSelectedAnswer(3);
        });
        holder.option4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) question.setSelectedAnswer(4);
        });

        // Hiển thị câu trả lời đã chọn trước đó nếu có
        switch (question.getSelectedAnswer()) {
            case 1:
                holder.option1.setChecked(true);
                break;
            case 2:
                holder.option2.setChecked(true);
                break;
            case 3:
                holder.option3.setChecked(true);
                break;
            case 4:
                holder.option4.setChecked(true);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return questions.length;
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView question;
        RadioButton option1, option2, option3, option4;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            option1 = itemView.findViewById(R.id.option1);
            option2 = itemView.findViewById(R.id.option2);
            option3 = itemView.findViewById(R.id.option3);
            option4 = itemView.findViewById(R.id.option4);
        }
    }
}
