package network;

import java.util.List;
import business.entities.PatientDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PatientApiService {

    @GET("patients/")
    Call<List<PatientDTO>> getAllPatients();

    @POST("patients/")
    Call<PatientDTO> createPatient(@Body PatientDTO patient);
}