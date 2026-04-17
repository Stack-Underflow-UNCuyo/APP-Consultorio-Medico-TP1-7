package controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

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

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private MaterialToolbar toolbar;

    private static final String TITULO_TURNOS    = "Turnos";
    private static final String TITULO_PACIENTES = "Pacientes";
    private static final String TITULO_MEDICOS   = "Médicos";
    private static final String TITULO_PERFIL    = "Mi Perfil";

    private static final String KEY_NAV_ITEM = "nav_item_seleccionado";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // verificar sesion antes de mostrar la UI
        TokenManager tokenManager = new TokenManager(this);
        if (!tokenManager.isLoggedIn()) {
            goToLanding();
            return;
        }

        // Inicializar Retrofit con el token guardado
        RetrofitClient.init(this);

        // Configurar tabs según el rol
        String role = tokenManager.getRole();
        setupNavigationForRole(role);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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

                }
//                else if (itemId == R.id.nav_perfil) {
//                    cargarFragmento(new PerfilFragment(), TITULO_PERFIL);
//                    return true;
//                }

                return false;
            }
        });

        if (savedInstanceState != null) {
            int itemId = savedInstanceState.getInt(KEY_NAV_ITEM, R.id.nav_turnos);
            bottomNav.setSelectedItemId(itemId);
        } else {
            bottomNav.setSelectedItemId(R.id.nav_turnos);
        }
    }

    private void goToLanding() {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupNavigationForRole(String role) {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if ("MEDIC".equals(role)) {
            // El médico ve: Turnos, Pacientes, Médicos
            bottomNav.getMenu().findItem(R.id.nav_pacientes).setVisible(true);
            bottomNav.getMenu().findItem(R.id.nav_medicos).setVisible(true);
        } else {
            // El paciente solo ve: Turnos
            bottomNav.getMenu().findItem(R.id.nav_pacientes).setVisible(false);
            bottomNav.getMenu().findItem(R.id.nav_medicos).setVisible(false);
        }

        // Cargar fragment inicial
        bottomNav.setSelectedItemId(R.id.nav_turnos);
    }

    private void cargarFragmento(Fragment fragment, String titulo) {
        toolbar.setTitle(titulo);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_NAV_ITEM, bottomNav.getSelectedItemId());
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_nuevo) {
            int navItem = bottomNav.getSelectedItemId();
            if (navItem == R.id.nav_turnos) {
                // TODO: abrir diálogo o Activity para nuevo turno
            } else if (navItem == R.id.nav_pacientes) {
                // TODO: abrir diálogo o Activity para nuevo paciente
            } else if (navItem == R.id.nav_medicos) {
                // TODO: abrir diálogo o Activity para nuevo médico
            }
            return true;

        } else if (id == R.id.action_cerrar_sesion) {
            cerrarSesion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cerrarSesion() {
        new TokenManager(this).clearSession();
        RetrofitClient.reset();
        goToLanding();
    }
}