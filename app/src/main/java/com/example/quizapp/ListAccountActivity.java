package com.example.quizapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.Adapter.AccountAdapter;
import com.example.quizapp.Models.AccountModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListAccountActivity extends AppCompatActivity {

    private RecyclerView rvAccount;
    private AccountAdapter accountAdapter;
    private List<AccountModel> accountList;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private ProgressDialog progressDialog;


    Dialog loadingDialog;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menu;
    View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_account);

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải dữ liệu...");
        progressDialog.setCancelable(false);
        progressDialog.show();



        rvAccount = findViewById(R.id.rvAccount);
        rvAccount.setLayoutManager(new LinearLayoutManager(this));

        accountList = new ArrayList<>();
        accountAdapter = new AccountAdapter(accountList);
        rvAccount.setAdapter(accountAdapter);

        loadAccountsFromFirebase();
    }

    private void loadAccountsFromFirebase() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                accountList.clear(); // Xóa dữ liệu cũ
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userName = dataSnapshot.child("userName").getValue(String.class);
                    String userEmail = dataSnapshot.child("userEmail").getValue(String.class);
                    String userPassword = dataSnapshot.child("userPassword").getValue(String.class);

                    accountList.add(new AccountModel(userName, userEmail, userPassword));
                }

                accountAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(ListAccountActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        drawerLayout = findViewById(R.id.mainAccount);
        navigationView = findViewById(R.id.navigationViewAccount);
        menu = findViewById(R.id.back);
        header = navigationView.getHeaderView(0);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawerLayout.isDrawerOpen(GravityCompat.START))
                {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else{
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.homeAdmin) {
                    Intent intent = new Intent(ListAccountActivity.this, AdminMainActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (item.getItemId() == R.id.accountAdmin) {
                    Intent intent = new Intent(ListAccountActivity.this, ListAccountActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
    }
}
