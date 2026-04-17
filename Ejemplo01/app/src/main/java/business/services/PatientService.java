package business.services;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import business.entities.PatientDTO;
import network.PatientApiService;
import network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientService {

    private PatientApiService apiService;

    public interface OnPatientsLoaded {
        void onSuccess(List<PatientDTO> patients);
        void onError(String errorMessage);
    }

    public interface OnPatientSaved {
        void onSuccess();
        void onError(String errorMessage);
    }

    public PatientService(Context context) {
        this.apiService = RetrofitClient.getClient().create(PatientApiService.class);
    }


    public void getAllPatients(OnPatientsLoaded callback) {
        apiService.getAllPatients().enqueue(new Callback<List<PatientDTO>>() {
            @Override
            public void onResponse(Call<List<PatientDTO>> call, Response<List<PatientDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error del servidor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<PatientDTO>> call, Throwable t) {
                callback.onError("Fallo de red: " + t.getMessage());
            }
        });
    }

    public void registerPatient(String name, String lastName, String dniStr, String phoneStr, String email, OnPatientSaved callback) {
        try {
            if (name == null || name.trim().isEmpty()) throw new Exception("El nombre es obligatorio");
            if (lastName == null || lastName.trim().isEmpty()) throw new Exception("El apellido es obligatorio");
            if (dniStr == null || dniStr.trim().isEmpty()) throw new Exception("El DNI es obligatorio");
            if (phoneStr == null || phoneStr.trim().isEmpty()) throw new Exception("El teléfono es obligatorio");
            if (email == null || email.trim().isEmpty()) throw new Exception("El email es obligatorio");

            int dni = Integer.parseInt(dniStr.trim());

            PatientDTO newPatient = new PatientDTO(name.trim(), lastName.trim(), email.trim(), dni, phoneStr);
            newPatient.setActive(true);

            // Enviamos el objeto a la API REST
            apiService.createPatient(newPatient).enqueue(new Callback<PatientDTO>() {
                @Override
                public void onResponse(Call<PatientDTO> call, Response<PatientDTO> response) {
                    if (response.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Error al guardar en el servidor");
                    }
                }

                @Override
                public void onFailure(Call<PatientDTO> call, Throwable t) {
                    callback.onError("Fallo de red al guardar: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    public List<PatientDTO> filterPatients(List<PatientDTO> allPatients, String searchText) {
        if (allPatients == null) return new ArrayList<>();

        List<PatientDTO> filteredList = new ArrayList<>();
        String searchLower = searchText != null ? searchText.toLowerCase().trim() : "";

        for (PatientDTO patient : allPatients) {
            if (patient.getName().toLowerCase().contains(searchLower) ||
                    patient.getLastName().toLowerCase().contains(searchLower) ||
                    String.valueOf(patient.getDni()).contains(searchLower)) {
                filteredList.add(patient);
            }
        }
        return filteredList;
    }
}