package controllers;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.compmovil.ejemplo01.R;

import business.security.TokenManager;
import network.RetrofitClient;
import ui.medics.MedicFragment;
import ui.mis_turnos.TurnosFragment;
import ui.patients.PatientFragment;
import ui.profile.ProfileFragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private MaterialToolbar toolbar;

    private static final String TITULO_TURNOS    = "Turnos";
    private static final String TITULO_PACIENTES = "Pacientes";
    private static final String TITULO_MEDICOS   = "Médicos";
    private static final String TITULO_PERFIL    = "Perfil";
    private static final String KEY_NAV_ITEM = "nav_item_seleccionado";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottom_navigation);

        // verificar sesion antes de mostrar la UI
        TokenManager tokenManager = new TokenManager(this);
        if (!tokenManager.isLoggedIn()) {
            goToLanding();
            return;
        }

        // Inicializar Retrofit con el token guardado
        RetrofitClient.init(this);

        // Configurar el toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Configurar tabs según el rol
        String role = tokenManager.getRole();
        setupNavigationForRole(role);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_turnos) {
                cargarFragmento(new TurnosFragment(), TITULO_TURNOS);
                return true;
            } else if (itemId == R.id.nav_pacientes) {
                cargarFragmento(new PatientFragment(), TITULO_PACIENTES);
                return true;
            } else if (itemId == R.id.nav_medicos) {
                cargarFragmento(new MedicFragment(), TITULO_MEDICOS);
                return true;
            } else if (itemId == R.id.nav_perfil) {
                cargarFragmento(new ProfileFragment(), TITULO_PERFIL);
                return true;
            }
            return false;
        });

        // Restaurar estado o cargar fragment inicial
        if (savedInstanceState != null) {
            int itemId = savedInstanceState.getInt(KEY_NAV_ITEM, R.id.nav_turnos);
            bottomNav.setSelectedItemId(itemId);
        } else {
            bottomNav.setSelectedItemId(R.id.nav_turnos);
        }
    }

    private void setupNavigationForRole(String role) {
        if (bottomNav == null) return;

        if ("MEDIC".equals(role)) {
            bottomNav.getMenu().findItem(R.id.nav_pacientes).setVisible(true);
            bottomNav.getMenu().findItem(R.id.nav_medicos).setVisible(true);
        } else {
            bottomNav.getMenu().findItem(R.id.nav_pacientes).setVisible(false);
            bottomNav.getMenu().findItem(R.id.nav_medicos).setVisible(false);
        }
        // El perfil siempre debe ser visible
        bottomNav.getMenu().findItem(R.id.nav_perfil).setVisible(true);
    }

    private void cargarFragmento(Fragment fragment, String titulo) {
        if (toolbar != null) toolbar.setTitle(titulo);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    private void goToLanding() {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (bottomNav != null) {
            outState.putInt(KEY_NAV_ITEM, bottomNav.getSelectedItemId());
        }
    }
}
