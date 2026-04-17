package ui.patients;

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import business.entities.PatientDTO;
import business.services.PatientService;

public class PatientFragment extends Fragment {
    private RecyclerView rvPatients;
    private LinearLayout layoutEmpty;
    private TextView tvCount;
    private FloatingActionButton fab;
    private TextInputEditText tiSearchBar;

    private PatientService patientService;
    private PatientAdapter patientAdapter;
    private List<PatientDTO> currentPatients;
    private String currentSearchText = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_patients, container, false);

        rvPatients = view.findViewById(R.id.rv_pacientes);
        layoutEmpty = view.findViewById(R.id.layout_empty_pacientes);
        tvCount = view.findViewById(R.id.tv_count_pacientes);
        fab = view.findViewById(R.id.fab_nuevo_paciente);
        tiSearchBar = view.findViewById(R.id.et_buscar_paciente);

        rvPatients.setLayoutManager(new LinearLayoutManager(getContext()));

        patientService = new PatientService(getContext());

        loadPatients();

        fab.setOnClickListener(view1 -> showDialogNewPatient());

        tiSearchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                currentSearchText = charSequence.toString();
                applyFilter(); // Llamamos al método centralizado de UI
            }
            @Override public void afterTextChanged(Editable editable) {}
        });

        return view;
    }

    private void loadPatients() {
        patientService.getAllPatients(new PatientService.OnPatientsLoaded() {
            @Override
            public void onSuccess(List<PatientDTO> patients) {
                if (!isAdded() || getContext() == null) return;
                currentPatients = patients;
                applyFilter();
            }

            @Override
            public void onError(String errorMessage) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void applyFilter() {
        if (currentPatients == null) return;

        List<PatientDTO> filteredList = patientService.filterPatients(currentPatients, currentSearchText);

        tvCount.setText(filteredList.size() + " pacientes registrados");

        if (filteredList.isEmpty()){
            layoutEmpty.setVisibility(View.VISIBLE);
            rvPatients.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvPatients.setVisibility(View.VISIBLE);

            if (patientAdapter == null) {
                patientAdapter = new PatientAdapter(filteredList);
                rvPatients.setAdapter(patientAdapter);
            } else {
                patientAdapter.updateList(filteredList);
            }
        }
    }

    private void showDialogNewPatient(){
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_patient, null);

        TextInputLayout tilNombre   = dialogView.findViewById(R.id.til_dialog_name);
        TextInputLayout tilApellido = dialogView.findViewById(R.id.til_dialog_lastname);
        TextInputLayout tilDni      = dialogView.findViewById(R.id.til_dialog_dni);
        TextInputLayout tilTelefono = dialogView.findViewById(R.id.til_dialog_phone);
        TextInputLayout tilEmail    = dialogView.findViewById(R.id.til_dialog_email);

        TextInputEditText etNombre   = dialogView.findViewById(R.id.et_dialog_name);
        TextInputEditText etApellido = dialogView.findViewById(R.id.et_dialog_lastname);
        TextInputEditText etDni      = dialogView.findViewById(R.id.et_dialog_dni);
        TextInputEditText etTelefono = dialogView.findViewById(R.id.et_dialog_phone);
        TextInputEditText etEmail    = dialogView.findViewById(R.id.et_dialog_email);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

                tilNombre.setError(null);
                tilApellido.setError(null);
                tilDni.setError(null);
                tilTelefono.setError(null);
                tilEmail.setError(null);

                patientService.registerPatient(
                        etNombre.getText() != null ? etNombre.getText().toString() : "",
                        etApellido.getText() != null ? etApellido.getText().toString() : "",
                        etDni.getText() != null ? etDni.getText().toString() : "",
                        etTelefono.getText() != null ? etTelefono.getText().toString() : "",
                        etEmail.getText() != null ? etEmail.getText().toString() : "",
                        new PatientService.OnPatientSaved() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(requireContext(), "Paciente guardado en la nube", Toast.LENGTH_SHORT).show();
                                loadPatients();
                                dialog.dismiss();
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                );
            });
        });
        dialog.show();
    }
}