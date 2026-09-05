package com.example.assignmentgroup;

import android.content.Intent;
import android.graphics.Typeface;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class BottomNavHelper {

    public enum Tab { HOME, HISTORY, SAVED, PROFILE }

    public static void setup(AppCompatActivity activity, Tab active) {
        AuthManager authManager = new AuthManager(activity);

        TextView home = activity.findViewById(R.id.navHome);
        TextView history = activity.findViewById(R.id.navHistory);
        TextView saved = activity.findViewById(R.id.navSaved);
        TextView profile = activity.findViewById(R.id.navProfile);

        highlight(home, active == Tab.HOME);
        highlight(history, active == Tab.HISTORY);
        highlight(saved, active == Tab.SAVED);
        highlight(profile, active == Tab.PROFILE);

        home.setOnClickListener(v -> navigate(activity, MainActivity.class, active == Tab.HOME));
        history.setOnClickListener(v -> navigate(activity, HistoryActivity.class, active == Tab.HISTORY));
        saved.setOnClickListener(v -> navigate(activity, SavedActivity.class, active == Tab.SAVED));
        profile.setOnClickListener(v -> {
            if (active == Tab.PROFILE) return;
            if (authManager.isLoggedIn()) {
                navigate(activity, ProfileActivity.class, false);
            } else {
                Toast.makeText(activity, "Login to view your profile", Toast.LENGTH_SHORT).show();
                activity.startActivity(new Intent(activity, RegisterAndLogin.class));
            }
        });
    }

    private static void highlight(TextView tab, boolean active) {
        tab.setTextColor(ContextCompat.getColor(tab.getContext(), active ? R.color.primary_green : R.color.light_gray_text));
        tab.setTypeface(null, active ? Typeface.BOLD : Typeface.NORMAL);
    }

    private static void navigate(AppCompatActivity activity, Class<?> target, boolean isCurrent) {
        if (isCurrent) return;
        activity.startActivity(new Intent(activity, target));
        activity.finish();
    }
}
