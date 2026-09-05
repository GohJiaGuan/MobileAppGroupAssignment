package com.example.assignmentgroup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        EdgeToEdgeUtil.apply(this);

        authManager = new AuthManager(this);

        TextView tvName = findViewById(R.id.tvProfileName);
        TextView tvEmail = findViewById(R.id.tvProfileEmail);
        tvName.setText(authManager.getCurrentUserName());
        tvEmail.setText(authManager.getCurrentUserEmail());

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            authManager.logout();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        BottomNavHelper.setup(this, BottomNavHelper.Tab.PROFILE);
    }
}
