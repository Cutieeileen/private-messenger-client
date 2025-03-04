package ru.anonymus.simplemessenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import ru.anonymus.simplemessenger.adapters.ChatDetailsAdapter;
import ru.anonymus.simplemessenger.apiconnection.ApiService;
import ru.anonymus.simplemessenger.apiconnection.RetrofitClient;
import ru.anonymus.simplemessenger.databinding.ActivityChatDetailsBinding;
import ru.anonymus.simplemessenger.databinding.DialogAddUserToExistingChatBinding;
import ru.anonymus.simplemessenger.dto.ChatDetailsDto;
import ru.anonymus.simplemessenger.dto.UserDetailsDto;
import ru.anonymus.simplemessenger.dto.UserDetailsMinDto;
import ru.anonymus.simplemessenger.storage.GlobalVariables;
import ru.anonymus.simplemessenger.storage.SecureStorage;

public class ChatDetailsActivity extends AppCompatActivity {

    private ActivityChatDetailsBinding binding;

    private ImageView chatDetailsIcon;
    private TextView chatViewUsername;
    private RecyclerView chatUsersList;
    private ImageButton addUserBtn;

    private ChatDetailsDto chatDetails;

    private UserDetailsDto currentUser;

    private List<UserDetailsDto> users;
    private ChatDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.chatDetailsToolbar.btnBack.setOnClickListener(v -> finish());

        chatDetailsIcon = binding.chatDetailsIcon;
        chatViewUsername = binding.chatViewUsername;
        chatUsersList = binding.chatUsersList;
        addUserBtn = binding.addUserBtn;

        users = new ArrayList<>();

        currentUser = fetchCurrentUser();
        chatDetails = fetchChatDetails(Long.parseLong(getIntent().getStringExtra("chat_id")));

        adapter = new ChatDetailsAdapter(
                users,
                new ChatDetailsAdapter.OnUserClickListener() {
                    @Override
                    public void onUserClick(UserDetailsDto userDetails) {
                        Intent intent = new Intent(ChatDetailsActivity.this, UserViewActivity.class);
                        Log.i("USER-ID", String.valueOf(userDetails.getId()));
                        intent.putExtra("user_id", String.valueOf(userDetails.getId()));
                        startActivity(intent);
                    }
                },
                new ChatDetailsAdapter.OnDeleteClickListener() {
                    @Override
                    public void onDeleteClick(UserDetailsDto userDetails) {
                        ChatDetailsActivity.this.chatDetails = ChatDetailsActivity.this.deleteUserFromChat(Long.parseLong(ChatDetailsActivity.this.getIntent().getStringExtra("chat_id")), userDetails.getId());
                        ChatDetailsActivity.this.updateView();
                    }
                },
                currentUser.getAuthorities().contains("ROLE_ADMIN")
        );

        chatUsersList.setLayoutManager(new LinearLayoutManager(this));
        chatUsersList.setAdapter(adapter);

        addUserBtn.setOnClickListener(v -> showAddUserDialog());

        updateView();

    }

    private void showAddUserDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_add_user_to_existing_chat, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        EditText usernameField = dialogView.findViewById(R.id.dialogSearchUsersEt);
        Button button = dialogView.findViewById(R.id.dialogAddUserBtn);

        usernameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String query = charSequence.toString().trim();
                if (!query.isEmpty()){
                    UserDetailsDto userDetails = ChatDetailsActivity.this.findUserByName(charSequence.toString().trim());
                    if (userDetails == null){
                        button.setEnabled(false);
                    }else {
                        button.setEnabled(true);
                    }
                }else {
                    button.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        button.setOnClickListener(v -> {
            UserDetailsDto userDetails = ChatDetailsActivity.this.findUserByName(usernameField.getText().toString());
            ChatDetailsActivity.this.chatDetails = addUserToChat(Long.parseLong(ChatDetailsActivity.this.getIntent().getStringExtra("chat_id")), userDetails.getId());
            ChatDetailsActivity.this.updateView();
            dialog.dismiss();
        });

        dialog.show();

    }

    private ChatDetailsDto addUserToChat(Long chatId, Long userId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ChatDetailsDto> future = executor.submit(() -> {
            try {
                Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
                ApiService apiService = retrofit.create(ApiService.class);
                Response<ChatDetailsDto> response = apiService.addUserToExistingChat(new SecureStorage(this).getToken(), chatId, userId).execute();
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

    private UserDetailsDto findUserByName(String name) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<UserDetailsDto> future = executor.submit(() -> {
            try {
                Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
                ApiService apiService = retrofit.create(ApiService.class);
                Response<UserDetailsDto> response = apiService.findUserDetailsByName(new SecureStorage(this).getToken(), name).execute();
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

    private ChatDetailsDto deleteUserFromChat(Long chatId, Long userId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ChatDetailsDto> future = executor.submit(() -> {
            try {
                Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
                ApiService apiService = retrofit.create(ApiService.class);
                Response<ChatDetailsDto> response = apiService.deleteUserFromChat(new SecureStorage(this).getToken(), chatId, userId).execute();
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

    private ChatDetailsDto fetchChatDetails(Long chatId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ChatDetailsDto> future = executor.submit(() -> {
            try {
                Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
                ApiService apiService = retrofit.create(ApiService.class);
                Response<ChatDetailsDto> response = apiService.getChatDetails(new SecureStorage(this).getToken(), chatId).execute();
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

    private void updateView(){

        chatViewUsername.setText(chatDetails.getTitle());

        if (chatDetails.getType().equals("direct")){
            chatDetailsIcon.setImageResource(R.drawable.baseline_supervised_user_circle_24);
        }else if (chatDetails.getType().equals("group")){
            chatDetailsIcon.setImageResource(R.drawable.baseline_groups_24);
        }else {
            chatDetailsIcon.setImageResource(R.drawable.baseline_supervised_user_circle_24);
        }

        users.clear();
        users.addAll(chatDetails.getMembers());
        adapter.notifyDataSetChanged();

    }

}