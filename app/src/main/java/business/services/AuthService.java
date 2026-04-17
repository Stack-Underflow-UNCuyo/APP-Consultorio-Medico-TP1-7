package business.services;

import android.content.Context;
import business.entities.LoginRequestDTO;
import business.entities.LoginResponseDTO;
import business.security.TokenManager;
import network.AuthApiService;
import network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthService {

    private final AuthApiService apiService;
    private final TokenManager tokenManager;
    private final Context context;

    public interface OnLoginResult {
        void onSuccess(String role);
        void onError(String message);
    }

    public AuthService(Context context) {
        this.apiService   = RetrofitClient.getClient().create(AuthApiService.class);
        this.tokenManager = new TokenManager(context);
        this.context    = context;
    }

    public void login(String email, String password, OnLoginResult callback) {
        LoginRequestDTO request = new LoginRequestDTO(email, password);

        apiService.login(request).enqueue(new Callback<LoginResponseDTO>() {
            @Override
            public void onResponse(Call<LoginResponseDTO> call,
                                   Response<LoginResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponseDTO body = response.body();

                    // Guardamos el token y reiniciamos Retrofit con el nuevo token
                    tokenManager.saveSession(
                            body.getToken(),
                            body.getRole(),
                            body.getEmail()
                    );
                    RetrofitClient.reset();
                    RetrofitClient.init( context);

                    callback.onSuccess(body.getRole());
                } else if (response.code() == 401) {
                    callback.onError("Credenciales incorrectas");
                } else {
                    callback.onError("Error del servidor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                callback.onError("Sin conexión: " + t.getMessage());
            }
        });
    }
}