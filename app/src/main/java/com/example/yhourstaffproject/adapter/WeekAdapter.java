package com.example.yhourstaffproject.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yhourstaffproject.R;
import com.example.yhourstaffproject.activities.WeekDetailActivity;
import com.example.yhourstaffproject.object.Week;

import java.util.List;

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.ViewHolder> {

    private List<Week> weeks;

    public WeekAdapter(List<Week> weeks) {
        this.weeks = weeks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.week_item_layout, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Week week = weeks.get(position);
        String[] parts = week.getId().split(":");
        String namePart = parts[1];
        holder.weekNameTextView.setText(namePart);
        holder.startDayTextView.setText(week.getStartDay());
        holder.endDayTextView.setText(" - " + week.getEndDay());
        holder.detailWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), WeekDetailActivity.class);
                intent.putExtra("id", week.getId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return weeks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView weekNameTextView;
        TextView startDayTextView;
        TextView endDayTextView;
        ImageButton detailWeekButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            weekNameTextView = itemView.findViewById(R.id.week_name_tv);
            startDayTextView = itemView.findViewById(R.id.start_day_tv);
            endDayTextView = itemView.findViewById(R.id.end_day_tv);
            detailWeekButton = itemView.findViewById(R.id.detail_week_btn);
        }
    }
}

