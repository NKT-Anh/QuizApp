package com.example.quizapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.Models.AccountModel;
import com.example.quizapp.R;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private List<AccountModel> accountList;

    public AccountAdapter(List<AccountModel> accountList) {
        this.accountList = accountList;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_subcategory_design, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        AccountModel account = accountList.get(position);
        holder.tvUserEmail.setText(account.getUserEmail());

        holder.itemView.setOnClickListener(v -> {
            showAccountDetailsDialog(v, account);
        });
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserEmail;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserEmail = itemView.findViewById(R.id.subCategoryName);
        }
    }
    private void showAccountDetailsDialog(View view, AccountModel account) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Thông tin tài khoản");

        String message = "Tên người dùng: " + account.getUserName() + "\n" +
                "Email: " + account.getUserEmail()
               ;
        builder.setMessage(message);

        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
}
