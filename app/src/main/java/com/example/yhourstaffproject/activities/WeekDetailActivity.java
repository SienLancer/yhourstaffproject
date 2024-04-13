package com.example.yhourstaffproject.activities;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.yhourstaffproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WeekDetailActivity extends AppCompatActivity {

    TextView realtime_table;
    ViewFlipper viewFlipper;
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
            morningSstart, morningSend, afternoonSstart, afternoonSend, eveningSstart, eveningSend,
            morningSstart_tue, morningSend_tue, afternoonSstart_tue, afternoonSend_tue, eveningSstart_tue, eveningSend_tue,
            morningSstart_wed, morningSend_wed, afternoonSstart_wed, afternoonSend_wed, eveningSstart_wed, eveningSend_wed,
            morningSstart_thu, morningSend_thu, afternoonSstart_thu, afternoonSend_thu, eveningSstart_thu, eveningSend_thu,
            morningSstart_fri, morningSend_fri, afternoonSstart_fri, afternoonSend_fri, eveningSstart_fri, eveningSend_fri,
            morningSstart_sat, morningSend_sat, afternoonSstart_sat, afternoonSend_sat, eveningSstart_sat, eveningSend_sat,
            morningSstart_sun, morningSend_sun, afternoonSstart_sun, afternoonSend_sun, eveningSstart_sun, eveningSend_sun,
            start_end_date_tv, title_timetable_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_week_detail);
        init();
        getAndSetIntentData();
        getDataTable();


        viewFlipper.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        if (startX < endX) {
                            // Vuốt sang phải
                            viewFlipper.setInAnimation(AnimationUtils.loadAnimation(WeekDetailActivity.this, R.anim.slide_in_left_viewfliper));
                            viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(WeekDetailActivity.this, R.anim.slide_out_left_viewfliper));
                            viewFlipper.showPrevious();
                        } else if (startX > endX) {
                            // Vuốt sang trái
                            viewFlipper.setInAnimation(AnimationUtils.loadAnimation(WeekDetailActivity.this, R.anim.slide_in_right_viewfliper));
                            viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(WeekDetailActivity.this, R.anim.slide_out_right_viewfliper));
                            viewFlipper.showNext();
                        }
                        break;
                }
                return true;
            }




        });

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
                                        String nameWeek = weekSnapshot.child("id").getValue(String.class);
                                        String[] parts = nameWeek.split(":");
                                        String namePart = parts[1];
                                        title_timetable_tv.setText(namePart);
                                        start_end_date_tv.setText(weekSnapshot.child("startDay").getValue(String.class) + " - " + weekSnapshot.child("endDay").getValue(String.class));
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

                                        morningSstart.setText(weekSnapshot.child("morningSStart").getValue(String.class));
                                        morningSend.setText(weekSnapshot.child("morningSend").getValue(String.class));
                                        afternoonSstart.setText(weekSnapshot.child("afternoonSStart").getValue(String.class));
                                        afternoonSend.setText(weekSnapshot.child("afternoonSend").getValue(String.class));
                                        eveningSstart.setText(weekSnapshot.child("eveningSStart").getValue(String.class));
                                        eveningSend.setText(weekSnapshot.child("eveningSend").getValue(String.class));
                                        morningSstart_tue.setText(weekSnapshot.child("morningSStart").getValue(String.class));
                                        morningSend_tue.setText(weekSnapshot.child("morningSend").getValue(String.class));
                                        afternoonSstart_tue.setText(weekSnapshot.child("afternoonSStart").getValue(String.class));
                                        afternoonSend_tue.setText(weekSnapshot.child("afternoonSend").getValue(String.class));
                                        eveningSstart_tue.setText(weekSnapshot.child("eveningSStart").getValue(String.class));
                                        eveningSend_tue.setText(weekSnapshot.child("eveningSend").getValue(String.class));
                                        morningSstart_wed.setText(weekSnapshot.child("morningSStart").getValue(String.class));
                                        morningSend_wed.setText(weekSnapshot.child("morningSend").getValue(String.class));
                                        afternoonSstart_wed.setText(weekSnapshot.child("afternoonSStart").getValue(String.class));
                                        afternoonSend_wed.setText(weekSnapshot.child("afternoonSend").getValue(String.class));
                                        eveningSstart_wed.setText(weekSnapshot.child("eveningSStart").getValue(String.class));
                                        eveningSend_wed.setText(weekSnapshot.child("eveningSend").getValue(String.class));
                                        morningSstart_thu.setText(weekSnapshot.child("morningSStart").getValue(String.class));
                                        morningSend_thu.setText(weekSnapshot.child("morningSend").getValue(String.class));
                                        afternoonSstart_thu.setText(weekSnapshot.child("afternoonSStart").getValue(String.class));
                                        afternoonSend_thu.setText(weekSnapshot.child("afternoonSend").getValue(String.class));
                                        eveningSstart_thu.setText(weekSnapshot.child("eveningSStart").getValue(String.class));
                                        eveningSend_thu.setText(weekSnapshot.child("eveningSend").getValue(String.class));
                                        morningSstart_fri.setText(weekSnapshot.child("morningSStart").getValue(String.class));
                                        morningSend_fri.setText(weekSnapshot.child("morningSend").getValue(String.class));
                                        afternoonSstart_fri.setText(weekSnapshot.child("afternoonSStart").getValue(String.class));
                                        afternoonSend_fri.setText(weekSnapshot.child("afternoonSend").getValue(String.class));
                                        eveningSstart_fri.setText(weekSnapshot.child("eveningSStart").getValue(String.class));
                                        eveningSend_fri.setText(weekSnapshot.child("eveningSend").getValue(String.class));
                                        morningSstart_sat.setText(weekSnapshot.child("morningSStart").getValue(String.class));
                                        morningSend_sat.setText(weekSnapshot.child("morningSend").getValue(String.class));
                                        afternoonSstart_sat.setText(weekSnapshot.child("afternoonSStart").getValue(String.class));
                                        afternoonSend_sat.setText(weekSnapshot.child("afternoonSend").getValue(String.class));
                                        eveningSstart_sat.setText(weekSnapshot.child("eveningSStart").getValue(String.class));
                                        eveningSend_sat.setText(weekSnapshot.child("eveningSend").getValue(String.class));
                                        morningSstart_sun.setText(weekSnapshot.child("morningSStart").getValue(String.class));
                                        morningSend_sun.setText(weekSnapshot.child("morningSend").getValue(String.class));
                                        afternoonSstart_sun.setText(weekSnapshot.child("afternoonSStart").getValue(String.class));
                                        afternoonSend_sun.setText(weekSnapshot.child("afternoonSend").getValue(String.class));
                                        eveningSstart_sun.setText(weekSnapshot.child("eveningSStart").getValue(String.class));
                                        eveningSend_sun.setText(weekSnapshot.child("eveningSend").getValue(String.class));

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

        viewFlipper = findViewById(R.id.view_flipper);
        start_end_date_tv = findViewById(R.id.start_end_date_tv);
        title_timetable_tv = findViewById(R.id.title_timetable_tv);


        Mon1=findViewById(R.id.Monday1);
        Mon2=findViewById(R.id.Monday2);
        Mon3=findViewById(R.id.Monday3);

        Tue1 = findViewById(R.id.Tuesday1);
        Tue2 = findViewById(R.id.Tuesday2);
        Tue3 = findViewById(R.id.Tuesday3);

        Wed1 = findViewById(R.id.Wednesday1);
        Wed2 = findViewById(R.id.Wednesday2);
        Wed3 = findViewById(R.id.Wednesday3);

        Thu1 = findViewById(R.id.Thursday1);
        Thu2 = findViewById(R.id.Thursday2);
        Thu3 = findViewById(R.id.Thursday3);

        Fri1 = findViewById(R.id.Friday1);
        Fri2 = findViewById(R.id.Friday2);
        Fri3 = findViewById(R.id.Friday3);


        Sat1 = findViewById(R.id.Saturday1);
        Sat2 = findViewById(R.id.Saturday2);
        Sat3 = findViewById(R.id.Saturday3);


        Sun1 = findViewById(R.id.Sunday1);
        Sun2 = findViewById(R.id.Sunday2);
        Sun3 = findViewById(R.id.Sunday3);

        morningSstart = findViewById(R.id.morningSstart);
        morningSend = findViewById(R.id.morningSend);
        afternoonSstart = findViewById(R.id.afternoonSstart);
        afternoonSend = findViewById(R.id.afternoonSend);
        eveningSstart = findViewById(R.id.eveningSstart);

        eveningSend = findViewById(R.id.eveningSend);


        morningSstart_tue = findViewById(R.id.morningSstart_tue);
        morningSend_tue = findViewById(R.id.morningSend_tue);
        afternoonSstart_tue = findViewById(R.id.afternoonSstart_tue);
        afternoonSend_tue = findViewById(R.id.afternoonSend_tue);
        eveningSstart_tue = findViewById(R.id.eveningSstart_tue);

        eveningSend_tue = findViewById(R.id.eveningSend_tue);

        morningSstart_wed = findViewById(R.id.morningSstart_wed);
        morningSend_wed = findViewById(R.id.morningSend_wed);
        afternoonSstart_wed = findViewById(R.id.afternoonSstart_wed);
        afternoonSend_wed = findViewById(R.id.afternoonSend_wed);
        eveningSstart_wed = findViewById(R.id.eveningSstart_wed);
        eveningSend_wed = findViewById(R.id.eveningSend_wed);

        morningSstart_thu = findViewById(R.id.morningSstart_thu);
        morningSend_thu = findViewById(R.id.morningSend_thu);
        afternoonSstart_thu = findViewById(R.id.afternoonSstart_thu);
        afternoonSend_thu = findViewById(R.id.afternoonSend_thu);
        eveningSstart_thu = findViewById(R.id.eveningSstart_thu);
        eveningSend_thu = findViewById(R.id.eveningSend_thu);

        morningSstart_fri = findViewById(R.id.morningSstart_fri);
        morningSend_fri = findViewById(R.id.morningSend_fri);
        afternoonSstart_fri = findViewById(R.id.afternoonSstart_fri);
        afternoonSend_fri = findViewById(R.id.afternoonSend_fri);
        eveningSstart_fri = findViewById(R.id.eveningSstart_fri);
        eveningSend_fri = findViewById(R.id.eveningSend_fri);

        morningSstart_sat = findViewById(R.id.morningSstart_sat);
        morningSend_sat = findViewById(R.id.morningSend_sat);
        afternoonSstart_sat = findViewById(R.id.afternoonSstart_sat);
        afternoonSend_sat = findViewById(R.id.afternoonSend_sat);
        eveningSstart_sat = findViewById(R.id.eveningSstart_sat);
        eveningSend_sat = findViewById(R.id.eveningSend_sat);

        morningSstart_sun = findViewById(R.id.morningSstart_sun);
        morningSend_sun = findViewById(R.id.morningSend_sun);
        afternoonSstart_sun = findViewById(R.id.afternoonSstart_sun);
        afternoonSend_sun = findViewById(R.id.afternoonSend_sun);
        eveningSstart_sun = findViewById(R.id.eveningSstart_sun);
        eveningSend_sun = findViewById(R.id.eveningSend_sun);



    }
}