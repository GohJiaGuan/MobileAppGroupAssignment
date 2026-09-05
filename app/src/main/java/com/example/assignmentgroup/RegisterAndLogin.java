package com.example.assignmentgroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterAndLogin extends AppCompatActivity {

        LinearLayout loginLayout;
        LinearLayout registerLayout;

        TextView tvGoRegister;
        TextView tvGoLogin;

        Button btnLogin;
        Button btnRegister;

        EditText etLoginEmail;
        EditText etLoginPassword;

        EditText etRegisterName;
        EditText etRegisterEmail;
        EditText etRegisterPassword;
        EditText etRegisterConfirmPassword;

        AuthManager authManager;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login_register);
            EdgeToEdgeUtil.apply(this);

            authManager = new AuthManager(this);
            findViewById(R.id.btnBack).setOnClickListener(v -> finish());

            // Layouts
            loginLayout = findViewById(R.id.loginLayout);
            registerLayout = findViewById(R.id.registerLayout);

            // TextViews
            tvGoRegister = findViewById(R.id.tvGoRegister);
            tvGoLogin = findViewById(R.id.tvGoLogin);

            // Buttons
            btnLogin = findViewById(R.id.btnLogin);
            btnRegister = findViewById(R.id.btnRegister);

            // Login fields
            etLoginEmail = findViewById(R.id.etLoginEmail);
            etLoginPassword = findViewById(R.id.etLoginPassword);

            // Register fields
            etRegisterName = findViewById(R.id.etRegisterName);
            etRegisterEmail = findViewById(R.id.etRegisterEmail);
            etRegisterPassword = findViewById(R.id.etRegisterPassword);
            etRegisterConfirmPassword =
                    findViewById(R.id.etRegisterConfirmPassword);

//Register field
            tvGoRegister.setOnClickListener(v -> {

                loginLayout.setVisibility(View.GONE);
                registerLayout.setVisibility(View.VISIBLE);

            });
//Login field
            tvGoLogin.setOnClickListener(v -> {

                registerLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);

            });


//LOGIN BUTTON
            btnLogin.setOnClickListener(v -> {

                String email = etLoginEmail.getText().toString().trim();
                String password = etLoginPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    etLoginEmail.setError("Please enter your email");
                    return;
                }

                if (password.isEmpty()) {
                    etLoginPassword.setError("Please enter your password");
                    return;
                }

                if (!authManager.login(email, password)) {
                    Toast.makeText(RegisterAndLogin.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(
                        RegisterAndLogin.this,
                        "Login successful",
                        Toast.LENGTH_SHORT
                ).show();
                Intent intent = new Intent(RegisterAndLogin.this, MainActivity.class);
                startActivity(intent);
                finish();

            });


// REGISTER BUTTON
            btnRegister.setOnClickListener(v -> {

                String name = etRegisterName.getText().toString().trim();
                String email = etRegisterEmail.getText().toString().trim();
                String password =
                        etRegisterPassword.getText().toString().trim();
                String confirmPassword =
                        etRegisterConfirmPassword.getText().toString().trim();


                if (name.isEmpty()) {
                    etRegisterName.setError("Please enter your name");
                    return;
                }

                if (email.isEmpty()) {
                    etRegisterEmail.setError("Please enter your email");
                    return;
                }

                if (password.isEmpty()) {
                    etRegisterPassword.setError("Please enter a password");
                    return;
                }

                if (confirmPassword.isEmpty()) {
                    etRegisterConfirmPassword.setError(
                            "Please confirm your password"
                    );
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    etRegisterConfirmPassword.setError(
                            "Passwords do not match"
                    );
                    return;
                }

                if (!authManager.register(name, email, password)) {
                    etRegisterEmail.setError("An account with this email already exists");
                    return;
                }

                Toast.makeText(
                        RegisterAndLogin.this,
                        "Registration successful",
                        Toast.LENGTH_SHORT
                ).show();
                Intent intent = new Intent(RegisterAndLogin.this, MainActivity.class);
                startActivity(intent);
                finish();

            });
        }
    }

