package ru.anonymus.simplemessenger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.anonymus.simplemessenger.apiconnection.ApiService;
import ru.anonymus.simplemessenger.apiconnection.RetrofitClient;
import ru.anonymus.simplemessenger.databinding.ActivityMainBinding;
import ru.anonymus.simplemessenger.storage.GlobalVariables;
import ru.anonymus.simplemessenger.storage.SecureStorage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SecureStorage secureStorage = new SecureStorage(this);
        String token = secureStorage.getToken();

        if (token != null) {
            Log.d("MainActivity", "User is logged in");

            Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
            ApiService authService = retrofit.create(ApiService.class);

            authService.validate(secureStorage.getToken()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()){
                        startActivity(new Intent(MainActivity.this, ChatsActivity.class));
                    }else {
                        secureStorage.clearToken();
                        Toast.makeText(MainActivity.this, "Ваша авторизация устарела", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Ошибка соеденения с сервером.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            });


        } else {
            Log.d("MainActivity", "User is not logged in");
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

    }
}