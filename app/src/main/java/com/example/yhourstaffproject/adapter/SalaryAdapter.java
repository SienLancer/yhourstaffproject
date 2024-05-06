package com.example.yhourstaffproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yhourstaffproject.R;
import com.example.yhourstaffproject.object.Salary;

import java.text.DecimalFormat;
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
        holder.start_date_after_payday_tv.setText("Start date: " + salary.getStartDate());

        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedSalary = formatter.format(salary.getCurrentSalary());
        holder.current_salary_tv.setText(formattedSalary);
        String payDay = salary.getPayDay();
        if (payDay.equals("")) {
            // Nếu ngày thanh toán là null, thiết lập hình ảnh tương ứng
            holder.received_salary_iv.setImageResource(R.drawable.received_yet_ic); // Thay thế R.drawable.null_payday_image với ID hình ảnh thích hợp
            holder.payday_tv.setText("Payday: " + payDay);
        } else {
            holder.received_salary_iv.setImageResource(R.drawable.check_salary_ic);
            holder.payday_tv.setText("Payday: " + payDay);
            // Nếu ngày thanh toán không null, không cần thiết lập hình ảnh
        }




    }

    @Override
    public int getItemCount() {
        return salaries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView start_date_after_payday_tv, current_salary_tv, payday_tv;
        ImageView received_salary_iv;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            start_date_after_payday_tv = itemView.findViewById(R.id.start_date_after_payday_tv);
            current_salary_tv = itemView.findViewById(R.id.current_salary_tv);
            payday_tv = itemView.findViewById(R.id.payday_tv);
            received_salary_iv = itemView.findViewById(R.id.received_salary_iv);
        }
    }
}

