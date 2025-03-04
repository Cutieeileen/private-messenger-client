package ru.anonymus.simplemessenger.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.anonymus.simplemessenger.R;
import ru.anonymus.simplemessenger.dto.MessageDto;


public class ScreenMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MessageDto> messages;
    private String currentUserName;
    private OnUsernameClickListener listener;

    public interface OnUsernameClickListener{
        void onClick(Long userId);
    }

    public ScreenMessageAdapter(List<MessageDto> messages, String currentUserName, OnUsernameClickListener onUsernameClickListener) {
        this.messages = messages;
        this.currentUserName = currentUserName;
        this.listener = onUsernameClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSender().getUsername().equals(currentUserName) ? 1 : 0; // 1 - отправленное, 0 - полученное сообщение
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageDto message = messages.get(position);

        // Отображаем имя отправителя и время
        if (holder instanceof SentMessageViewHolder) {
            SentMessageViewHolder sentHolder = (SentMessageViewHolder) holder;
            sentHolder.messageText.setText(message.getContent());
            sentHolder.senderName.setText(message.getSender().getUsername());
            if (!message.getSender().getRoles().isEmpty()){
                sentHolder.senderRole.setText(message.getSender().getRoles().get(0).getName());
                sentHolder.senderRole.setBackgroundColor(Color.parseColor(message.getSender().getRoles().get(0).getColorCode()));
            }else {
                sentHolder.senderRole.setVisibility(View.INVISIBLE);
            }

            sentHolder.timestamp.setText(message.getTimestamp());
//            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) sentHolder.container.getLayoutParams();
//            params.gravity = Gravity.END;
//            sentHolder.container.setLayoutParams(params);


        } else {
            ReceivedMessageViewHolder receivedHolder = (ReceivedMessageViewHolder) holder;
            receivedHolder.messageText.setText(message.getContent());
            receivedHolder.senderName.setText(message.getSender().getUsername());
            if (!message.getSender().getRoles().isEmpty()){
                receivedHolder.senderRole.setText(message.getSender().getRoles().get(0).getName());
                receivedHolder.senderRole.setBackgroundColor(Color.parseColor(message.getSender().getRoles().get(0).getColorCode()));
            }else {
                receivedHolder.senderRole.setVisibility(View.INVISIBLE);
            }

            receivedHolder.senderName.setOnClickListener(v -> listener.onClick(message.getSender().getId()));

            receivedHolder.timestamp.setText(message.getTimestamp());
//            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) receivedHolder.container.getLayoutParams();
//            params.gravity = Gravity.START;
//            receivedHolder.container.setLayoutParams(params);

        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // ViewHolder для отправленных сообщений
    public static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView senderName;
        TextView timestamp;
        TextView senderRole;
        LinearLayout container;

        public SentMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            senderName = itemView.findViewById(R.id.senderName);
            senderRole = itemView.findViewById(R.id.sentSenderRole);
            timestamp = itemView.findViewById(R.id.timestamp);
            container = itemView.findViewById(R.id.messageContainer);
        }
    }

    // ViewHolder для полученных сообщений
    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView senderName;
        TextView timestamp;
        TextView senderRole;
        LinearLayout container;

        public ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            senderName = itemView.findViewById(R.id.senderName);
            senderRole = itemView.findViewById(R.id.receivedSenderRole);
            timestamp = itemView.findViewById(R.id.timestamp);
            container = itemView.findViewById(R.id.messageContainer);
        }
    }
}

