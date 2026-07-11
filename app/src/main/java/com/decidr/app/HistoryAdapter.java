package com.decidr.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.decidr.app.data.DecisionEntry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<DecisionEntry> mDecisions;
    private final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());

    public void setDecisions(List<DecisionEntry> decisions) {
        this.mDecisions = decisions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DecisionEntry entry = mDecisions.get(position);
        holder.tvPrompt.setText(entry.prompt);
        holder.tvResult.setText(entry.result);
        holder.tvDate.setText(sdf.format(new Date(entry.timestamp)));
    }

    @Override
    public int getItemCount() {
        return mDecisions == null ? 0 : mDecisions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPrompt, tvResult, tvDate;
        ViewHolder(View itemView) {
            super(itemView);
            tvPrompt = itemView.findViewById(R.id.tv_prompt);
            tvResult = itemView.findViewById(R.id.tv_result);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}