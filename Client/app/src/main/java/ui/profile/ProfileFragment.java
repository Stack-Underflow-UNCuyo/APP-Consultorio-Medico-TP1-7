package ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.compmovil.ejemplo01.R;

import business.security.TokenManager;
import controllers.LandingActivity;
import network.RetrofitClient;

public class ProfileFragment extends Fragment {

    private TokenManager tokenManager;
    private TextView tvEmail, tvRole, tvNombre, tvAvatar;
    private LinearLayout btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tokenManager = new TokenManager(requireContext());

        initViews(view);
        loadUserData();
        setupLogout();

        return view;
    }

    private void initViews(View view) {
        tvEmail = view.findViewById(R.id.tv_email_perfil);
        tvRole = view.findViewById(R.id.tv_dni_perfil);
        tvNombre = view.findViewById(R.id.tv_nombre_perfil);
        tvAvatar = view.findViewById(R.id.tv_avatar_perfil);
        btnLogout = view.findViewById(R.id.option_cerrar_sesion);
    }

    private void loadUserData() {
        String email = tokenManager.getEmail();
        String role = tokenManager.getRole();
        String name = tokenManager.getName();

        if (name != null) {
            tvNombre.setText(name);
            // Usar iniciales del nombre para el avatar
            if (name.length() >= 2) {
                tvAvatar.setText(name.substring(0, 2).toUpperCase());
            } else if (name.length() == 1) {
                tvAvatar.setText(name.toUpperCase());
            }
        } else if (email != null) {
            tvNombre.setText("Usuario");
            if (email.length() >= 2) {
                tvAvatar.setText(email.substring(0, 2).toUpperCase());
            }
        }

        if (email != null) {
            tvEmail.setText(email);
        }

        if (role != null) {
            tvRole.setText("Rol: " + role);
        }
    }

    private void setupLogout() {
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                tokenManager.clearSession();
                RetrofitClient.reset();

                Intent intent = new Intent(requireContext(), LandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().finish();
                }
            });
        }
    }
}
