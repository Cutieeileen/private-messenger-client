package ru.anonymus.simplemessenger.apiconnection;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import ru.anonymus.simplemessenger.dto.ChatDetailsDto;
import ru.anonymus.simplemessenger.dto.ChatDto;
import ru.anonymus.simplemessenger.dto.CreateChatRequest;
import ru.anonymus.simplemessenger.dto.CreateRoleDto;
import ru.anonymus.simplemessenger.dto.GenerateInviteCodeDto;
import ru.anonymus.simplemessenger.dto.LoginRequest;
import ru.anonymus.simplemessenger.dto.MessageDto;
import ru.anonymus.simplemessenger.dto.RegisterRequest;
import ru.anonymus.simplemessenger.dto.ResponseInviteCodeDto;
import ru.anonymus.simplemessenger.dto.RoleDto;
import ru.anonymus.simplemessenger.dto.TokenResponse;
import ru.anonymus.simplemessenger.dto.UserDetailsDto;
import ru.anonymus.simplemessenger.dto.UserDetailsMinDto;
import ru.anonymus.simplemessenger.dto.UserDto;
import ru.anonymus.simplemessenger.dto.UserUpdateRolesRequest;

public interface ApiService {
    @POST("/auth/register")
    Call<UserDto> register(@Body RegisterRequest request);

    @POST("/auth/login")
    Call<TokenResponse> login(@Body LoginRequest request);

    @GET("/auth/validate")
    Call<Void> validate(@Header ("Authorization") String authToken);

    @GET("/chats/my")
    Call<List<ChatDto>> getMyChats(@Header ("Authorization") String authToken);

    @POST("/chats/create")
    Call<ChatDto> createChat(@Header ("Authorization") String authToken, @Body CreateChatRequest createChatRequest);

    @GET("/users/find/{name}")
    Call<UserDetailsMinDto> findUserByName(@Header ("Authorization") String authToken, @Path("name") String name);

    @GET("/users/me")
    Call<UserDetailsDto> findMe(@Header ("Authorization") String authToken);

    @POST("/messages/send")
    Call<MessageDto> sendMessage(@Header ("Authorization") String authToken, @Body MessageDto messageDto);

    @GET("/messages/{chatId}")
    Call<List<MessageDto>> getMessagesInChat(@Header ("Authorization") String authToken, @Path("chatId") Long chatId);

    @GET("/admin/authorities/list")
    Call<List<String>> getAuthoritiesList(@Header ("Authorization") String authToken);

    @GET("/users/roles")
    Call<List<RoleDto>> getRoles(@Header ("Authorization") String authToken);

    @POST("/admin/invitecode")
    Call<ResponseInviteCodeDto> generateInviteCode(@Header ("Authorization") String authToken, @Body GenerateInviteCodeDto generateInviteCodeDto);

    @POST("/admin/roles/new")
    Call<Void> createRole(@Header ("Authorization") String authToken, @Body CreateRoleDto createRoleDto);

    @DELETE("/admin/user/{userId}/role/{roleId}")
    Call<UserDetailsDto> removeRoleFromUser(@Header ("Authorization") String authToken, @Path("userId") Long userId, @Path("roleId") Long roleId);

    @GET("/users/{userId}")
    Call<UserDetailsDto> getUserDetails(@Header ("Authorization") String authToken, @Path("userId") Long userId);

    @GET("/chats/{chatId}")
    Call<ChatDetailsDto> getChatDetails(@Header ("Authorization") String authToken, @Path("chatId") Long chatId);

    @DELETE("/admin/chats/{chatId}/users/{userId}")
    Call<ChatDetailsDto> deleteUserFromChat(@Header ("Authorization") String authToken, @Path("chatId") Long chatId, @Path("userId") Long userId);

    @GET("/users/find/full/{name}")
    Call<UserDetailsDto> findUserDetailsByName(@Header ("Authorization") String authToken, @Path("name") String name);

    @GET("/chats/{chatId}/add/{userId}")
    Call<ChatDetailsDto> addUserToExistingChat(@Header ("Authorization") String authToken, @Path("chatId") Long chatId, @Path("userId") Long userId);

    @PATCH("/admin/roles/update")
    Call<UserDetailsDto> updateUsersRoles(@Header ("Authorization") String authToken, @Body UserUpdateRolesRequest request);

    @POST("/users/me/update")
    Call<UserDetailsDto> updateMe(@Header ("Authorization") String authToken, @Body UserDetailsDto userDetailsDto);

}

