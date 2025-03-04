package ru.anonymus.simplemessenger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.anonymus.simplemessenger.apiconnection.ApiService;
import ru.anonymus.simplemessenger.apiconnection.RetrofitClient;
import ru.anonymus.simplemessenger.databinding.ActivityLoginBinding;
import ru.anonymus.simplemessenger.dto.LoginRequest;
import ru.anonymus.simplemessenger.dto.TokenResponse;
import ru.anonymus.simplemessenger.storage.GlobalVariables;
import ru.anonymus.simplemessenger.storage.SecureStorage;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private EditText etUsername, etPassword;
    private Button btnLogin;
    Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
    ApiService authService = retrofit.create(ApiService.class);
    SecureStorage secureStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        secureStorage = new SecureStorage(this);

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etUsername.getText().toString().isEmpty() || binding.etPassword.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Поля Имя пользователя и пароль не должны быть пустыми.", Toast.LENGTH_SHORT);
                }else {
                    LoginRequest loginRequest = new LoginRequest(binding.etUsername.getText().toString(), binding.etPassword.getText().toString());
                    authService.login(loginRequest).enqueue(new Callback<TokenResponse>() {
                        @Override
                        public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                            if (response.isSuccessful()){
                                String token = response.body().getToken();
                                Log.d("Login", "Token: " + token);
                                secureStorage.saveToken(token);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }else if (response.code() == 403){
                                Toast.makeText(getApplicationContext(), "Неверное имя пользователя или пароль", Toast.LENGTH_SHORT);
                            }else {
                                Toast.makeText(getApplicationContext(), "Произошла ошибка при попытке входа в аккаунт.", Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        public void onFailure(Call<TokenResponse> call, Throwable t) {
                            Log.e("Login", "Ошибка сети: " + t.getMessage());
                            Toast.makeText(getApplicationContext(), "Произошла ошибка при попытке входа в аккаунт.", Toast.LENGTH_SHORT);
                        }
                    });
                }
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

    }
}


