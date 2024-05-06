package com.example.yhourstaffproject.activities;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yhourstaffproject.R;
import com.example.yhourstaffproject.adapter.SalaryAdapter;
import com.example.yhourstaffproject.object.Salary;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalaryActivity extends AppCompatActivity {
    TextView total_salary_tv, status_salary_tv, start_date_salary_tv, payday_salary_tv, dialog_title, dialog_message;
    Button received_salary_btn, button_yes, button_no;

    private RecyclerView recyclerView;
    private SalaryAdapter adapter;
    private List<Salary> salaries = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Dialog dialog;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_salary);

        total_salary_tv = findViewById(R.id.total_salary_tv);
        start_date_salary_tv = findViewById(R.id.start_date_salary_tv);
        received_salary_btn = findViewById(R.id.received_salary_btn);


        recyclerView = findViewById(R.id.salary_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SalaryAdapter(salaries);
        recyclerView.setAdapter(adapter);

        dialog=new Dialog(SalaryActivity.this);
        dialog.setContentView(R.layout.custom_yes_no_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_title = dialog.findViewById(R.id.dialog_title);
        dialog_message = dialog.findViewById(R.id.dialog_message);

        dialog_title.setText("Received Salary");
        dialog_message.setText("Do you want to receive salary?");



        button_yes =dialog.findViewById(R.id.button_yes);
        button_no =dialog.findViewById(R.id.button_no);



        received_salary_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                button_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmationOfSalaryReceipt();
                        dialog.dismiss();
                    }
                });

                button_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });

        loadDataFromFirebase();
        loadDataList();
    }

    private void loadDataList() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        if (user != null) {
            firebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                    Log.d(TAG, "Owner Shop ID: " + ownerShopId);
                    if (ownerShopId != null) {
                        firebaseDatabase.getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    String userKey = userSnapshot.getKey();


                                    if (userKey != null && userKey.equals(userId)){

                                        for (DataSnapshot timekeepingSnapshot : snapshot.child(userKey).child("salary").getChildren()) {
                                            String salaryKey = timekeepingSnapshot.getKey();

                                            String sid = timekeepingSnapshot.child("id").getValue(String.class);
                                            String startDate = timekeepingSnapshot.child("startDate").getValue(String.class);
                                            Integer currentSalary = timekeepingSnapshot.child("currentSalary").getValue(Integer.class);
                                            String status = timekeepingSnapshot.child("status").getValue(String.class);
                                            String payDay = timekeepingSnapshot.child("payDay").getValue(String.class);


                                            Log.d(TAG, "Salary Key: " + salaryKey);
                                            Log.d(TAG, "Start Date: " + startDate);
                                            Log.d(TAG, "Status: " + status);
                                            Log.d(TAG, "Pay Day: " + payDay);
                                            Log.d(TAG, "Current Salary: " + currentSalary);

                                            salaries.add(new Salary(sid,currentSalary, status, startDate, payDay));
                                            adapter.notifyDataSetChanged();


                                        }


                                        return; // Kết thúc vòng lặp sau khi tìm thấy tuần có ID trùng khớp
                                    }


                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(SalaryActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(SalaryActivity.this, "Shop not found", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            Toast.makeText(SalaryActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }


    }

    private void confirmationOfSalaryReceipt() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        long timestamp = System.currentTimeMillis();
        if (user != null) {
            firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    LocalDateTime now = LocalDateTime.now();
                    int year = now.getYear();
                    int month = now.getMonthValue();
                    int day = now.getDayOfMonth();
                    int hour = now.getHour();
                    int minute = now.getMinute();
                    String minuteFormatted = String.format("%02d", minute);

                    String dateForTimeKeeping = day + " " + month + " " + year;
                    String dateForPayDay = day + "/" + month + "/" + year;
                    setDataStatusBefore();
                    DatabaseReference userReference = firebaseDatabase.getReference("User").child(userId).child("salary");

                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String id = timestamp + "salary" + dateForTimeKeeping; // Tạo id mới
                            DatabaseReference newTimekeepingRef = userReference.child(id);

                            if (!snapshot.child(id).exists()) {

                                String payDayCurrent = "";
                                Integer currentSalary = 0;
                                String status = "Not paid yet";

                                Map<String, Object> timekeepingData = new HashMap<>();
                                timekeepingData.put("id", id);
                                timekeepingData.put("currentSalary", currentSalary);
                                timekeepingData.put("status", status);
                                timekeepingData.put("payDay", payDayCurrent);
                                timekeepingData.put("startDate", dateForPayDay);


                                newTimekeepingRef.setValue(timekeepingData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Data added successfully");

                                        } else {
                                            Log.d(TAG, "Failed to add data");
                                        }
                                    }
                                });
                            } else {
                                Log.d(TAG, "Data already exists for today");
                                Toast.makeText(SalaryActivity.this, "Data already exists for today", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SalaryActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SalaryActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(SalaryActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDataFromFirebase() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = firebaseDatabase.getReference("User");

            userRef.child(userId).child("shopID").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String ownerShopId = snapshot.getValue(String.class);
                    if (ownerShopId != null) {
                        userRef.child(userId).child("salary").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot salarySnapshot : snapshot.getChildren()) {
                                    String status = salarySnapshot.child("status").getValue(String.class);
                                    if (status != null && status.equals("Not paid yet")) {
                                        // Only process data if status is "Not paid yet"
                                        String salaryKey = salarySnapshot.getKey();
                                        String startDate = salarySnapshot.child("startDate").getValue(String.class);
                                        Integer currentSalary = salarySnapshot.child("currentSalary").getValue(Integer.class);
                                        String payDay = salarySnapshot.child("payDay").getValue(String.class);

                                        DecimalFormat formatter = new DecimalFormat("#,###");
                                        String formattedSalary = formatter.format(currentSalary);

                                        // Update UI with data from relevant key
                                        total_salary_tv.setText(formattedSalary + " VND");
                                        start_date_salary_tv.setText(startDate);
                                        // Exit loop after processing one entry (optional, depending on your requirement)
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                handleError(error);
                            }
                        });
                    } else {
                        handleShopNotFound();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    handleError(error);
                }
            });
        } else {
            showUserNotLoggedInError();
        }
    }




    private void setDataStatusBefore() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = firebaseDatabase.getReference("User");

            userRef.child(userId).child("shopID").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String ownerShopId = snapshot.getValue(String.class);
                    if (ownerShopId != null) {
                        userRef.child(userId).child("salary").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot salarySnapshot : snapshot.getChildren()) {
                                    String status = salarySnapshot.child("status").getValue(String.class);
                                    if (status != null && status.equals("Not paid yet")) {
                                        LocalDateTime now = LocalDateTime.now();
                                        int year = now.getYear();
                                        int month = now.getMonthValue();
                                        int day = now.getDayOfMonth();
                                        int hour = now.getHour();
                                        int minute = now.getMinute();
                                        String minuteFormatted = String.format("%02d", minute);

                                        String dateForPayDay = day + "/" + month + "/" + year + " " + hour + ":" + minuteFormatted;
                                        // Update status to "Received Salary" in database
                                        salarySnapshot.getRef().child("status").setValue("Received Salary");
                                        salarySnapshot.getRef().child("payDay").setValue(dateForPayDay);

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                handleError(error);
                            }
                        });
                    } else {
                        handleShopNotFound();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    handleError(error);
                }
            });
        } else {
            showUserNotLoggedInError();
        }
    }

    private void handleError(DatabaseError error) {
        // Handle Firebase Database error
        Log.e(TAG, "Firebase Database Error: " + error.getMessage());
        Toast.makeText(SalaryActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void handleShopNotFound() {
        // Handle case when shop is not found
        Toast.makeText(SalaryActivity.this, "Shop not found", Toast.LENGTH_SHORT).show();
    }

    private void showUserNotLoggedInError() {
        // Handle case when user is not logged in
        Toast.makeText(SalaryActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
    }

    private void showCustomToast(String message) {
        // Inflate layout cho Toast
        View layout = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_container));

        // Thiết lập nội dung của Toast
        TextView textView = layout.findViewById(R.id.custom_toast_text);
        textView.setText(message);

        // Tạo một Toast và đặt layout của nó
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }


}