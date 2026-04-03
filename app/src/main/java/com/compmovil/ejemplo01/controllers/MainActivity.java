package com.compmovil.ejemplo01.controllers;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.compmovil.ejemplo01.R;
import com.compmovil.ejemplo01.fragments.MedicosFragment;
import com.compmovil.ejemplo01.fragments.PacientesFragment;
import com.compmovil.ejemplo01.fragments.PerfilFragment;
import com.compmovil.ejemplo01.fragments.TurnosFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * HomeActivity
 * ------------------------------------------------------------------
 * Activity principal post-login. Contiene el BottomNavigationView
 * y un FragmentContainerView donde se cargan los 4 fragmentos:
 *   - TurnosFragment    (pantalla de inicio)
 *   - PacientesFragment
 *   - MedicosFragment
 *   - PerfilFragment
 *
 * Cómo se usa desde LoginActivity:
 *   Intent intent = new Intent(this, HomeActivity.class);
 *   startActivity(intent);
 *   finish(); // para que el usuario no vuelva al login con el botón Atrás
 * ------------------------------------------------------------------
 */
public class MainActivity extends AppCompatActivity {

    // ── Componentes de UI ──────────────────────────────────────────
    private BottomNavigationView bottomNav;
    private MaterialToolbar toolbar;

    // ── Títulos del Toolbar según fragmento activo ─────────────────
    private static final String TITULO_TURNOS    = "Turnos";
    private static final String TITULO_PACIENTES = "Pacientes";
    private static final String TITULO_MEDICOS   = "Médicos";
    private static final String TITULO_PERFIL    = "Mi Perfil";

    // ── Clave para guardar el ítem seleccionado ante rotación ──────
    private static final String KEY_NAV_ITEM = "nav_item_seleccionado";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Enlazar vistas
        toolbar   = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottom_navigation);

        // 2. Configurar Toolbar como ActionBar de soporte
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 3. Listener del BottomNavigationView
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_turnos) {
                    cargarFragmento(new TurnosFragment(), TITULO_TURNOS);
                    return true;

                } else if (itemId == R.id.nav_pacientes) {
                    cargarFragmento(new PacientesFragment(), TITULO_PACIENTES);
                    return true;

                } else if (itemId == R.id.nav_medicos) {
                    cargarFragmento(new MedicosFragment(), TITULO_MEDICOS);
                    return true;

                } else if (itemId == R.id.nav_perfil) {
                    cargarFragmento(new PerfilFragment(), TITULO_PERFIL);
                    return true;
                }

                return false;
            }
        });

        // 4. Determinar qué fragmento mostrar al iniciar (o restaurar tras rotación)
        if (savedInstanceState != null) {
            // Restaurar ítem seleccionado antes de rotar pantalla
            int itemId = savedInstanceState.getInt(KEY_NAV_ITEM, R.id.nav_turnos);
            bottomNav.setSelectedItemId(itemId);
        } else {
            // Primera apertura: mostrar Turnos por defecto
            bottomNav.setSelectedItemId(R.id.nav_turnos);
        }
    }

    /**
     * Reemplaza el fragmento en el FragmentContainerView y actualiza el título del Toolbar.
     *
     * @param fragment El fragmento a mostrar
     * @param titulo   El título que aparecerá en el Toolbar
     */
    private void cargarFragmento(Fragment fragment, String titulo) {
        // Actualizar título del toolbar
        toolbar.setTitle(titulo);

        // Reemplazar fragmento con transacción
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    /**
     * Guardar el ítem seleccionado para sobrevivir rotaciones de pantalla.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_NAV_ITEM, bottomNav.getSelectedItemId());
    }

    /**
     * Manejo del menú del Toolbar (ícono "+" y overflow "Cerrar sesión")
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_nuevo) {
            // Delegar la acción "Nuevo" al fragmento activo según el destino actual
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

    /**
     * Cierra la sesión y vuelve a la LandingActivity.
     * Limpia el back stack para que el usuario no pueda volver con Atrás.
     */
    private void cerrarSesion() {
        // TODO: limpiar SharedPreferences o token de sesión si los usás
        android.content.Intent intent = new android.content.Intent(this, LandingActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}