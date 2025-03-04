package ru.anonymus.simplemessenger;

import android.util.Log;

import io.reactivex.disposables.Disposable;
import okhttp3.*;
import ru.anonymus.simplemessenger.storage.GlobalVariables;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

import java.util.Map;

public class WebSocketClient {

    private OkHttpClient client;
    private WebSocket webSocket;
    private String serverUrl;
    private String authToken;

    private StompClient stompClient;
    private Disposable topicSubscription;
    private WebSocketCallback callback;


    public WebSocketClient(WebSocketCallback callback, String authToken) {
        this.serverUrl = GlobalVariables.WS_BASE_URL + "/ws/websocket";
        this.callback = callback;
        this.authToken = authToken;
    }


    public void connectToChat(String chatId) {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, serverUrl, Map.of("Authorization", authToken));
        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d("WebSocket", "Connected to WebSocket");
                    break;
                case ERROR:
                    Log.e("WebSocket", "Error: ", lifecycleEvent.getException());
                    break;
                case CLOSED:
                    Log.d("WebSocket", "Disconnected");
                    break;
            }
        });

        stompClient.connect();

        // Подписываемся на чат
        topicSubscription = stompClient.topic("/topic/chat/" + chatId)
                .subscribe(message -> {
                    if (callback != null) {
                        callback.onMessageReceived(message.getPayload());
                    }
                }, throwable -> Log.e("WebSocket", "Subscription error", throwable));
    }


    public StompClient connect() {

        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d("WebSocket", "Connected to WebSocket");
                    break;
                case ERROR:
                    Log.e("WebSocket", "Error: ", lifecycleEvent.getException());
                    break;
                case CLOSED:
                    Log.d("WebSocket", "Disconnected");
                    break;
            }
        });

        stompClient.connect();

        return stompClient;

    }

    public void subscribe(){

    }

    public void disconnect() {
        if (topicSubscription != null) {
            topicSubscription.dispose();
        }
        if (stompClient != null) {
            stompClient.disconnect();
        }
    }

    public interface WebSocketCallback {
        void onMessageReceived(String message);
    }

}

