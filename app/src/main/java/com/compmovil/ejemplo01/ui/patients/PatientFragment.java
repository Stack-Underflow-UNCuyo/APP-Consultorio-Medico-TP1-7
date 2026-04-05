package com.compmovil.ejemplo01.ui.patients;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import business.entities.PatientDTO;
import business.persistence.PatientDAO;

public class PatientFragment extends Fragment {
    private RecyclerView rvPatients;
    private LinearLayout layoutEmpty;
    private TextView tvCount;
    private FloatingActionButton fab;
    private TextInputEditText tiSearchBar;

    private PatientDAO patientDAO;
    private PatientAdapter patientAdapter;
    private List<PatientDTO> currentPatients;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_patients, container, false);

        // vincular las vistas
        rvPatients = view.findViewById(R.id.rv_pacientes);
        layoutEmpty = view.findViewById(R.id.layout_empty_pacientes);
        tvCount = view.findViewById(R.id.tv_count_pacientes);
        fab = view.findViewById(R.id.fab_nuevo_paciente);
        tiSearchBar = view.findViewById(R.id.til_buscar_paciente);

        // config RecyclerView
        rvPatients.setLayoutManager(new LinearLayoutManager(getContext()));

        // init & load DAO
        patientDAO = new PatientDAO(getContext());
        loadPatients();

        // FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogNewPatient();
            }
        });

        // Searchbar
        tiSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterPatients(charSequence.toString());
            }
        });


        return view;
    }

    private void filterPatients(String txt){
        if (currentPatients == null){return;}

        List<PatientDTO> newList = new ArrayList<>();
        String txtMin = txt.toLowerCase();

        for (PatientDTO patient : currentPatients){
            if ((patient.getName().toLowerCase().contains(txtMin)) ||
            patient.getLastName().toLowerCase().contains(txtMin) ||
            String.valueOf(patient.getDni()).contains(txtMin)){
                newList.add(patient);
            }
        }

        if(patientAdapter != null){
            patientAdapter.updateList(newList);
        }

    }

    private void showDialogNewPatient(){
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_patient, null);

        // get TextInputLayout to show errors
        TextInputLayout tilNombre   = dialogView.findViewById(R.id.til_dialog_name);
        TextInputLayout tilApellido = dialogView.findViewById(R.id.til_dialog_lastname);
        TextInputLayout tilDni      = dialogView.findViewById(R.id.til_dialog_dni);
        TextInputLayout tilTelefono = dialogView.findViewById(R.id.til_dialog_phone);
        TextInputLayout tilEmail    = dialogView.findViewById(R.id.til_dialog_email);

        // get EditText
        TextInputEditText etNombre   = dialogView.findViewById(R.id.et_dialog_name);
        TextInputEditText etApellido = dialogView.findViewById(R.id.et_dialog_lastname);
        TextInputEditText etDni      = dialogView.findViewById(R.id.et_dialog_dni);
        TextInputEditText etTelefono = dialogView.findViewById(R.id.et_dialog_phone);
        TextInputEditText etEmail    = dialogView.findViewById(R.id.et_dialog_email);

        // create dialog pop up
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (d, which) -> d.dismiss())
                .create();

        // overrides save button incase of errors
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {
                        // clean errors
                        tilNombre.setError(null);
                        tilApellido.setError(null);
                        tilDni.setError(null);
                        tilTelefono.setError(null);
                        tilEmail.setError(null);

                        // reed values
                        String nombre   = etNombre.getText()   != null ? etNombre.getText().toString().trim()   : "";
                        String apellido = etApellido.getText() != null ? etApellido.getText().toString().trim() : "";
                        String dniStr   = etDni.getText()      != null ? etDni.getText().toString().trim()      : "";
                        String telefono = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";
                        String email    = etEmail.getText()    != null ? etEmail.getText().toString().trim()    : "";

                        // validate
                        boolean valido = true;

                        if (nombre.isEmpty()) {
                            tilNombre.setError("El nombre es obligatorio");
                            valido = false;
                        }
                        if (apellido.isEmpty()) {
                            tilApellido.setError("El apellido es obligatorio");
                            valido = false;
                        }
                        if (dniStr.isEmpty()) {
                            tilDni.setError("El DNI es obligatorio");
                            valido = false;
                        } else if (dniStr.length() < 7 || dniStr.length() > 8) {
                            tilDni.setError("DNI inválido (7 u 8 dígitos)");
                            valido = false;
                        }
                        if (telefono.isEmpty()) {
                            tilTelefono.setError("El teléfono es obligatorio");
                            valido = false;
                        }
                        if (email.isEmpty()) {
                            tilEmail.setError("El email es obligatorio");
                            valido = false;
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            tilEmail.setError("Formato de email inválido");
                            valido = false;
                        }

                        if (!valido) return; // no cerramos el pop up

                        PatientDTO newPatient = new PatientDTO(
                                nombre,
                                apellido,
                                email,
                                Integer.parseInt(dniStr),
                                Integer.parseInt(telefono)
                        );

                        PatientDAO dao = new PatientDAO(requireContext());
                        long idGenerado = dao.insert(newPatient);

                        if (idGenerado > 0) {
                            Toast.makeText(requireContext(),
                                    "Paciente guardado correctamente", Toast.LENGTH_SHORT).show();
                            loadPatients();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(requireContext(),
                                    "Error al guardar. Intentá de nuevo.", Toast.LENGTH_SHORT).show();
                        }
                    });
            dialog.show();
        });

    }

    @SuppressLint("SetTextI18n")
    private  void loadPatients(){
        currentPatients = patientDAO.getAll();

        tvCount.setText(currentPatients.size() + " pacientes registrados");

        if (currentPatients.isEmpty()){
            layoutEmpty.setVisibility(View.VISIBLE);
            rvPatients.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvPatients.setVisibility(View.VISIBLE);

            patientAdapter = new PatientAdapter(currentPatients);
            rvPatients.setAdapter(patientAdapter);
        }
    }

}