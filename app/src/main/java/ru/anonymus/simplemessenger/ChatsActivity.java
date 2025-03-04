package ru.anonymus.simplemessenger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.anonymus.simplemessenger.adapters.ChatAdapter;
import ru.anonymus.simplemessenger.apiconnection.ApiService;
import ru.anonymus.simplemessenger.apiconnection.RetrofitClient;
import ru.anonymus.simplemessenger.databinding.ActivityChatListBinding;
import ru.anonymus.simplemessenger.dto.ChatDto;
import ru.anonymus.simplemessenger.dto.UserDetailsDto;
import ru.anonymus.simplemessenger.storage.GlobalVariables;
import ru.anonymus.simplemessenger.storage.SecureStorage;

public class ChatsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatDto> chatList;
    private ActivityChatListBinding binding;
    private UserDetailsDto currentUser;
    private ImageButton adminButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adminButton = binding.chatListToolBar.btnAdmin;
        recyclerView = binding.recyclerViewChats;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        currentUser = fetchCurrentUser();

        if (currentUser.getAuthorities().contains("ROLE_ADMIN")){
            adminButton.setActivated(true);
            adminButton.setVisibility(View.VISIBLE);
            adminButton.setOnClickListener(v -> {
                startActivity(new Intent(ChatsActivity.this, AdminActivity.class));
            });
        }else {
            adminButton.setActivated(false);
            adminButton.setVisibility(View.GONE);
        }


        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList, chat -> {
            Intent intent = new Intent(ChatsActivity.this, ChatActivity.class);
            intent.putExtra("chat_id", String.valueOf(chat.getChatId()));
            intent.putExtra("chat_type", chat.getType());

            if (chat.getType().equals("direct")){

                for (String name : chat.getMembers()){
                    if (!currentUser.getUsername().equals(name)){
                        intent.putExtra("chat_title", name);
                        break;
                    }
                }

            }else if (chat.getType().equals("group")){
                intent.putExtra("chat_title", chat.getTitle());
            }

            startActivity(intent);
        }, currentUser.getUsername());

        recyclerView.setAdapter(chatAdapter);

        binding.btnNewChat.setOnClickListener(v -> {
            startActivity(new Intent(ChatsActivity.this, CreateChatActivity.class));
        });

        binding.chatListToolBar.btnUserSettings.setOnClickListener(v -> startActivity(new Intent(ChatsActivity.this, MyProfileActivity.class)));

        loadChats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChats();
    }

    private void loadChats() {
        String token = new SecureStorage(this).getToken();
        if (token == null) {
            Toast.makeText(this, "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ChatsActivity.this, LoginActivity.class));
            return;
        }

        Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getMyChats(token).enqueue(new Callback<List<ChatDto>>() {
            @Override
            public void onResponse(Call<List<ChatDto>> call, Response<List<ChatDto>> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        chatList.clear();
                        chatList.addAll(response.body());
                        chatAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(ChatsActivity.this, "У вас ещё нет чатов.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.e("ChatsActivity", "Ошибка получения чатов: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<List<ChatDto>> call, Throwable t) {
                Log.e("ChatsActivity", "Ошибка сети: " + t.getMessage());
                Toast.makeText(ChatsActivity.this, "Ошибка загрузки чатов", Toast.LENGTH_SHORT).show();
            }
        });

    }

//    private String fetchCurrentUserName() {
//        String user = null;
//        Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
//        ApiService apiService = retrofit.create(ApiService.class);
//        Call<UserDetailsDto> call = apiService.findMe(new SecureStorage(this).getToken());
//        call.enqueue(new Callback<UserDetailsDto>() {
//            @Override
//            public void onResponse(Call<UserDetailsDto> call, Response<UserDetailsDto> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    user = response.body().getUsername();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UserDetailsDto> call, Throwable t) {
//                // Обработка ошибки
//                Toast.makeText(ChatsActivity.this, "Сервер не отвечает...", Toast.LENGTH_SHORT).show();
//                Log.e("Chat", t.getMessage());
//            }
//        });
//    }

    private UserDetailsDto fetchCurrentUser() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<UserDetailsDto> future = executor.submit(() -> {
            try {
                Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
                ApiService apiService = retrofit.create(ApiService.class);
                Response<UserDetailsDto> response = apiService.findMe(new SecureStorage(this).getToken()).execute();
                if (response.isSuccessful() && response.body() != null) {
                    return response.body();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            executor.shutdown();
        }
    }


}

