package ru.anonymus.simplemessenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.anonymus.simplemessenger.adapters.ScreenMessageAdapter;
import ru.anonymus.simplemessenger.apiconnection.ApiService;
import ru.anonymus.simplemessenger.apiconnection.RetrofitClient;
import ru.anonymus.simplemessenger.databinding.ActivityChatBinding;
import ru.anonymus.simplemessenger.dto.MessageDto;
import ru.anonymus.simplemessenger.dto.UserDetailsDto;
import ru.anonymus.simplemessenger.storage.GlobalVariables;
import ru.anonymus.simplemessenger.storage.SecureStorage;

public class ChatActivity extends AppCompatActivity implements WebSocketClient.WebSocketCallback {

    private WebSocketClient webSocketClient;
    private RecyclerView recyclerViewMessages;
    private ScreenMessageAdapter messageAdapter;
    private List<MessageDto> messages = new ArrayList<>();
    private String currentUserName;
    private EditText etMessage;
    private ImageButton btnSend;
    private ActivityChatBinding binding;
    private Toolbar chatToolBar;

    private ImageButton btnBack;
    private ImageButton btnMenu;
    private ImageView chatAvatar;
    private TextView chatTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerViewMessages = binding.recyclerViewMessages;
        etMessage = binding.etMessage;
        btnSend = binding.btnSend;

        chatToolBar = binding.chatToolBar.getRoot();

        btnBack = binding.chatToolBar.btnBack;
        btnMenu = binding.chatToolBar.btnMenu;
        chatAvatar = binding.chatToolBar.chatAvatar;
        chatTitle = binding.chatToolBar.chatTitle;

        if (getIntent().getStringExtra("chat_type").equals("direct")){
            chatAvatar.setImageResource(R.drawable.baseline_supervised_user_circle_24);
        }else {
            chatAvatar.setImageResource(R.drawable.baseline_groups_24);
            chatToolBar.setOnClickListener(v -> {
                Intent intent = new Intent(ChatActivity.this, ChatDetailsActivity.class);
                intent.putExtra("chat_id", getIntent().getStringExtra("chat_id"));
                startActivity(intent);
            });
        }

        chatTitle.setText(getIntent().getStringExtra("chat_title"));

        btnBack.setOnClickListener(v -> finish());

        // Настройка RecyclerView
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));

        // Получаем имя текущего пользователя
        fetchCurrentUserName();

        webSocketClient = new WebSocketClient(this, new SecureStorage(this).getToken());

        webSocketClient.connectToChat(getIntent().getStringExtra("chat_id"));

        // Обработчик нажатия на кнопку отправки сообщения
        btnSend.setOnClickListener(v -> sendMessage());

        Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<MessageDto>> call = apiService.getMessagesInChat(new SecureStorage(this).getToken(), Long.parseLong(getIntent().getStringExtra("chat_id")));
        call.enqueue(new Callback<List<MessageDto>>() {
            @Override
            public void onResponse(Call<List<MessageDto>> call, Response<List<MessageDto>> response) {
                if (response.isSuccessful()){
                    messages.clear();
                    messages.addAll(response.body());  // Добавляем в список
                    messageAdapter.notifyItemInserted(messages.size() - 1);
                    recyclerViewMessages.scrollToPosition(messages.size() - 1);  // Прокручиваем к последнему сообщению
                }else {
                    Toast.makeText(ChatActivity.this, "Произошла ошибка при попытке получения сообщений: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MessageDto>> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Сервер не отвечает...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fetchCurrentUserName() {
        Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);
        Call<UserDetailsDto> call = apiService.findMe(new SecureStorage(this).getToken());
        call.enqueue(new Callback<UserDetailsDto>() {
            @Override
            public void onResponse(Call<UserDetailsDto> call, Response<UserDetailsDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUserName = response.body().getUsername();
                    setupRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<UserDetailsDto> call, Throwable t) {
                // Обработка ошибки
                Toast.makeText(ChatActivity.this, "Сервер не отвечает...", Toast.LENGTH_SHORT).show();
                Log.e("Chat", t.getMessage());
            }
        });
    }

    private void setupRecyclerView() {
        // Устанавливаем адаптер для RecyclerView
        messageAdapter = new ScreenMessageAdapter(messages, currentUserName, userId -> {
            Intent intent = new Intent(ChatActivity.this, UserViewActivity.class);
            intent.putExtra("user_id", String.valueOf(userId));
            startActivity(intent);
        });
        recyclerViewMessages.setAdapter(messageAdapter);

        // Автопрокрутка к последнему сообщению
        recyclerViewMessages.scrollToPosition(messages.size() - 1);
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        // Формируем сообщение
        MessageDto message = new MessageDto();
        message.setContent(messageText);
        message.setChatId(Long.parseLong(getIntent().getStringExtra("chat_id")));


        // Отправляем сообщение на сервер
        Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);
        Call<MessageDto> call = apiService.sendMessage(new SecureStorage(this).getToken(), message);
        call.enqueue(new Callback<MessageDto>() {
            @Override
            public void onResponse(Call<MessageDto> call, Response<MessageDto> response) {
                if (response.isSuccessful()) {
                    etMessage.setText("");  // Очищаем поле ввода
                }
            }

            @Override
            public void onFailure(Call<MessageDto> call, Throwable t) {
                // Обработка ошибки
                Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                Log.e("SEND_MESSAGE", t.getMessage());
            }
        });
    }

    private String getCurrentTimestamp() {
        // Возвращает текущую метку времени (можно использовать любую нужную библиотеку для работы с датой/временем)
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Добавляем новые сообщения в список
    public void addMessages(List<MessageDto> newMessages) {
        messages.addAll(newMessages);
        messageAdapter.notifyDataSetChanged();
        recyclerViewMessages.scrollToPosition(messages.size() - 1);
    }

    @Override
    public void onMessageReceived(String message) {
        try {
            MessageDto messageDto = new ObjectMapper().readValue(message, MessageDto.class);
            runOnUiThread(() -> {
                messages.add(messageDto);  // Добавляем в список
                messageAdapter.notifyItemInserted(messages.size() - 1);
                recyclerViewMessages.scrollToPosition(messages.size() - 1);  // Прокручиваем к последнему сообщению
            });

        } catch (JsonProcessingException e) {
            Log.e("WebSocket Callback Error", "Error occurated on message parse attempt", e);
        }
    }

    @Override
    protected void onDestroy(){
        webSocketClient.disconnect();
        super.onDestroy();
    }

}

