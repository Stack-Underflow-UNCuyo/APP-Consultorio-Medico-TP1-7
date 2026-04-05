package com.compmovil.ejemplo01.controllers;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.compmovil.ejemplo01.R;
import com.compmovil.ejemplo01.ui.medics.MedicFragment;
import com.compmovil.ejemplo01.ui.mis_turnos.TurnosFragment;
import com.compmovil.ejemplo01.ui.patients.PatientFragment;
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
        setContentView(R.layout.activity_main);

        toolbar   = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottom_navigation);

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
        android.content.Intent intent = new android.content.Intent(this, LandingActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}