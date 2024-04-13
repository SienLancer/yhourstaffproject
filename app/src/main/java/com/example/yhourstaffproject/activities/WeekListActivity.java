package com.example.yhourstaffproject.activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yhourstaffproject.R;
import com.example.yhourstaffproject.adapter.WeekAdapter;
import com.example.yhourstaffproject.object.Week;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WeekListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WeekAdapter adapter;
    private List<Week> weekList = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_list);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeekAdapter(weekList);
        recyclerView.setAdapter(adapter);

        // Load data from Firebase
        loadDataFromFirebase();
    }

    private void loadDataFromFirebase() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        if (user != null) {
            firebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                        Log.d(TAG, "Owner Shop ID: " + ownerShopId);
                        if (ownerShopId != null) {
                            firebaseDatabase.getReference("Shop").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    boolean shopFound = false;
                                    for (DataSnapshot shopSnapshot : snapshot.getChildren()) {
                                        String shopKey = shopSnapshot.getKey();
                                        Log.d(TAG, "Shop Key: " + shopKey);
                                        if (ownerShopId.equals(shopKey)) {
                                            shopFound = true;
                                            //Toast.makeText(NewCalendarActivity.this, "Shop found", Toast.LENGTH_SHORT).show();
                                            // Thực hiện các hành động cần thiết khi tìm thấy cửa hàng

                                            for (DataSnapshot calendarSnapshot : shopSnapshot.child("Calendar").getChildren()) {
                                                String weekId = calendarSnapshot.getKey();
                                                String startDay = calendarSnapshot.child("startDay").getValue(String.class);
                                                String endDay = calendarSnapshot.child("endDay").getValue(String.class);
                                                Log.d(TAG, "Week Key: " + weekId);
                                                Log.d(TAG, "Start Day: " + startDay);
                                                Log.d(TAG, "End Day: " + endDay);
                                                Week week = new Week(weekId, startDay, endDay);
                                                weekList.add(week);
                                                adapter.notifyDataSetChanged();

                                            }

                                            break; // Kết thúc vòng lặp khi đã tìm thấy cửa hàng
                                        }
                                    }
                                    if (!shopFound) {
                                        Toast.makeText(WeekListActivity.this, "List not found", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(WeekListActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(WeekListActivity.this, "Shop not found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(WeekListActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(WeekListActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(WeekListActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }


}
