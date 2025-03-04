package ru.anonymus.simplemessenger;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.anonymus.simplemessenger.apiconnection.ApiService;
import ru.anonymus.simplemessenger.apiconnection.RetrofitClient;
import ru.anonymus.simplemessenger.databinding.ActivityRegisterBinding;
import ru.anonymus.simplemessenger.dto.RegisterRequest;
import ru.anonymus.simplemessenger.dto.UserDto;
import ru.anonymus.simplemessenger.storage.GlobalVariables;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etInviteCode;
    private Button btnRegister;
    private Button btnLoginRedirect;
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        etUsername = binding.etRegisterUsername;
        etPassword = binding.etRegisterPassword;
        etInviteCode = binding.etRegisterInviteCode;

        btnRegister = binding.btnRegister;
        btnLoginRedirect = binding.btnLoginRedirect;

        btnRegister.setOnClickListener(v -> registerUser());
        btnLoginRedirect.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String inviteCode = etInviteCode.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Проверка, что все поля заполнены
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(inviteCode) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Создание запроса на регистрацию пользователя
        Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
        ApiService authService = retrofit.create(ApiService.class);

        RegisterRequest request = new RegisterRequest(username, password, inviteCode);

        authService.register(request).enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegistrationActivity.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                    finish(); // Закрытие экрана регистрации после успешной регистрации
                } else {
                    Toast.makeText(RegistrationActivity.this, "Ошибка регистрации. Попробуйте снова.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                Toast.makeText(RegistrationActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                Log.e("REGISTER", "Ошибка сети", t);
            }
        });
    }
}

