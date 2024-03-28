package com.example.yhourstaffproject.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yhourstaffproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WeekDetailActivity extends AppCompatActivity {

    TextView realtime_table;
    String weekId;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ValueEventListener listener;
    TextView Sun1,Sun2,Sun3,
            Mon1,Mon2,Mon3,
            Tue1,Tue2,Tue3,
            Wed1,Wed2,Wed3,
            Thu1,Thu2,Thu3,
            Fri1,Fri2,Fri3,
            Sat1,Sat2,Sat3,
            morningSstart, morningSend, afternoonSstart, afternoonSend, eveningSstart, eveningSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_week_detail);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        init();
        getAndSetIntentData();
        getDataTable();

    }


    public void getDataTable(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String ownerShopId = snapshot.getValue(String.class);
                    if (ownerShopId != null) {
                        DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");
                        shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot weekSnapshot : dataSnapshot.getChildren()) {
                                    // Lấy ID của tuần
                                    String weekSnapshotId = weekSnapshot.getKey();
                                    // So sánh ID của tuần với weekId
                                    if (weekSnapshotId != null && weekSnapshotId.equals(weekId)) {
                                        // Hiển thị dữ liệu từ tuần có ID trùng khớp lên giao diện người dùng
                                        realtime_table.setText(weekSnapshot.child("startDay").getValue(String.class));
                                        Mon1.setText(weekSnapshot.child("mon1").getValue(String.class));
                                        Mon2.setText(weekSnapshot.child("mon2").getValue(String.class));
                                        Mon3.setText(weekSnapshot.child("mon3").getValue(String.class));
                                        Tue1.setText(weekSnapshot.child("tue1").getValue(String.class));
                                        Tue2.setText(weekSnapshot.child("tue2").getValue(String.class));
                                        Tue3.setText(weekSnapshot.child("tue3").getValue(String.class));

                                        Wed1.setText(weekSnapshot.child("wed1").getValue(String.class));
                                        Wed2.setText(weekSnapshot.child("wed2").getValue(String.class));
                                        Wed3.setText(weekSnapshot.child("wed3").getValue(String.class));

                                        Thu1.setText(weekSnapshot.child("thu1").getValue(String.class));
                                        Thu2.setText(weekSnapshot.child("thu2").getValue(String.class));
                                        Thu3.setText(weekSnapshot.child("thu3").getValue(String.class));
                                        Fri1.setText(weekSnapshot.child("fri1").getValue(String.class));
                                        Fri2.setText(weekSnapshot.child("fri2").getValue(String.class));
                                        Fri3.setText(weekSnapshot.child("fri3").getValue(String.class));
                                        Sat1.setText(weekSnapshot.child("sat1").getValue(String.class));
                                        Sat2.setText(weekSnapshot.child("sat2").getValue(String.class));
                                        Sat3.setText(weekSnapshot.child("sat3").getValue(String.class));

                                        Sun1.setText(weekSnapshot.child("sun1").getValue(String.class));
                                        Sun2.setText(weekSnapshot.child("sun2").getValue(String.class));
                                        Sun3.setText(weekSnapshot.child("sun3").getValue(String.class));

                                        morningSstart.setText(weekSnapshot.child("morningSstart").getValue(String.class));
                                        morningSend.setText(weekSnapshot.child("morningSend").getValue(String.class));
                                        afternoonSstart.setText(weekSnapshot.child("afternoonSstart").getValue(String.class));
                                        afternoonSend.setText(weekSnapshot.child("afternoonSend").getValue(String.class));
                                        eveningSstart.setText(weekSnapshot.child("eveningSstart").getValue(String.class));
                                        eveningSend.setText(weekSnapshot.child("eveningSend").getValue(String.class));

                                        return; // Kết thúc vòng lặp sau khi tìm thấy tuần có ID trùng khớp
                                    }
                                }
                                // Nếu không tìm thấy tuần nào có ID trùng khớp, hiển thị thông báo hoặc xử lý phù hợp
                                Toast.makeText(WeekDetailActivity.this, "Week not found", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(WeekDetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(WeekDetailActivity.this, "Shop ID not found for this user", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(WeekDetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(WeekDetailActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") ) {
            // Geting Data from Intent

            weekId = getIntent().getStringExtra("id");



        }else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }



    public void init(){
        Sun1=findViewById(R.id.Sunday1);
        Sun2=findViewById(R.id.Sunday2);
        Sun3=findViewById(R.id.Sunday3);

        Mon1=findViewById(R.id.Monday1);
        Mon2=findViewById(R.id.Monday2);
        Mon3=findViewById(R.id.Monday3);

        Tue1=findViewById(R.id.Tuesday1);
        Tue2=findViewById(R.id.Tuesday2);
        Tue3=findViewById(R.id.Tuesday3);

        Wed1=findViewById(R.id.Wednesday1);
        Wed2=findViewById(R.id.Wednesday2);
        Wed3=findViewById(R.id.Wednesday3);


        Thu1=findViewById(R.id.Thursday1);
        Thu2=findViewById(R.id.Thursday2);
        Thu3=findViewById(R.id.Thursday3);


        Fri1=findViewById(R.id.Friday1);
        Fri2=findViewById(R.id.Friday2);
        Fri3=findViewById(R.id.Friday3);

        Sat1=findViewById(R.id.Saturday1);
        Sat2=findViewById(R.id.Saturday2);
        Sat3=findViewById(R.id.Saturday3);

        morningSstart=findViewById(R.id.morningSstart);
        morningSend=findViewById(R.id.morningSend);
        afternoonSstart=findViewById(R.id.afternoonSstart);
        afternoonSend=findViewById(R.id.afternoonSend);
        eveningSstart=findViewById(R.id.eveningSstart);
        eveningSend=findViewById(R.id.eveningSend);

        realtime_table = findViewById(R.id.realtime_table);




    }
}