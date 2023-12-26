package com.example.examenfinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private List<String> historialList;
    private LayoutInflater inflater;

    public HistorialAdapter(Context context, List<String> historialList) {
        this.inflater = LayoutInflater.from(context);
        this.historialList = historialList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_historial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String historialItem = historialList.get(position);
        holder.bind(historialItem);
    }

    @Override
    public int getItemCount() {
        return historialList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView historialTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            historialTextView = itemView.findViewById(R.id.textViewHistorialItem);
        }

        public void bind(String historialItem) {
            historialTextView.setText(historialItem);
        }
    }
}