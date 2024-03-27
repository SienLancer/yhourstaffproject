package com.example.yhourstaffproject.activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.yhourstaffproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SalaryActivity extends AppCompatActivity {
    TextView total_salary_tv, status_salary_tv;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_salary);

        total_salary_tv = findViewById(R.id.total_salary_tv);
        status_salary_tv = findViewById(R.id.status_salary_tv);

        loadDataFromFirebase();
    }

    private void loadDataFromFirebase() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        if (user != null) {
            firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                    Log.d(TAG, "Owner Shop ID: " + ownerShopId);
                    if (ownerShopId != null) {
                        firebaseDatabase.getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Integer currentSalary = snapshot.child(userId).child("salary").child("currentSalary").getValue(Integer.class);
                                String statusSalary = snapshot.child(userId).child("salary").child("status").getValue(String.class);

                                Log.d(TAG, "Current Salary: " + currentSalary);
                                Log.d(TAG, "Status Salary: " + statusSalary);
                                total_salary_tv.setText(currentSalary+"");
                                status_salary_tv.setText(statusSalary);


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
}