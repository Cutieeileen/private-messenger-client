package ru.anonymus.simplemessenger.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import ru.anonymus.simplemessenger.R;
import ru.anonymus.simplemessenger.dto.ChatDto;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatDto> chatList;
    private OnChatClickListener listener;
    private String currentUsername;

    public interface OnChatClickListener {
        void onChatClick(ChatDto chat);
    }

    public ChatAdapter(List<ChatDto> chatList, OnChatClickListener listener, String currentUsername) {
        this.chatList = chatList;
        this.listener = listener;
        this.currentUsername = currentUsername;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatDto chat = chatList.get(position);

        if (chat.getType().equals("direct")){
            Log.d("CHAT-ADAPTER", "Chat type: direct");

            for (String name : chat.getMembers()){
                Log.d("CHAT-ADAPTER-CYCLE", "User: " + name);
                if (!currentUsername.equals(name)){
                    holder.title.setText(name);
                    Log.d("CHAT-ADAPTER-TITLE", "Title: " + name);
                    break;
                }
            }

            holder.chatIcon.setImageResource(R.drawable.baseline_supervised_user_circle_24);

        }else if (chat.getType().equals("group")){
            holder.title.setText(chat.getTitle());
            holder.chatIcon.setImageResource(R.drawable.baseline_groups_24);
        }


        if (chat.getLastMessage() != null){
            if (chat.getLastMessage().getContent().split("\n")[0].length() >=10){
                String msgTxt = chat.getLastMessage().getContent().split("\n")[0].substring(0, 10) + "...";
                holder.lastMessage.setText(chat.getLastMessage().getSender().getUsername() + ": " + msgTxt);
            }else {
                if (chat.getLastMessage().getContent().length() >= 10){
                    holder.lastMessage.setText(chat.getLastMessage().getSender().getUsername() + ": " + chat.getLastMessage().getContent().split("\n")[0] + "...");
                }else {
                    holder.lastMessage.setText(chat.getLastMessage().getSender().getUsername() + ": " + chat.getLastMessage().getContent().split("\n")[0]);
                }

            }
        }
        else holder.lastMessage.setText("");
        holder.lastMessageTime.setVisibility(View.INVISIBLE);
        holder.itemView.setOnClickListener(v -> listener.onChatClick(chat));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView title, lastMessage, lastMessageTime;
        ImageView chatIcon;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.chatName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            lastMessageTime = itemView.findViewById(R.id.lastMessageTime);
            chatIcon = itemView.findViewById(R.id.chatIcon);
        }
    }
}

