package com.example.assignmentgroup;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SavedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);
        EdgeToEdgeUtil.apply(this);
        BottomNavHelper.setup(this, BottomNavHelper.Tab.SAVED);
    }
}
