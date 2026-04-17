package network;

import business.entities.LoginRequestDTO;
import business.entities.LoginResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("auth/login")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO request);
}