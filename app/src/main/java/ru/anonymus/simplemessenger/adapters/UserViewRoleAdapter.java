package ru.anonymus.simplemessenger.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.anonymus.simplemessenger.R;
import ru.anonymus.simplemessenger.dto.ChatDto;
import ru.anonymus.simplemessenger.dto.RoleDto;
import ru.anonymus.simplemessenger.dto.UserDetailsDto;

public class UserViewRoleAdapter extends RecyclerView.Adapter<UserViewRoleAdapter.RoleViewHolder> {


    List<RoleDto> roleDtos;
    OnDeleteClickListener listener;
    UserDetailsDto userDetails;
    boolean showDeleteButton;

    public interface OnDeleteClickListener {
        void onDeleteClick(UserDetailsDto userDetails, RoleDto roleDto);
    }

    public UserViewRoleAdapter(List<RoleDto> roleDtos, UserDetailsDto userDetails, OnDeleteClickListener onDeleteClickListener, boolean showDeleteButton) {
        this.listener = onDeleteClickListener;
        this.userDetails = userDetails;
        this.showDeleteButton = showDeleteButton;
        this.roleDtos = roleDtos;
    }

    @NonNull
    @Override
    public RoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_view_role, parent, false);

        return new RoleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoleViewHolder holder, int position) {
        RoleDto roleDto = roleDtos.get(position);

        holder.title.setText(roleDto.getName());
        holder.relativeLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(roleDto.getColorCode())));

        if (!showDeleteButton){
            holder.deleteBtn.setEnabled(false);
            holder.deleteBtn.setVisibility(View.GONE);
        }else {
            holder.deleteBtn.setOnClickListener(view -> listener.onDeleteClick(userDetails, roleDto));
        }

    }

    @Override
    public int getItemCount() {
        return roleDtos.size();
    }

    public static class RoleViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView deleteBtn;
        RelativeLayout relativeLayout;

        public RoleViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.roleItemName);
            deleteBtn = itemView.findViewById(R.id.removeRoleButton);
            relativeLayout = itemView.findViewById(R.id.userViewRoleContainer);
        }
    }

}
