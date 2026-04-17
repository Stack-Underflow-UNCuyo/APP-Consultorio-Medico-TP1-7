package business.services;

import android.content.Context;
import android.util.Patterns;

import java.util.regex.Pattern;

import business.entities.LoginRequestDTO;
import business.entities.LoginResponseDTO;
import business.entities.UserDTO;
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

    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";


    public interface OnLoginResult {
        void onSuccess(String token, String role);
        void onError(String message);
    }

    public interface OnRegisterResult {
        void onSuccess();
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
                            body.getEmail(),
                            body.getName(),
                            body.getId()
                    );
                    RetrofitClient.reset();
                    RetrofitClient.init( context);

                    callback.onSuccess(body.getToken(), body.getRole());
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

    public void register(UserDTO user, OnRegisterResult callback) {
        apiService.register(user).enqueue(new retrofit2.Callback<UserDTO>() {
            @Override
            public void onResponse(retrofit2.Call<UserDTO> call, retrofit2.Response<UserDTO> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Error en el registro: " + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<UserDTO> call, Throwable t) {
                callback.onError("Fallo de red: " + t.getMessage());
            }
        });
    }

    public Boolean isEmailValid(String email){
        return email != null && !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public Boolean isPasswordValid(String password){
        if (password == null || password.isEmpty()) return false;
        return Pattern.compile(PASSWORD_PATTERN).matcher(password).matches();
    }

    public boolean doPasswordsMatch(String pass, String confirmPass) {
        if (pass == null || confirmPass == null) return false;
        return pass.equals(confirmPass);
    }


}
