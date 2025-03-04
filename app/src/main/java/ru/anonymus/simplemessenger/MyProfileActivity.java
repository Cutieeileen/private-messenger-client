package ru.anonymus.simplemessenger;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.anonymus.simplemessenger.adapters.RolesToUserAdapter;
import ru.anonymus.simplemessenger.adapters.UserViewRoleAdapter;
import ru.anonymus.simplemessenger.adapters.models.Role;
import ru.anonymus.simplemessenger.apiconnection.ApiService;
import ru.anonymus.simplemessenger.apiconnection.RetrofitClient;
import ru.anonymus.simplemessenger.databinding.ActivityMyProfileBinding;
import ru.anonymus.simplemessenger.databinding.ActivityUserViewBinding;
import ru.anonymus.simplemessenger.dto.RoleDto;
import ru.anonymus.simplemessenger.dto.UserDetailsDto;
import ru.anonymus.simplemessenger.dto.UserUpdateRolesRequest;
import ru.anonymus.simplemessenger.storage.GlobalVariables;
import ru.anonymus.simplemessenger.storage.SecureStorage;

public class MyProfileActivity extends AppCompatActivity {


    private ActivityMyProfileBinding binding;

    private EditText username;
    private EditText description;

    private RecyclerView rolesView;
    private ImageButton addRoleButton;

    private UserDetailsDto currentUser;

    private ImageButton editNameBtn;
    private ImageButton editDescriptionBtn;

    private UserViewRoleAdapter roleAdapter;
    private List<RoleDto> rolesList;

    private boolean isEditingName = false, isEditingDescription = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rolesList = new ArrayList<>();

        binding.userViewToolbar.btnBack.setOnClickListener(view -> finish());

        username = binding.userViewUsername;
        description = binding.userViewUserDescription;

        rolesView = binding.usersRolesList;
        addRoleButton = binding.addRoleToUserButton;

        editNameBtn = binding.updateUserNameBtn;
        editDescriptionBtn = binding.updateDescriptionBtn;

        rolesView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        binding.userViewToolbar.btnMenu.setOnClickListener(v -> updateView());

        currentUser = fetchCurrentUser();

        rolesList.addAll(currentUser.getRoles());

        roleAdapter = new UserViewRoleAdapter(
                rolesList,
                currentUser,
                (userDetails1, roleDto) -> {
                    removeRole(userDetails1.getId(), roleDto.getId());
                },
                currentUser.getAuthorities().contains("ROLE_ADMIN")
        );

        rolesView.setAdapter(roleAdapter);

        if (currentUser.getAuthorities().contains("ROLE_ADMIN")){
            addRoleButton.setOnClickListener(v -> showRolesDialog(currentUser));
            addRoleButton.setVisibility(View.VISIBLE);
        }else {
            addRoleButton.setVisibility(View.GONE);
        }

        editNameBtn.setOnClickListener(v -> toggleEditing(true));
        editDescriptionBtn.setOnClickListener(v -> toggleEditing(false));

