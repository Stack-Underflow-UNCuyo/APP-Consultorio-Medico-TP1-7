package business.services;

import android.content.Context;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import business.entities.AppointmentDTO;
import business.entities.StateAppointment;
import network.AppointmentApiService;
import network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentService {

    private AppointmentApiService apiService;

    // Interfaces para los Callbacks asíncronos
    public interface OnAppointmentsLoaded {
        void onSuccess(List<AppointmentDTO> appointments);
        void onError(String errorMessage);
    }

    public interface OnAppointmentSaved {
        void onSuccess();
        void onError(String errorMessage);
    }

    public AppointmentService(Context context) {
        this.apiService = RetrofitClient.getClient().create(AppointmentApiService.class);
    }

    public void getAllAppointments(OnAppointmentsLoaded callback) {
        apiService.getAllAppointments().enqueue(new Callback<List<AppointmentDTO>>() {
            @Override
            public void onResponse(Call<List<AppointmentDTO>> call, Response<List<AppointmentDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error del servidor al cargar turnos: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<AppointmentDTO>> call, Throwable t) {
                callback.onError("Fallo de red: " + t.getMessage());
            }
        });
    }

    public void getAppointmentsByPatient(long patientId, OnAppointmentsLoaded callback) {
        apiService.getAppointmentsByPatient(patientId).enqueue(new Callback<List<AppointmentDTO>>() {
            @Override
            public void onResponse(Call<List<AppointmentDTO>> call, Response<List<AppointmentDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error al cargar turnos del paciente: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<AppointmentDTO>> call, Throwable t) {
                callback.onError("Fallo de red: " + t.getMessage());
            }
        });
    }

    public void getAppointmentsByMedic(long medicId, OnAppointmentsLoaded callback) {
        apiService.getAppointmentsByMedic(medicId).enqueue(new Callback<List<AppointmentDTO>>() {
            @Override
            public void onResponse(Call<List<AppointmentDTO>> call, Response<List<AppointmentDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error al cargar turnos del médico: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<AppointmentDTO>> call, Throwable t) {
                callback.onError("Fallo de red: " + t.getMessage());
            }
        });
    }

    public void registerAppointment(String date, String time, String idPatientStr, String idMedicStr, OnAppointmentSaved callback) {
        try {
            if (date == null || date.trim().isEmpty()) throw new Exception("La fecha es obligatoria");
            if (time == null || time.trim().isEmpty()) throw new Exception("El horario es obligatorio");
            if (idPatientStr == null || idPatientStr.trim().isEmpty()) throw new Exception("Debes seleccionar un paciente");
            if (idMedicStr == null || idMedicStr.trim().isEmpty()) throw new Exception("Debes seleccionar un médico");

            long idPatient;
            long idMedic;
            try {
                idPatient = Long.parseLong(idPatientStr.trim());
                idMedic = Long.parseLong(idMedicStr.trim());
            } catch (NumberFormatException e) {
                throw new Exception("Error interno: Los IDs seleccionados no son válidos.");
            }

            AppointmentDTO newAppt = new AppointmentDTO(date.trim(), time.trim(), idPatient, idMedic, StateAppointment.PENDING);
            newAppt.setActive(true);

            apiService.createAppointment(newAppt).enqueue(new Callback<AppointmentDTO>() {
                @Override
                public void onResponse(Call<AppointmentDTO> call, Response<AppointmentDTO> response) {
                    if (response.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Error del servidor al guardar el turno");
                    }
                }

                @Override
                public void onFailure(Call<AppointmentDTO> call, Throwable t) {
                    callback.onError("Fallo de red al guardar: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    public List<AppointmentDTO> getPendingAppointments(List<AppointmentDTO> allAppointments) {
        if (allAppointments == null) return new ArrayList<>();

        List<AppointmentDTO> pendingList = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (AppointmentDTO appt : allAppointments) {
            try {
                LocalDate apptDate = LocalDate.parse(appt.getDate());
                if (apptDate.isEqual(today) || apptDate.isAfter(today)) {
                    pendingList.add(appt);
                }
            } catch (Exception ignored) {
            }
        }
        return pendingList;
    }
}
