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

import com.example.quizapp.AdminSubCategoryActivity;
import com.example.quizapp.Models.CategoryModel;
import com.example.quizapp.R;
import com.example.quizapp.SubCategoryActivity;
import com.example.quizapp.databinding.RvCategoryDesignBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdminCategoryAdaper extends  RecyclerView.Adapter<AdminCategoryAdaper.ViewHoler> {
    Context context;
    ArrayList<CategoryModel> list;
    private  String categoryID;

    public AdminCategoryAdaper(Context context, ArrayList<CategoryModel> models) {
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

                    imageUrl = "android.resource://" + context.getPackageName() + "/" + R.drawable.admin_logo;
                }

                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.admin_logo)
                        .into(holder.binding.categoryImage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, AdminSubCategoryActivity.class);
                        intent.putExtra("catId",categoryModel.getKey());
                        context.startActivity(intent);
                    }
                });
                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Xóa câu hỏi");
                            builder.setMessage("Bạn có chắc là xóa câu hỏi không ?");
                            builder.setPositiveButton("Có",((dialogInterface, i) -> {

                                FirebaseDatabase.getInstance().getReference().child("chuDe")
                                        .child(categoryModel.getKey())
                                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context,"Xóa thành công",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                ;

                            }));

                            builder.setNegativeButton("Không",(dialogInterface, i) -> {

                                dialogInterface.cancel();
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                            return true;
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