        updateView();

    }

    private void showRolesDialog(UserDetailsDto userDetails){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_add_role_to_user, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();


        RecyclerView recyclerView = dialogView.findViewById(R.id.rolesListRw);
        Button button = dialogView.findViewById(R.id.saveRolesBtn);

        List<Long> usersRolesIds = userDetails.getRoles().stream().map(RoleDto::getId).collect(Collectors.toList());
        List<Role> rolesList = getRoles().stream().map(roleDto -> new Role(roleDto.getId(), roleDto.getName(), usersRolesIds.contains(roleDto.getId()), roleDto.getColorCode())).collect(Collectors.toList());

        recyclerView.setLayoutManager(new LinearLayoutManager(dialog.getContext()));
        RolesToUserAdapter adapter = new RolesToUserAdapter(rolesList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        button.setOnClickListener(v -> {

            updateUserRoles(userDetails.getId(), rolesList.stream().filter(Role::isSelected).map(Role::getId).collect(Collectors.toList()));
            dialog.dismiss();

        });

        dialog.show();

    }

    public void updateUserRoles(Long userId, List<Long> roleIds){

        UserUpdateRolesRequest userUpdateRolesRequest = new UserUpdateRolesRequest(userId, roleIds);

        Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);
        apiService.updateUsersRoles(new SecureStorage(this).getToken(), userUpdateRolesRequest).enqueue(new Callback<UserDetailsDto>() {
            @Override
            public void onResponse(Call<UserDetailsDto> call, Response<UserDetailsDto> response) {
                if (response.isSuccessful()){
                    currentUser = response.body();
                    updateView();

                }else {
                    Toast.makeText(MyProfileActivity.this, response.code() + ": " + response.message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<UserDetailsDto> call, Throwable t) {
                Log.e("USER-UPDATE", t.getMessage());
            }
        });

    }

    private List<RoleDto> getRoles() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<RoleDto>> future = executor.submit(() -> {
            try {
                Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
                ApiService apiService = retrofit.create(ApiService.class);
                Response<List<RoleDto>> response = apiService.getRoles(new SecureStorage(this).getToken()).execute();
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

    private void toggleEditing(boolean isName) {
        if (isName) {
            if (!isEditingName) {

                username.setInputType(InputType.TYPE_CLASS_TEXT);
                username.setEnabled(true);
                username.setClickable(true);

                isEditingName = true;
                editNameBtn.setImageResource(R.drawable.sharp_done_24);
            } else {
                // Отправляем данные на сервер и выключаем режим редактирования
                String newName = username.getText().toString().trim();

                username.setInputType(InputType.TYPE_NULL);
                username.setEnabled(false);
                username.setClickable(false);

                currentUser.setUsername(newName);
                updateUser(currentUser);

                isEditingName = false;
                editNameBtn.setImageResource(R.drawable.baseline_mode_edit_24);
            }
        } else {
            if (!isEditingDescription) {
                // Включаем режим редактирования описания

                description.setInputType(InputType.TYPE_CLASS_TEXT);
                description.setEnabled(true);
                description.setClickable(true);

                isEditingDescription = true;
                editDescriptionBtn.setImageResource(R.drawable.sharp_done_24);
            } else {
                // Отправляем данные на сервер и выключаем режим редактирования
                String newDescription = description.getText().toString().trim();

                currentUser.setDescription(newDescription);
                updateUser(currentUser);

                description.setInputType(InputType.TYPE_NULL);
                description.setEnabled(false);
                description.setClickable(false);

                isEditingDescription = false;
                editDescriptionBtn.setImageResource(R.drawable.baseline_mode_edit_24);
            }
        }
    }

    private void updateUser(UserDetailsDto userDetails){

        Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);
        apiService.updateMe(new SecureStorage(this).getToken(), userDetails).enqueue(new Callback<UserDetailsDto>() {
            @Override
            public void onResponse(Call<UserDetailsDto> call, Response<UserDetailsDto> response) {
                if (response.isSuccessful()){
                    currentUser = response.body();
                    updateView();
                }else {
                    Toast.makeText(MyProfileActivity.this, response.code() + ": " + response.message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<UserDetailsDto> call, Throwable t) {
                Log.e("USER-UPDATE", t.getMessage());
            }
        });

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

    public void removeRole(Long userId, Long roleId){

        Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);
        apiService.removeRoleFromUser(new SecureStorage(this).getToken(), userId, roleId).enqueue(new Callback<UserDetailsDto>() {
            @Override
            public void onResponse(Call<UserDetailsDto> call, Response<UserDetailsDto> response) {
                if (response.isSuccessful()){
                    currentUser = response.body();
                    updateView();

                }else {
                    Toast.makeText(MyProfileActivity.this, response.code() + ": " + response.message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<UserDetailsDto> call, Throwable t) {
                Log.e("USER-UPDATE", t.getMessage());
            }
        });

    }

    public void updateView(){

        username.setText(currentUser.getUsername());
        description.setText(currentUser.getDescription());
        rolesList.clear();
        rolesList.addAll(currentUser.getRoles());
        roleAdapter.notifyDataSetChanged();

    }

}