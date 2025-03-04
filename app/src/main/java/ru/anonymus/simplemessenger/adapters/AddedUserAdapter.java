package ru.anonymus.simplemessenger.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.anonymus.simplemessenger.R;
import ru.anonymus.simplemessenger.dto.UserDetailsMinDto;

public class AddedUserAdapter extends RecyclerView.Adapter<AddedUserAdapter.AddedUserViewHolder> {

    private List<UserDetailsMinDto> addedUsers;

    public AddedUserAdapter(List<UserDetailsMinDto> addedUsers) {
        this.addedUsers = addedUsers;
    }

    @NonNull
    @Override
    public AddedUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_details_user, parent, false);
        return new AddedUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddedUserViewHolder holder, int position) {
        UserDetailsMinDto user = addedUsers.get(position);
        holder.username.setText(user.getUsername());

        holder.deleteButton.setOnClickListener(v -> {
            addedUsers.remove(user);
            this.notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return addedUsers.size();
    }

    public static class AddedUserViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        ImageButton deleteButton;

        public AddedUserViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.userName);
            deleteButton = itemView.findViewById(R.id.deleteUserFromChatBtn);
        }
    }
}

