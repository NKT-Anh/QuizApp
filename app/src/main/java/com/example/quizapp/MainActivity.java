package com.example.quizapp;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.quizapp.Adapter.CategoryAdaper;
import com.example.quizapp.Models.CategoryModel;
import com.example.quizapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    CategoryAdaper adapter;
    ArrayList<CategoryModel> list;
    Dialog loadingDialog;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menu;
    View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        list = new ArrayList<>();

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.show();

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.rvCategory.setLayoutManager(layoutManager);
        adapter = new CategoryAdaper(this, list);
        binding.rvCategory.setAdapter(adapter);


        database.getReference().child("chuDe").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        CategoryModel model = dataSnapshot.getValue(CategoryModel.class);
                        if (model != null && dataSnapshot.getKey() != null) {
                            model.setKey(dataSnapshot.getKey());
                            list.add(model);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    loadingDialog.dismiss();
                    if (list.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Hiện tại chưa có dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Hiện tại chưa có dữ liệu", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        drawerLayout = findViewById(R.id.drawLayout);
        navigationView = findViewById(R.id.navigationView);
        menu = findViewById(R.id.menu);
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
                String shareBody = "Chia sẻ app cho mình nha" +"http://play.google.com/store/app/details/id="+ MainActivity.this.getPackageName();
                if(item.getItemId()==R.id.Share) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);

                }
                else if (item.getItemId() == R.id.rate) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/app/details/id=" + MainActivity.this.getPackageName())));
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (item.getItemId() == R.id.privacy) {
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("link")));
                    drawerLayout.closeDrawer(GravityCompat.START);

                }
                else if(item.getItemId() == R.id.logout){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }

                return false;
            }
        });
    }
}