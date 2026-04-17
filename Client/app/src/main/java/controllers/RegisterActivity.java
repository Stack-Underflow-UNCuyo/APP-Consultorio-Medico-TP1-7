package controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.compmovil.ejemplo01.R;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import business.entities.UserDTO;
import business.services.AuthService;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilNombre, tilApellido, tilDni, tilTelefono, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etNombre, etApellido, etDni, etTelefono, etEmail, etPassword, etConfirmPassword;
    private MaterialCheckBox cbTerminos;
    private Button btnRegister;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authService = new AuthService(this);
        initViews();
        setupListeners();
    }

    private void initViews() {
        tilNombre = findViewById(R.id.til_nombre);
        tilApellido = findViewById(R.id.til_apellido);
        tilDni = findViewById(R.id.til_dni);
        tilTelefono = findViewById(R.id.til_telefono);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);

        etNombre = findViewById(R.id.et_nombre);
        etApellido = findViewById(R.id.et_apellido);
        etDni = findViewById(R.id.et_dni);
        etTelefono = findViewById(R.id.et_telefono);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        cbTerminos = findViewById(R.id.cb_terminos);
        btnRegister = findViewById(R.id.btn_register_submit);

        ImageButton btnBack = findViewById(R.id.btn_back);
        TextView tvIrLogin = findViewById(R.id.tv_ir_login);

        btnBack.setOnClickListener(v -> finish());
        tvIrLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        resetErrors();

        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();

        boolean isValid = true;

        if (etNombre.getText().toString().isEmpty()) { tilNombre.setError("Obligatorio"); isValid = false; }
        if (!authService.isEmailValid(email)) { tilEmail.setError("Email inválido"); isValid = false; }
        if (!authService.isPasswordValid(pass)) { tilPassword.setError("Mínimo 8 caracteres"); isValid = false; }
        if (!authService.doPasswordsMatch(pass, confirm)) { tilConfirmPassword.setError("No coinciden"); isValid = false; }

        if (!cbTerminos.isChecked()) {
            Toast.makeText(this, "Aceptá los términos", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (!isValid) return;

        UserDTO user = new UserDTO();
        user.setName(etNombre.getText().toString().trim());
        user.setLastName(etApellido.getText().toString().trim());
        user.setDni(String.valueOf(Integer.parseInt(etDni.getText().toString().trim())));
        user.setPhone(etTelefono.getText().toString().trim());
        user.setEmail(email);
        user.setPassword(pass);
        user.setActive(true);

        authService.register(user, new AuthService.OnRegisterResult() {
            @Override
            public void onSuccess() {
                Toast.makeText(RegisterActivity.this, "¡Registro Exitoso!", Toast.LENGTH_LONG).show();
                finish(); // Volver al login
            }

            @Override
            public void onError(String message) {
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void resetErrors() {
        tilNombre.setError(null); tilApellido.setError(null); tilDni.setError(null);
        tilTelefono.setError(null); tilEmail.setError(null);
        tilPassword.setError(null); tilConfirmPassword.setError(null);
    }
}