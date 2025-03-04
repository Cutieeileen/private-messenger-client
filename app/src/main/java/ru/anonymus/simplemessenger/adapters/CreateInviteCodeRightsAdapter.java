package ru.anonymus.simplemessenger.adapters;

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
import ru.anonymus.simplemessenger.adapters.models.Right;

public class CreateInviteCodeRightsAdapter extends RecyclerView.Adapter<CreateInviteCodeRightsAdapter.RightViewHolder> {

    private List<Right> rightsList;

    public CreateInviteCodeRightsAdapter(List<Right> rightsList) {
        this.rightsList = rightsList;
    }

    @NonNull
    @Override
    public CreateInviteCodeRightsAdapter.RightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_right, parent, false);
        return new RightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CreateInviteCodeRightsAdapter.RightViewHolder holder, int position) {
        Right right = rightsList.get(position);
        holder.rightName.setText(right.getName());
        holder.checkBox.setChecked(right.isSelected());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            right.setSelected(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return rightsList.size();
    }

    public static class RightViewHolder extends RecyclerView.ViewHolder {
        TextView rightName;
        CheckBox checkBox;

        public RightViewHolder(@NonNull View itemView) {
            super(itemView);
            rightName = itemView.findViewById(R.id.rightName);
            checkBox = itemView.findViewById(R.id.rightCheckBox);
        }
    }

}
