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

import com.example.quizapp.AdminQuestionsActivity;
import com.example.quizapp.Models.SubCategoryModel;
import com.example.quizapp.QuestionsActivity;
import com.example.quizapp.R;
import com.example.quizapp.databinding.RvSubcategoryDesignBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdminSubCategoryAdaper extends  RecyclerView.Adapter<AdminSubCategoryAdaper.ViewHoler> {
    Context context;
    ArrayList<SubCategoryModel> list;
    private  String catId;
    private  String subCatId;

    public AdminSubCategoryAdaper(Context context, ArrayList<SubCategoryModel> list, String catId) {
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
                Intent intent = new Intent(context, AdminQuestionsActivity.class);
                intent.putExtra("catId",catId);
                intent.putExtra("subCatId",categoryModel.getKey());
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

                    FirebaseDatabase.getInstance().getReference().child("chuDe").child(catId)
                            .child("linhVuc")
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
        RvSubcategoryDesignBinding binding;

        public ViewHoler(@NonNull View itemView) {

            super(itemView);
            binding = RvSubcategoryDesignBinding.bind(itemView);
        }
    }

}
