package ru.anonymus.simplemessenger.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.anonymus.simplemessenger.R;
import ru.anonymus.simplemessenger.adapters.models.Role;

public class RolesToUserAdapter extends RecyclerView.Adapter<RolesToUserAdapter.RoleViewHolder>{

    private List<Role> rolesList = new ArrayList<>();

    public RolesToUserAdapter(List<Role> rolesList) {
        this.rolesList = rolesList;
    }

    @NonNull
    @Override
    public RoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_role, parent, false);
        return new RoleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoleViewHolder holder, int position) {

        Role role = rolesList.get(position);
        holder.roleName.setText(role.getName());
        holder.checkBox.setChecked(role.isSelected());

        if (!role.getColorCode().isEmpty()){
            try {
                holder.roleName.setTextColor(Color.parseColor(role.getColorCode()));
            }catch (Exception e){
                Log.e("Color", e.getMessage());
            }

        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            role.setSelected(isChecked);
        });

    }

    @Override
    public int getItemCount() {
        return rolesList.size();
    }

    public static class RoleViewHolder extends RecyclerView.ViewHolder {
        TextView roleName;
        CheckBox checkBox;

        public RoleViewHolder(@NonNull View itemView) {
            super(itemView);
            roleName = itemView.findViewById(R.id.roleName);
            checkBox = itemView.findViewById(R.id.roleCheckBox);
        }
    }

}
