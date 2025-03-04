package ru.anonymus.simplemessenger;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ru.anonymus.simplemessenger.databinding.ActivityAdminBinding;

public class AdminActivity extends AppCompatActivity {

    ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.adminToolbar.btnAdminBack.setOnClickListener(view -> finish());

        binding.adminCreateInviteCodeBtn.setOnClickListener(view -> {
            startActivity(new Intent(AdminActivity.this, CreateInviteCodeActivity.class));
        });

    }

}