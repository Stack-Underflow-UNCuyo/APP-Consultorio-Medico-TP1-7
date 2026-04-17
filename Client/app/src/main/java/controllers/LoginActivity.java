package controllers;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.compmovil.ejemplo01.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import business.security.TokenManager;
import business.services.AuthService;
import network.RetrofitClient;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailTIL, passwordTIL;
    private TextInputEditText email, password;
    private Button btnLogin;
    private TextView registerTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. IMPORTANTE: Inicializar Retrofit aquí también
        RetrofitClient.init(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        emailTIL = findViewById(R.id.til_email);
        passwordTIL = findViewById(R.id.til_password);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login_submit);
        registerTV = findViewById(R.id.tv_ir_registro);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(view -> attemptLogin());

        registerTV.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        if (!validateLoginInput()) {
            return;
        }

        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        Toast.makeText(this, "Ingresando...", Toast.LENGTH_SHORT).show();

        AuthService authService = new AuthService(this);
        authService.login(emailStr, passwordStr, new AuthService.OnLoginResult() {
            @Override
            public void onSuccess(String token, String role) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateLoginInput() {
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        emailTIL.setError(null);
        passwordTIL.setError(null);

        if (TextUtils.isEmpty(emailStr)) {
            emailTIL.setError("El email es obligatorio");
            return false;
        }

        if (TextUtils.isEmpty(passwordStr)) {
            passwordTIL.setError("La contraseña es obligatoria");
            return false;
        }

        return true;
    }
}