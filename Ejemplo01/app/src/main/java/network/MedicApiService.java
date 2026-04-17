package network;

import java.util.List;

import business.entities.MedicDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MedicApiService {

    @GET("medics/")
    Call<List<MedicDTO>> getAllMedics();

    @POST("medics/")
    Call<MedicDTO> createMedic(@Body MedicDTO medic);
}