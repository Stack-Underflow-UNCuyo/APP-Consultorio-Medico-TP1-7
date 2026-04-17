package controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.compmovil.ejemplo01.R;
import com.google.android.material.textfield.TextInputLayout;
import business.services.AuthService;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    TextInputLayout emailTIL;
    TextInputLayout passwordTIL;

    Button loginBtn;
    ImageButton backImgBtn;

    TextView registerTV;

    private AuthService authService;

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

        authService = new AuthService(this);

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
            if (!validateLoginInput()) {
                performLogin();
            };


        });
    }

    private boolean validateLoginInput() {
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();
        boolean isValid = true;

        // Limpiar errores previos
        emailTIL.setError(null);
        passwordTIL.setError(null);

        // LE PREGUNTAMOS AL SERVICIO (No lo validamos nosotros)
        if (!authService.isEmailValid(emailStr)) {
            emailTIL.setError("Ingrese un email válido");
            isValid = false;
        }

        if (passwordStr.isEmpty()) {
            passwordTIL.setError("La contraseña es obligatoria");
            isValid = false;
        } else if (!authService.isPasswordValid(passwordStr)) {
            passwordTIL.setError("La contraseña debe tener 8 caracteres, mayúsculas, números y símbolos");
            isValid = false;
        }

        return isValid;
    }

    private void performLogin(){
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        authService.login(emailStr, passwordStr, new AuthService.OnLoginResult() {
            @Override
            public void onSuccess(String role) {
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
    }

}
