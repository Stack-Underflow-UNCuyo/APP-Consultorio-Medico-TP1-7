package network;

import java.util.List;
import business.entities.AppointmentDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AppointmentApiService {

    @GET("appointments/")
    Call<List<AppointmentDTO>> getAllAppointments();

    @POST("appointments/")
    Call<AppointmentDTO> createAppointment(@Body AppointmentDTO appointment);

    @GET("appointments/patient/{patientId}")
    Call<List<AppointmentDTO>> getAppointmentsByPatient(@Path("patientId") long patientId);

    @GET("appointments/medic/{medicId}")
    Call<List<AppointmentDTO>> getAppointmentsByMedic(@Path("medicId") long medicId);
}
