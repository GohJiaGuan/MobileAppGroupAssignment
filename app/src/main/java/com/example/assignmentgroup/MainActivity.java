package com.example.assignmentgroup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {

    private AuthManager authManager;
    private TextView tvLoginProfile;
    private ViewPager2 tipsPager;
    private final Handler carouselHandler = new Handler(Looper.getMainLooper());
    private Runnable carouselTick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdgeUtil.apply(this);

        authManager = new AuthManager(this);
        tvLoginProfile = findViewById(R.id.tvLoginProfile);

        setupCarousel();
        setupFeatureCards();
        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLoginState();
    }

    private void refreshLoginState() {
        tvLoginProfile.setText(authManager.isLoggedIn() ? authManager.getCurrentUserName() : "Login");
    }

    private void setupCarousel() {
        tipsPager = findViewById(R.id.tipsPager);
        tipsPager.setAdapter(new TipsCarouselAdapter());

        carouselTick = () -> {
            int next = (tipsPager.getCurrentItem() + 1) % TipsCarouselAdapter.TIPS.length;
            tipsPager.setCurrentItem(next, true);
            carouselHandler.postDelayed(carouselTick, 3500);
        };
    }

    private void setupFeatureCards() {
        Button recycleButton = findViewById(R.id.button);
        Button encyclopediaButton = findViewById(R.id.button2);

        recycleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Function1.class));
            }
        });
        encyclopediaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        com.example.assignmentgroup.encyclopedia.EncyclopediaHomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupBottomNav() {
        BottomNavHelper.setup(this, BottomNavHelper.Tab.HOME);
        tvLoginProfile.setOnClickListener(v -> {
            if (authManager.isLoggedIn()) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else {
                startActivity(new Intent(this, RegisterAndLogin.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        carouselHandler.postDelayed(carouselTick, 3500);
    }

    @Override
    protected void onStop() {
        super.onStop();
        carouselHandler.removeCallbacks(carouselTick);
    }
}
