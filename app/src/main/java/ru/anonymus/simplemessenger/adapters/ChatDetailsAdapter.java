package ru.anonymus.simplemessenger.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.anonymus.simplemessenger.R;
import ru.anonymus.simplemessenger.dto.UserDetailsDto;

public class ChatDetailsAdapter extends RecyclerView.Adapter<ChatDetailsAdapter.ChatViewHolder>{

    private List<UserDetailsDto> users;
    private OnUserClickListener userClickListener;
    private OnDeleteClickListener deleteClickListener;
    private Boolean viewAdminButtons;

    public interface OnUserClickListener {
        void onUserClick(UserDetailsDto userDetails);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(UserDetailsDto userDetails);
    }

    public ChatDetailsAdapter(List<UserDetailsDto> users, OnUserClickListener userClickListener, OnDeleteClickListener deleteClickListener, Boolean viewAdminButtons) {
        this.users = users;
        this.userClickListener = userClickListener;
        this.deleteClickListener = deleteClickListener;
        this.viewAdminButtons = viewAdminButtons;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_details_user, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        UserDetailsDto user = users.get(position);

        holder.userName.setText(user.getUsername());
        holder.container.setOnClickListener(v -> userClickListener.onUserClick(user));
        holder.deleteButton.setOnClickListener(v -> deleteClickListener.onDeleteClick(user));

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout container;
        TextView userName;
        ImageView userIcon;
        ImageButton deleteButton;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.userDetailsContainer);
            userName = itemView.findViewById(R.id.userName);
            userIcon = itemView.findViewById(R.id.userIcon);
            deleteButton = itemView.findViewById(R.id.deleteUserFromChatBtn);
        }
    }

}
