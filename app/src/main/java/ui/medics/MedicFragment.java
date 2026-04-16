package ui.medics;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compmovil.ejemplo01.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import business.entities.MedicDTO;
import business.persistence.MedicDAO;
import business.services.MedicService;

public class MedicFragment extends Fragment {
    private RecyclerView rvMedics;
    private LinearLayout layoutEmpty;
    private TextView tvCount;
    private FloatingActionButton fab;
    private TextInputEditText tiSearchBar;
    private ChipGroup chipGroupSpeciality;

    private MedicService medicService;
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
        medicService = new MedicService(getContext());

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
        allMedics = medicService.getAllMedics();
        applyCombinedFilters();
    }

    // Combined Filters
    @SuppressLint("SetTextI18n")
    private void applyCombinedFilters(){
        if (allMedics == null) return;

        List<MedicDTO> filteredList = medicService.filterMedics(allMedics, currentSearchText, currentSpecialityFilter);

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
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_medic, null);

        TextInputLayout      tilNombre       = dialogView.findViewById(R.id.til_dialog_medic_name);
        TextInputLayout      tilApellido     = dialogView.findViewById(R.id.til_dialog_medic_lastname);
        TextInputLayout      tilMatricula    = dialogView.findViewById(R.id.til_dialog_medic_registration);
        TextInputLayout      tilEspecialidad = dialogView.findViewById(R.id.til_dialog_medic_speciality);

        TextInputEditText    etNombre        = dialogView.findViewById(R.id.et_dialog_medic_name);
        TextInputEditText    etApellido      = dialogView.findViewById(R.id.et_dialog_medic_lastname);
        TextInputEditText    etMatricula     = dialogView.findViewById(R.id.et_dialog_medic_registration);
        AutoCompleteTextView actvEspecialidad = dialogView.findViewById(R.id.actv_dialog_medic_speciality);

        ChipGroup chipGroupEsp = dialogView.findViewById(R.id.chipgroup_especialidades);

        String[] especialidades = {
                "Clínica Médica", "Cardiología", "Pediatría",
                "Traumatología", "Dermatología", "Ginecología",
                "Neurología", "Oftalmología"
        };
        ArrayAdapter<String> adapterEsp = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                especialidades);
        actvEspecialidad.setAdapter(adapterEsp);

        int[] chipIds = {
                R.id.chip_esp_clinica, R.id.chip_esp_cardiologia,
                R.id.chip_esp_pediatria, R.id.chip_esp_traumatologia,
                R.id.chip_esp_dermatologia, R.id.chip_esp_ginecologia,
                R.id.chip_esp_neurologia, R.id.chip_esp_oftalmologia
        };
        for (int chipId : chipIds) {
            Chip chip = dialogView.findViewById(chipId);
            if (chip != null) {
                chip.setOnClickListener(v -> {
                    actvEspecialidad.setText(chip.getText(), false);
                    tilEspecialidad.setError(null);
                    actvEspecialidad.clearFocus();
                });
            }
        }

        actvEspecialidad.setOnItemClickListener((parent, v, position, id) ->
                tilEspecialidad.setError(null));

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {
                        try {
                            medicService.registerMedic(
                                    etNombre.getText().toString(),
                                    etApellido.getText().toString(),
                                    etMatricula.getText().toString(),
                                    actvEspecialidad.getText().toString()
                            );

                            Toast.makeText(requireContext(), "Médico guardado", Toast.LENGTH_SHORT).show();
                            loadMedics();
                            dialog.dismiss();

                        } catch (Exception e) {
                            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        dialog.show();
    }
}