package controllers;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.compmovil.ejemplo01.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import business.entities.UserDTO;
import business.security.TokenManager;
import business.services.AuthService;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilNombre, tilApellido, tilDni, tilTelefono, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etNombre, etApellido, etDni, etTelefono, etEmail, etPassword, etConfirmPassword;
    private MaterialCardView cardMedico, cardPaciente;
    private RadioButton rbMedico, rbPaciente;
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

        rbMedico = findViewById(R.id.rb_medico);
        rbPaciente = findViewById(R.id.rb_paciente);
        cardMedico = findViewById(R.id.card_medico);
        cardPaciente = findViewById(R.id.card_paciente);

        cbTerminos = findViewById(R.id.cb_terminos);
        btnRegister = findViewById(R.id.btn_register_submit);

        ImageButton btnBack = findViewById(R.id.btn_back);
        TextView tvIrLogin = findViewById(R.id.tv_ir_login);

        btnBack.setOnClickListener(v -> finish());
        tvIrLogin.setOnClickListener(v -> finish());
    }

    private void setupListeners() {
        // Gestión manual de selección para Médico
        cardMedico.setOnClickListener(v -> selectRole(true));
        rbMedico.setOnClickListener(v -> selectRole(true));

        // Gestión manual de selección para Paciente
        cardPaciente.setOnClickListener(v -> selectRole(false));
        rbPaciente.setOnClickListener(v -> selectRole(false));

        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    /**
     * Gestiona la exclusividad de la selección y actualiza la UI
     */
    private void selectRole(boolean isMedic) {
        // Actualizar RadioButtons
        rbMedico.setChecked(isMedic);
        rbPaciente.setChecked(!isMedic);

        // Colores
        int colorSeleccionado = Color.parseColor("#1A73E8");
        int colorDefault = Color.parseColor("#D0DEEC");
        int fondoSeleccionado = Color.parseColor("#F0F7FF");
        int fondoDefault = Color.parseColor("#FFFFFF");

        // Aplicar estilos a tarjetas
        if (isMedic) {
            cardMedico.setStrokeColor(colorSeleccionado);
            cardMedico.setCardBackgroundColor(fondoSeleccionado);
            cardPaciente.setStrokeColor(colorDefault);
            cardPaciente.setCardBackgroundColor(fondoDefault);
        } else {
            cardPaciente.setStrokeColor(colorSeleccionado);
            cardPaciente.setCardBackgroundColor(fondoSeleccionado);
            cardMedico.setStrokeColor(colorDefault);
            cardMedico.setCardBackgroundColor(fondoDefault);
        }
    }

    private void attemptRegister() {
        resetErrors();

        // VALIDACIÓN DE ROL: Ahora chequeamos los botones directamente
        if (!rbMedico.isChecked() && !rbPaciente.isChecked()) {
            Toast.makeText(this, "Por favor, seleccioná un rol", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = rbMedico.isChecked() ? "MEDIC" : "PATIENT";
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String dni = etDni.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(nombre)) { tilNombre.setError("Requerido"); isValid = false; }
        if (TextUtils.isEmpty(apellido)) { tilApellido.setError("Requerido"); isValid = false; }
        if (TextUtils.isEmpty(dni)) { tilDni.setError("Requerido"); isValid = false; }

        if (!authService.isEmailValid(email)) {
            tilEmail.setError("Email inválido");
            isValid = false;
        }

        if (!authService.isPasswordValid(password)) {
            tilPassword.setError("No cumple con los requisitos de seguridad");
            isValid = false;
        }

        if (!password.equals(confirm)) {
            tilConfirmPassword.setError("Las contraseñas no coinciden");
            isValid = false;
        }

        if (!cbTerminos.isChecked()) {
            Toast.makeText(this, "Aceptá los términos", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (!isValid) return;

        UserDTO newUser = new UserDTO(nombre, apellido, dni, telefono, email, password, role);
        newUser.setRole(role);

        authService.register(newUser, new AuthService.OnRegisterResult() {
            @Override
            public void onSuccess() {
                Toast.makeText(RegisterActivity.this, "¡Cuenta creada exitosamente!", Toast.LENGTH_SHORT).show();

                TokenManager tokenManager = new TokenManager(RegisterActivity.this);
                Intent intent;

                if (tokenManager.isLoggedIn()) {
                    intent = new Intent(RegisterActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(RegisterActivity.this, LoginActivity.class);
                }

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();
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