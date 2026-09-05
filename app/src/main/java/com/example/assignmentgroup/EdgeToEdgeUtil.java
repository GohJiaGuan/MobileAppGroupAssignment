package com.example.assignmentgroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Applies consistent system-bar padding across every screen so content and the
// bottom nav bar sit at the same position relative to the status/gesture bars.
public class EdgeToEdgeUtil {
    public static void apply(AppCompatActivity activity) {
        EdgeToEdge.enable(activity);
        ViewCompat.setOnApplyWindowInsetsListener(activity.findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
