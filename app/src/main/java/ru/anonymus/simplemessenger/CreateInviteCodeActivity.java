package ru.anonymus.simplemessenger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import ru.anonymus.simplemessenger.adapters.CreateInviteCodeRightsAdapter;
import ru.anonymus.simplemessenger.adapters.CreateInviteCodeRolesAdapter;
import ru.anonymus.simplemessenger.adapters.models.Right;
import ru.anonymus.simplemessenger.adapters.models.Role;
import ru.anonymus.simplemessenger.apiconnection.ApiService;
import ru.anonymus.simplemessenger.apiconnection.RetrofitClient;
import ru.anonymus.simplemessenger.databinding.ActivityCreateInviteCodeBinding;
import ru.anonymus.simplemessenger.dto.CreateRoleDto;
import ru.anonymus.simplemessenger.dto.GenerateInviteCodeDto;
import ru.anonymus.simplemessenger.dto.ResponseInviteCodeDto;
import ru.anonymus.simplemessenger.dto.RoleDto;
import ru.anonymus.simplemessenger.dto.UserDetailsDto;
import ru.anonymus.simplemessenger.storage.GlobalVariables;
import ru.anonymus.simplemessenger.storage.SecureStorage;

public class CreateInviteCodeActivity extends AppCompatActivity {

    private ActivityCreateInviteCodeBinding binding;

    private RecyclerView rightsView;
    private RecyclerView rolesView;
    private CreateInviteCodeRightsAdapter rightsAdapter;
    private CreateInviteCodeRolesAdapter rolesAdapter;
    private EditText resultEt;
    private List<Right> rightsList;
    private List<Role> rolesList;

    private ImageButton addRoleBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateInviteCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rightsList = new ArrayList<>();
        rolesList = new ArrayList<>();

        binding.adminToolbar.btnAdminBack.setOnClickListener(v -> finish());

        resultEt = binding.inviteCodeCreationResultTw;
        resultEt.setVisibility(View.INVISIBLE);

        addRoleBtn = binding.adminAddRoleBtn;

        rightsView = binding.inviteCodeCreationRightsRecyclerView;
        rightsView.setLayoutManager(new LinearLayoutManager(this));
        rightsAdapter = new CreateInviteCodeRightsAdapter(rightsList);
        rightsView.setAdapter(rightsAdapter);

        rolesView = binding.inviteCodeCreationRolesRecyclerView;
        rolesView.setLayoutManager(new LinearLayoutManager(this));
        rolesAdapter = new CreateInviteCodeRolesAdapter(rolesList);
        rolesView.setAdapter(rolesAdapter);

        getRoles();
        getRights();

        binding.createInviteCodeBtn.setOnClickListener(v -> {

            List<Right> selectedRights = rightsList.stream().filter(Right::isSelected).collect(Collectors.toList());
            List<Role> selectedRoles = rolesList.stream().filter(Role::isSelected).collect(Collectors.toList());
            sendCreationRequest(selectedRoles, selectedRights);

        });

        addRoleBtn.setOnClickListener(v -> showAddRoleDialog());


    }

    public void getRights(){

        String token = new SecureStorage(this).getToken();
        if (token == null) {
            Toast.makeText(this, "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CreateInviteCodeActivity.this, LoginActivity.class));
            return;
        }

        Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getAuthoritiesList(token).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        rightsList.clear();
                        rightsList.addAll(response.body().stream().map(Right::new).collect(Collectors.toList()));
                        rightsAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(CreateInviteCodeActivity.this, "Права не обнаружены.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.e("ChatsActivity", "Ошибка получения чатов: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("ChatsActivity", "Ошибка сети: " + t.getMessage());
                Toast.makeText(CreateInviteCodeActivity.this, "Ошибка загрузки прав", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getRoles(){

        String token = new SecureStorage(this).getToken();
        if (token == null) {
            Toast.makeText(this, "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CreateInviteCodeActivity.this, LoginActivity.class));
            return;
        }

        Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getRoles(token).enqueue(new Callback<List<RoleDto>>() {
            @Override
            public void onResponse(Call<List<RoleDto>> call, Response<List<RoleDto>> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        rolesList.clear();
                        rolesList.addAll(response.body().stream().map(roleDto -> new Role(roleDto.getId(), roleDto.getName(), false, roleDto.getColorCode())).collect(Collectors.toList()));
                        rolesAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(CreateInviteCodeActivity.this, "Роли не обнаружены.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.e("ChatsActivity", "Ошибка получения чатов: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<List<RoleDto>> call, Throwable t) {
                Log.e("ChatsActivity", "Ошибка сети: " + t.getMessage());
                Toast.makeText(CreateInviteCodeActivity.this, "Ошибка загрузки ролей", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void sendCreationRequest(List<Role> roles, List<Right> rights){

        String token = new SecureStorage(this).getToken();
        if (token == null) {
            Toast.makeText(this, "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CreateInviteCodeActivity.this, LoginActivity.class));
            return;
        }

        Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);

        GenerateInviteCodeDto generateInviteCodeDto = new GenerateInviteCodeDto();
        generateInviteCodeDto.setAuthorityNames(rights.stream().map(Right::getName).collect(Collectors.toList()));
        generateInviteCodeDto.setRolesIds(roles.stream().map(Role::getId).collect(Collectors.toList()));

        apiService.generateInviteCode(token, generateInviteCodeDto).enqueue(new Callback<ResponseInviteCodeDto>() {
            @Override
            public void onResponse(Call<ResponseInviteCodeDto> call, Response<ResponseInviteCodeDto> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        resultEt.setVisibility(View.VISIBLE);
                        resultEt.setText(response.body().getCode());
                    }else {
                        Toast.makeText(CreateInviteCodeActivity.this, "Ошибка.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.e("ChatsActivity", "Ошибка получения чатов: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<ResponseInviteCodeDto> call, Throwable t) {
                Log.e("ChatsActivity", "Ошибка сети: " + t.getMessage());
                Toast.makeText(CreateInviteCodeActivity.this, "Ошибка загрузки ролей", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void showAddRoleDialog() {
        // Создание диалога
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_role, null);
        builder.setView(dialogView);

        // Инициализация элементов диалога
        EditText roleNameEditText = dialogView.findViewById(R.id.rightNameEditText);
        EditText colorCodeEditText = dialogView.findViewById(R.id.colorCodeEditText);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button okButton = dialogView.findViewById(R.id.okButton);

        // Создание диалога
        AlertDialog dialog = builder.create();

        // Обработка нажатия на кнопку "Отмена"
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // Обработка нажатия на кнопку "ОК"
        okButton.setOnClickListener(v -> {
            String roleName = roleNameEditText.getText().toString();
            String colorCode = colorCodeEditText.getText().toString();

            // Проверка введенных данных
            if (roleName.isEmpty() || colorCode.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Void> future = executor.submit(() -> {
                try {
                    Retrofit retrofit = RetrofitClient.getClient(GlobalVariables.API_BASE_URL);
                    ApiService apiService = retrofit.create(ApiService.class);
                    Response<Void> response = apiService.createRole(new SecureStorage(this).getToken(), new CreateRoleDto(roleName, colorCode)).execute();
                    if (response.isSuccessful()) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });

            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }

            getRoles();

            // Закрытие диалога
            dialog.dismiss();
        });

        // Показ диалога
        dialog.show();
    }


}