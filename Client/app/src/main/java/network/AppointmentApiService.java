package network;

import java.util.List;
import business.entities.AppointmentDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AppointmentApiService {

    @GET("appointments/")
    Call<List<AppointmentDTO>> getAllAppointments();

    @POST("appointments/")
    Call<AppointmentDTO> createAppointment(@Body AppointmentDTO appointment);
}