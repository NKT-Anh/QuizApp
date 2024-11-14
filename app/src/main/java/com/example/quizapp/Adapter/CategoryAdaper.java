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

import com.example.quizapp.Models.CategoryModel;
import com.example.quizapp.R;
import com.example.quizapp.SubCategoryActivity;
import com.example.quizapp.databinding.RvCategoryDesignBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryAdaper extends  RecyclerView.Adapter<CategoryAdaper.ViewHoler> {
    Context context;
    ArrayList<CategoryModel> list;
    private  String categoryID;

    public CategoryAdaper(Context context, ArrayList<CategoryModel> models) {
        this.context = context;
        this.list = models;
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.rv_category_design,parent,false);
        return new ViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoler holder, int position) {
            CategoryModel categoryModel = list.get(position);
            holder.binding.categoryName.setText(categoryModel.getCatagoryName());
                String imageUrl = categoryModel.getCatagoryImage();
                if (imageUrl == null || imageUrl.isEmpty()) {

                    imageUrl = "android.resource://" + context.getPackageName() + "/" + R.drawable.logo;
                }

                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.logo)
                        .into(holder.binding.categoryImage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, SubCategoryActivity.class);
                        intent.putExtra("catId",categoryModel.getKey());
                        intent.putExtra("name",categoryModel.getCatagoryName());
                        context.startActivity(intent);
                    }
                });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class ViewHoler extends RecyclerView.ViewHolder {
        RvCategoryDesignBinding binding;

        public ViewHoler(@NonNull View itemView) {

            super(itemView);
            binding = RvCategoryDesignBinding.bind(itemView);
        }
    }

}
