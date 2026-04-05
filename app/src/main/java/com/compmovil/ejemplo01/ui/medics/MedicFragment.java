package com.compmovil.ejemplo01.ui.medics;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compmovil.ejemplo01.R;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import business.entities.MedicDTO;
import business.persistence.MedicDAO;

public class MedicFragment extends Fragment {
    private RecyclerView rvMedics;
    private LinearLayout layoutEmpty;
    private TextView tvCount;
    private FloatingActionButton fab;
    private TextInputEditText tiSearchBar;
    private ChipGroup chipGroupSpeciality;

    private MedicDAO medicDAO;
    private MedicAdapter medicAdapter;
    private List<MedicDTO> allMedics;

    private String currentSearchText = "";
    private String currentSpecialityFilter = "Todos";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_medic, container, false);

        rvMedics = view.findViewById(R.id.rv_medicos);
        layoutEmpty = view.findViewById(R.id.layout_empty_medicos);
        tvCount = view.findViewById(R.id.tv_count_medicos);
        fab = view.findViewById(R.id.fab_nuevo_medico);
        tiSearchBar = view.findViewById(R.id.et_buscar_medico);
        chipGroupSpeciality = view.findViewById(R.id.chipgroup_especialidad);

        rvMedics.setLayoutManager(new LinearLayoutManager(getContext()));
        medicDAO = new MedicDAO(getContext());

        loadMedics();

        fab.setOnClickListener(v -> showDialogNewMedic());

        // searchbar listener
        tiSearchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchText = s.toString().toLowerCase().trim();
                applyCombinedFilters(); // Llamamos al motor de filtrado
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        chipGroupSpeciality.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener(){
            @Override
            public void onCheckedChanged(@NonNull ChipGroup chipGroup, @NonNull List<Integer> list) {
                if(!list.isEmpty()){
                    for (Integer checkedId : list){
                        if (checkedId == R.id.chip_esp_todos) currentSpecialityFilter = "Todos";
                        else if (checkedId == R.id.chip_esp_clinica) currentSpecialityFilter = "Clínica";
                        else if (checkedId == R.id.chip_esp_cardiologia) currentSpecialityFilter = "Cardiología";
                        else if (checkedId == R.id.chip_esp_pediatria) currentSpecialityFilter = "Pediatría";
                    }
                    applyCombinedFilters();
                }
            }
        });

        return view;
    }

    private void loadMedics(){
        allMedics = medicDAO.getAll();
        applyCombinedFilters();
    }

    // Combined Filters
    @SuppressLint("SetTextI18n")
    private void applyCombinedFilters(){
        if (allMedics == null) return;

        List<MedicDTO> filteredList = new ArrayList<>();

        for (MedicDTO medic : allMedics) {
            // Text Search
            boolean matchesText = currentSearchText.isEmpty() ||
                    medic.getName().toLowerCase().contains(currentSearchText) ||
                    medic.getLastName().toLowerCase().contains(currentSearchText) ||
                    medic.getSpeciality().toLowerCase().contains(currentSearchText);

            // Chip Set Speaciality
            boolean matchesSpeciality = currentSpecialityFilter.equals("Todos") ||
                    medic.getSpeciality().equalsIgnoreCase(currentSpecialityFilter);

            if (matchesText && matchesSpeciality) {
                filteredList.add(medic);
            }
        }

        // Update UI
        tvCount.setText(filteredList.size() + " médicos registrados");

        if (filteredList.isEmpty()){
            layoutEmpty.setVisibility(View.VISIBLE);
            rvMedics.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvMedics.setVisibility(View.VISIBLE);

            if (medicAdapter == null) {
                medicAdapter = new MedicAdapter(filteredList);
                rvMedics.setAdapter(medicAdapter);
            } else {
                medicAdapter.updateList(filteredList);
            }
        }
    }

    private void showDialogNewMedic(){
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_medic, null);

        TextInputLayout tilName = dialogView.findViewById(R.id.til_dialog_medic_name);
        TextInputLayout tilLastname = dialogView.findViewById(R.id.til_dialog_medic_lastname);
        TextInputLayout tilRegistration = dialogView.findViewById(R.id.til_dialog_medic_registration);
        TextInputLayout tilSpeciality = dialogView.findViewById(R.id.til_dialog_medic_speciality);

        TextInputEditText etName = dialogView.findViewById(R.id.et_dialog_medic_name);
        TextInputEditText etLastname = dialogView.findViewById(R.id.et_dialog_medic_lastname);
        TextInputEditText etRegistration = dialogView.findViewById(R.id.et_dialog_medic_registration);
        TextInputEditText etSpeciality = dialogView.findViewById(R.id.et_dialog_medic_speciality);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                // Clean errors
                tilName.setError(null);
                tilLastname.setError(null);
                tilRegistration.setError(null);
                tilSpeciality.setError(null);

                String name = etName.getText().toString().trim();
                String lastname = etLastname.getText().toString().trim();
                String registration = etRegistration.getText().toString().trim();
                String speciality = etSpeciality.getText().toString().trim();

                boolean valid = true;

                if (name.isEmpty()) { tilName.setError("Requerido"); valid = false; }
                if (lastname.isEmpty()) { tilLastname.setError("Requerido"); valid = false; }
                if (registration.isEmpty()) { tilRegistration.setError("Requerido"); valid = false; }
                if (speciality.isEmpty()) { tilSpeciality.setError("Requerido"); valid = false; }

                if (!valid) return;

                // Crear DTO y guardar
                MedicDTO newMedic = new MedicDTO(name, lastname, registration, speciality);
                newMedic.setActive(true);

                long idGenerated = medicDAO.insert(newMedic);

                if (idGenerated > 0) {
                    Toast.makeText(requireContext(), "Médico guardado correctamente", Toast.LENGTH_SHORT).show();
                    loadMedics(); // Recargamos de la BD
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.show();
    }
}