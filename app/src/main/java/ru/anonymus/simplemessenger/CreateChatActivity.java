package ru.anonymus.simplemessenger;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.anonymus.simplemessenger.adapters.AddedUserAdapter;
import ru.anonymus.simplemessenger.apiconnection.ApiService;
import ru.anonymus.simplemessenger.apiconnection.RetrofitClient;
import ru.anonymus.simplemessenger.databinding.ActivityCreateChatBinding;
import ru.anonymus.simplemessenger.dto.ChatDto;
import ru.anonymus.simplemessenger.dto.CreateChatRequest;
import ru.anonymus.simplemessenger.dto.UserDetailsMinDto;
import ru.anonymus.simplemessenger.storage.GlobalVariables;
import ru.anonymus.simplemessenger.storage.SecureStorage;

public class CreateChatActivity extends AppCompatActivity {

    private EditText etSearchUser;
    private Button btnAddUser;
    private RecyclerView recyclerViewAddedUsers;
    private List<UserDetailsMinDto> addedUsers;
    private AddedUserAdapter addedUserAdapter;
    private UserDetailsMinDto selectedUser;

    ActivityCreateChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        etSearchUser = binding.etSearchUser;
        btnAddUser = binding.btnAddUser;
        recyclerViewAddedUsers = binding.recyclerViewAddedUsers;

        addedUsers = new ArrayList<>();
        addedUserAdapter = new AddedUserAdapter(addedUsers);
        recyclerViewAddedUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAddedUsers.setAdapter(addedUserAdapter);

        btnAddUser.setEnabled(false);

        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                searchUser(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        btnAddUser.setOnClickListener(v -> {
            if (selectedUser != null) {
                addUserToChat(selectedUser);
            }
        });

        binding.chatDetailsToolbar.btnBack.setOnClickListener(v -> finish());

        findViewById(R.id.btnCreateChat).setOnClickListener(v -> createChat());
    }

    private void searchUser(String query) {
        if (query.isEmpty()) {
            btnAddUser.setEnabled(false);
            return;
        }

        Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);

        apiService.findUserByName(new SecureStorage(this).getToken(), query).enqueue(new Callback<UserDetailsMinDto>() {
            @Override
            public void onResponse(Call<UserDetailsMinDto> call, Response<UserDetailsMinDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDetailsMinDto user = response.body();
                    selectedUser = user;
                    btnAddUser.setEnabled(true);
                } else {
                    btnAddUser.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<UserDetailsMinDto> call, Throwable t) {
                Toast.makeText(CreateChatActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                Log.e("CreateChat", t.getMessage());
            }
        });
    }

    private void addUserToChat(UserDetailsMinDto user) {
        // Добавление пользователя в список
        addedUsers.add(user);
        addedUserAdapter.notifyDataSetChanged();

        // Скрытие информации о найденном пользователе
        etSearchUser.setText(""); // Очистить поле поиска

        // Отключить кнопку "Добавить"
        btnAddUser.setEnabled(false);
    }

    private void createChat() {
        if (addedUsers.isEmpty()) {
            Toast.makeText(this, "Добавьте хотя бы одного пользователя", Toast.LENGTH_SHORT).show();
            return;
        }

        // Создаём запрос на создание чата с выбранными пользователями
        Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);

        // Создаём объект для чата
        CreateChatRequest newChat = new CreateChatRequest();

        newChat.setMembersUsernames(addedUsers.stream()
                .map(UserDetailsMinDto::getUsername)
                .collect(Collectors.toList()));

        if (addedUsers.size() == 1) newChat.setType("direct");
        if (addedUsers.size() > 1){
            newChat.setType("group");
        }
        StringBuilder builder = new StringBuilder();
        for (String name : newChat.getMembersUsernames()){
            builder.append(name);
            if (!newChat.getMembersUsernames().get(newChat.getMembersUsernames().size() - 1).equals(name)) builder.append(", ");
        }
        newChat.setTitle(builder.toString());


        // Отправляем запрос на создание чата с выбранными пользователями
        apiService.createChat(new SecureStorage(this).getToken(), newChat).enqueue(new Callback<ChatDto>() {
            @Override
            public void onResponse(Call<ChatDto> call, Response<ChatDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateChatActivity.this, "Чат создан", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreateChatActivity.this, "Ошибка создания чата", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChatDto> call, Throwable t) {
                Toast.makeText(CreateChatActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

