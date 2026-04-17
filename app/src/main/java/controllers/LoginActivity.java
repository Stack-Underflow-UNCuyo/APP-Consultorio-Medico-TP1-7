package controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.compmovil.ejemplo01.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import business.services.AuthService;

public class LoginActivity extends AppCompatActivity {

    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    EditText email, password;
    TextInputLayout emailTIL;
    TextInputLayout passwordTIL;

    Button loginBtn;
    ImageButton backImgBtn;

    TextView registerTV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);

        emailTIL = findViewById(R.id.til_email);
        passwordTIL = findViewById(R.id.til_password);

        loginBtn = findViewById(R.id.btn_login_submit);
        registerTV = findViewById(R.id.tv_ir_registro);
        backImgBtn = findViewById(R.id.btn_back);

        registerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        backImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loginBtn.setOnClickListener(view -> {
            if (!validateLoginInput()) return;

            String emailStr    = email.getText().toString().trim();
            String passwordStr = password.getText().toString().trim();

            AuthService authService = new AuthService(LoginActivity.this);
            authService.login(emailStr, passwordStr, new AuthService.OnLoginResult() {
                @Override
                public void onSuccess(String role) {
                    // ir al MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    public boolean validateLoginInput(){
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        if (emailStr.isEmpty()) {
            emailTIL.setError("Email address is required");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            emailTIL.setError("Please enter a valid email address");
            return false;
        }

        if (passwordStr.isEmpty()) {
            passwordTIL.setError("Password is required");
            return false;
        }

        if (!isValidPassword(passwordStr)) {
            passwordTIL.setError("Password must be at least 8 characters long and contain uppercase, lowercase, digit, and special character.");
            return false;
        }

        return true;

    }

    public boolean isValidPassword(final String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
