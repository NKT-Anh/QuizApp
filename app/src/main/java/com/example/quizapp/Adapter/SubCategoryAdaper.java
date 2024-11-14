package com.example.quizapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.Models.SubCategoryModel;
import com.example.quizapp.QuestionsActivity;
import com.example.quizapp.R;
import com.example.quizapp.databinding.RvSubcategoryDesignBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SubCategoryAdaper extends  RecyclerView.Adapter<SubCategoryAdaper.ViewHoler> {
    Context context;
    ArrayList<SubCategoryModel> list;
    private  String catId;
    private  String subCatId;

    public SubCategoryAdaper(Context context, ArrayList<SubCategoryModel> list, String catId) {
        this.context = context;
        this.list = list;
        this.catId = catId;
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.rv_subcategory_design,parent,false);
        return new ViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoler holder, int position) {
            SubCategoryModel categoryModel = list.get(position);
            holder.binding.subCategoryName.setText(categoryModel.getCatagoryName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, QuestionsActivity.class);
                intent.putExtra("catId",catId);
                intent.putExtra("subCatId",categoryModel.getKey());
                context.startActivity(intent);
            }
        });

   }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class ViewHoler extends RecyclerView.ViewHolder {
        RvSubcategoryDesignBinding binding;

        public ViewHoler(@NonNull View itemView) {

            super(itemView);
            binding = RvSubcategoryDesignBinding.bind(itemView);
        }
    }

}
