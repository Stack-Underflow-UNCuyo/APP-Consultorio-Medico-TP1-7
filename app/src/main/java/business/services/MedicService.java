package business.services;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import business.entities.MedicDTO;
import network.MedicApiService;
import network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicService {

    private MedicApiService apiService;

    public interface OnMedicsLoaded {
        void onSuccess(List<MedicDTO> medics);
        void onError(String errorMessage);
    }

    public interface OnMedicSaved {
        void onSuccess();
        void onError(String errorMessage);
    }

    public MedicService(Context context) {
        this.apiService = RetrofitClient.getClient().create(MedicApiService.class);
    }

    public void getAllMedics(OnMedicsLoaded callback) {
        apiService.getAllMedics().enqueue(new Callback<List<MedicDTO>>() {
            @Override
            public void onResponse(Call<List<MedicDTO>> call, Response<List<MedicDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error del servidor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<MedicDTO>> call, Throwable t) {
                callback.onError("Fallo de red: " + t.getMessage());
            }
        });
    }

    public void registerMedic(String name, String lastName, String registration, String speciality, OnMedicSaved callback) {
        try {
            if (name == null || name.trim().isEmpty()) throw new Exception("El nombre es obligatorio");
            if (lastName == null || lastName.trim().isEmpty()) throw new Exception("El apellido es obligatorio");
            if (registration == null || registration.trim().isEmpty()) throw new Exception("La matrícula es obligatoria");
            if (speciality == null || speciality.trim().isEmpty()) throw new Exception("La especialidad es obligatoria");

            MedicDTO newMedic = new MedicDTO(name.trim(), lastName.trim(), registration.trim(), speciality.trim());
            newMedic.setActive(true);

            apiService.createMedic(newMedic).enqueue(new Callback<MedicDTO>() {
                @Override
                public void onResponse(Call<MedicDTO> call, Response<MedicDTO> response) {
                    if (response.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Error al guardar en el servidor");
                    }
                }

                @Override
                public void onFailure(Call<MedicDTO> call, Throwable t) {
                    callback.onError("Fallo de red al guardar: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    public List<MedicDTO> filterMedics(List<MedicDTO> allMedics, String searchText, String specialityFilter) {
        if (allMedics == null) return new ArrayList<>();

        List<MedicDTO> filteredList = new ArrayList<>();
        String searchLower = searchText != null ? searchText.toLowerCase().trim() : "";

        for (MedicDTO medic : allMedics) {
            boolean matchesText = searchLower.isEmpty() ||
                    (medic.getName() != null && medic.getName().toLowerCase().contains(searchLower)) ||
                    (medic.getLastName() != null && medic.getLastName().toLowerCase().contains(searchLower)) ||
                    (medic.getSpeciality() != null && medic.getSpeciality().toLowerCase().contains(searchLower));

            boolean matchesSpeciality = specialityFilter.equals("Todos") ||
                    (medic.getSpeciality() != null && medic.getSpeciality().equalsIgnoreCase(specialityFilter));

            if (matchesText && matchesSpeciality) {
                filteredList.add(medic);
            }
        }
        return filteredList;
    }
}