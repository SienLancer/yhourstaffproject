package com.example.yhourstaffproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yhourstaffproject.R;
import com.example.yhourstaffproject.object.Salary;

import java.util.List;

public class SalaryAdapter extends RecyclerView.Adapter<SalaryAdapter.ViewHolder> {

    private List<Salary> salaries;

    public SalaryAdapter(List<Salary> salaries) {
        this.salaries = salaries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.salary_item_layout, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Salary salary = salaries.get(position);
        holder.start_date_after_payday_tv.setText("Start date after payday: " + salary.getStartDate());
        holder.current_salary_tv.setText("Current salary: " + salary.getCurrentSalary()+"");
        holder.status_salary_tv.setText("Status: " + salary.getStatus());
        holder.payday_tv.setText("Payday: " + salary.getPayDay());

//        Timekeeping timekeeping = timekeepings.get(position);
//        holder.timekeeping_name_tv.setText(timekeeping.getId());
//        holder.check_in_tv.setText(timekeeping.getCheckIn());
//        holder.check_out_tv.setText(timekeeping.getCheckOut());

    }

    @Override
    public int getItemCount() {
        return salaries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView start_date_after_payday_tv, current_salary_tv, status_salary_tv, payday_tv;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            start_date_after_payday_tv = itemView.findViewById(R.id.start_date_after_payday_tv);
            current_salary_tv = itemView.findViewById(R.id.current_salary_tv);
            status_salary_tv = itemView.findViewById(R.id.status_salary_tv);
            payday_tv = itemView.findViewById(R.id.payday_tv);

        }
    }
}

