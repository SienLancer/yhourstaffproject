package com.example.yhourstaffproject.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.yhourstaffproject.R;
import com.example.yhourstaffproject.activities.CalendarActivity;
import com.example.yhourstaffproject.activities.WeekListActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StaffCalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StaffCalendarFragment extends Fragment {
    private View mView;
    ViewFlipper viewFlipper;
    TextView start_end_date_tv;
    Button view_timetable_btn, list_timetable_btn;
    EditText ip_shift_et;
    Button add_shift_btn,cancel_btn;
    Dialog dialog;
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
            morningSstart_sun, morningSend_sun, afternoonSstart_sun, afternoonSend_sun, eveningSstart_sun, eveningSend_sun;






    public StaffCalendarFragment() {
        // Required empty public constructor
    }


    public static StaffCalendarFragment newInstance(String param1, String param2) {
        StaffCalendarFragment fragment = new StaffCalendarFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_staff_calendar, container, false);
        init();

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
                            viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left_viewfliper));
                            viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left_viewfliper));
                            viewFlipper.showPrevious();
                        } else if (startX > endX) {
                            // Vuốt sang trái
                            viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right_viewfliper));
                            viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right_viewfliper));
                            viewFlipper.showNext();
                        }
                        break;
                }
                return true;
            }




        });


        viewFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext(); // Click để chuyển đến view tiếp theo
            }
        });

        list_timetable_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WeekListActivity.class);
                startActivity(intent);
            }
        });

        view_timetable_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalendarActivity.class);
                startActivity(intent);
            }
        });






        return mView;
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
                        shopRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Lấy tất cả các tuần
                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                DataSnapshot lastWeekSnapshot = null;

                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                for (DataSnapshot weekSnapshot : weeks) {
                                    lastWeekSnapshot = weekSnapshot;
                                }

                                if (lastWeekSnapshot != null) {
                                    // Hiển thị dữ liệu từ tuần cuối cùng lên giao diện người dùng
                                    // Lấy dữ liệu từ tuần cuối cùng và hiển thị lên giao diện
                                    start_end_date_tv.setText(lastWeekSnapshot.child("startDay").getValue(String.class) + " - " + lastWeekSnapshot.child("endDay").getValue(String.class));
                                    Mon1.setText(lastWeekSnapshot.child("mon1").getValue(String.class));
                                    Mon2.setText(lastWeekSnapshot.child("mon2").getValue(String.class));
                                    Mon3.setText(lastWeekSnapshot.child("mon3").getValue(String.class));
                                    Tue1.setText(lastWeekSnapshot.child("tue1").getValue(String.class));
                                    Tue2.setText(lastWeekSnapshot.child("tue2").getValue(String.class));
                                    Tue3.setText(lastWeekSnapshot.child("tue3").getValue(String.class));
                                    Wed1.setText(lastWeekSnapshot.child("wed1").getValue(String.class));
                                    Wed2.setText(lastWeekSnapshot.child("wed2").getValue(String.class));
                                    Wed3.setText(lastWeekSnapshot.child("wed3").getValue(String.class));
                                    Thu1.setText(lastWeekSnapshot.child("thu1").getValue(String.class));
                                    Thu2.setText(lastWeekSnapshot.child("thu2").getValue(String.class));
                                    Thu3.setText(lastWeekSnapshot.child("thu3").getValue(String.class));
                                    Fri1.setText(lastWeekSnapshot.child("fri1").getValue(String.class));
                                    Fri2.setText(lastWeekSnapshot.child("fri2").getValue(String.class));
                                    Fri3.setText(lastWeekSnapshot.child("fri3").getValue(String.class));
                                    Sat1.setText(lastWeekSnapshot.child("sat1").getValue(String.class));
                                    Sat2.setText(lastWeekSnapshot.child("sat2").getValue(String.class));
                                    Sat3.setText(lastWeekSnapshot.child("sat3").getValue(String.class));
                                    Sun1.setText(lastWeekSnapshot.child("sun1").getValue(String.class));
                                    Sun2.setText(lastWeekSnapshot.child("sun2").getValue(String.class));
                                    Sun3.setText(lastWeekSnapshot.child("sun3").getValue(String.class));

                                    morningSstart.setText(lastWeekSnapshot.child("morningSStart").getValue(String.class));
                                    morningSend.setText(lastWeekSnapshot.child("morningSend").getValue(String.class));
                                    afternoonSstart.setText(lastWeekSnapshot.child("afternoonSStart").getValue(String.class));
                                    afternoonSend.setText(lastWeekSnapshot.child("afternoonSend").getValue(String.class));
                                    eveningSstart.setText(lastWeekSnapshot.child("eveningSStart").getValue(String.class));
                                    eveningSend.setText(lastWeekSnapshot.child("eveningSend").getValue(String.class));
                                    morningSstart_tue.setText(lastWeekSnapshot.child("morningSStart").getValue(String.class));
                                    morningSend_tue.setText(lastWeekSnapshot.child("morningSend").getValue(String.class));
                                    afternoonSstart_tue.setText(lastWeekSnapshot.child("afternoonSStart").getValue(String.class));
                                    afternoonSend_tue.setText(lastWeekSnapshot.child("afternoonSend").getValue(String.class));
                                    eveningSstart_tue.setText(lastWeekSnapshot.child("eveningSStart").getValue(String.class));
                                    eveningSend_tue.setText(lastWeekSnapshot.child("eveningSend").getValue(String.class));
                                    morningSstart_wed.setText(lastWeekSnapshot.child("morningSStart").getValue(String.class));
                                    morningSend_wed.setText(lastWeekSnapshot.child("morningSend").getValue(String.class));
                                    afternoonSstart_wed.setText(lastWeekSnapshot.child("afternoonSStart").getValue(String.class));
                                    afternoonSend_wed.setText(lastWeekSnapshot.child("afternoonSend").getValue(String.class));
                                    eveningSstart_wed.setText(lastWeekSnapshot.child("eveningSStart").getValue(String.class));
                                    eveningSend_wed.setText(lastWeekSnapshot.child("eveningSend").getValue(String.class));
                                    morningSstart_thu.setText(lastWeekSnapshot.child("morningSStart").getValue(String.class));
                                    morningSend_thu.setText(lastWeekSnapshot.child("morningSend").getValue(String.class));
                                    afternoonSstart_thu.setText(lastWeekSnapshot.child("afternoonSStart").getValue(String.class));
                                    afternoonSend_thu.setText(lastWeekSnapshot.child("afternoonSend").getValue(String.class));
                                    eveningSstart_thu.setText(lastWeekSnapshot.child("eveningSStart").getValue(String.class));
                                    eveningSend_thu.setText(lastWeekSnapshot.child("eveningSend").getValue(String.class));
                                    morningSstart_fri.setText(lastWeekSnapshot.child("morningSStart").getValue(String.class));
                                    morningSend_fri.setText(lastWeekSnapshot.child("morningSend").getValue(String.class));
                                    afternoonSstart_fri.setText(lastWeekSnapshot.child("afternoonSStart").getValue(String.class));
                                    afternoonSend_fri.setText(lastWeekSnapshot.child("afternoonSend").getValue(String.class));
                                    eveningSstart_fri.setText(lastWeekSnapshot.child("eveningSStart").getValue(String.class));
                                    eveningSend_fri.setText(lastWeekSnapshot.child("eveningSend").getValue(String.class));
                                    morningSstart_sat.setText(lastWeekSnapshot.child("morningSStart").getValue(String.class));
                                    morningSend_sat.setText(lastWeekSnapshot.child("morningSend").getValue(String.class));
                                    afternoonSstart_sat.setText(lastWeekSnapshot.child("afternoonSStart").getValue(String.class));
                                    afternoonSend_sat.setText(lastWeekSnapshot.child("afternoonSend").getValue(String.class));
                                    eveningSstart_sat.setText(lastWeekSnapshot.child("eveningSStart").getValue(String.class));
                                    eveningSend_sat.setText(lastWeekSnapshot.child("eveningSend").getValue(String.class));
                                    morningSstart_sun.setText(lastWeekSnapshot.child("morningSStart").getValue(String.class));
                                    morningSend_sun.setText(lastWeekSnapshot.child("morningSend").getValue(String.class));
                                    afternoonSstart_sun.setText(lastWeekSnapshot.child("afternoonSStart").getValue(String.class));
                                    afternoonSend_sun.setText(lastWeekSnapshot.child("afternoonSend").getValue(String.class));
                                    eveningSstart_sun.setText(lastWeekSnapshot.child("eveningSStart").getValue(String.class));
                                    eveningSend_sun.setText(lastWeekSnapshot.child("eveningSend").getValue(String.class));

                                    // Tiếp tục với các TextView khác tương tự
                                    // ...
                                } else {
                                    Toast.makeText(getContext(), "No weeks found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Shop ID not found for this user", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    public void init(){
        view_timetable_btn = mView.findViewById(R.id.view_timetable_btn);
        list_timetable_btn = mView.findViewById(R.id.list_timetable_btn);
        viewFlipper = mView.findViewById(R.id.view_flipper);
        start_end_date_tv = mView.findViewById(R.id.start_end_date_tv);


        Mon1=mView.findViewById(R.id.Monday1);
        Mon2=mView.findViewById(R.id.Monday2);
        Mon3=mView.findViewById(R.id.Monday3);

        Tue1=mView.findViewById(R.id.Tuesday1);
        Tue2=mView.findViewById(R.id.Tuesday2);
        Tue3=mView.findViewById(R.id.Tuesday3);

        Wed1 = mView.findViewById(R.id.Wednesday1);
        Wed2 = mView.findViewById(R.id.Wednesday2);
        Wed3 = mView.findViewById(R.id.Wednesday3);

        Thu1 = mView.findViewById(R.id.Thursday1);
        Thu2 = mView.findViewById(R.id.Thursday2);
        Thu3 = mView.findViewById(R.id.Thursday3);

        Fri1 = mView.findViewById(R.id.Friday1);
        Fri2 = mView.findViewById(R.id.Friday2);
        Fri3 = mView.findViewById(R.id.Friday3);

        Sat1 = mView.findViewById(R.id.Saturday1);
        Sat2 = mView.findViewById(R.id.Saturday2);
        Sat3 = mView.findViewById(R.id.Saturday3);

        Sun1 = mView.findViewById(R.id.Sunday1);
        Sun2 = mView.findViewById(R.id.Sunday2);
        Sun3 = mView.findViewById(R.id.Sunday3);

        morningSstart=mView.findViewById(R.id.morningSstart);
        morningSend=mView.findViewById(R.id.morningSend);
        afternoonSstart=mView.findViewById(R.id.afternoonSstart);
        afternoonSend=mView.findViewById(R.id.afternoonSend);
        eveningSstart=mView.findViewById(R.id.eveningSstart);
        eveningSend=mView.findViewById(R.id.eveningSend);

        morningSstart_tue = mView.findViewById(R.id.morningSstart_tue);
        morningSend_tue = mView.findViewById(R.id.morningSend_tue);
        afternoonSstart_tue = mView.findViewById(R.id.afternoonSstart_tue);
        afternoonSend_tue = mView.findViewById(R.id.afternoonSend_tue);
        eveningSstart_tue = mView.findViewById(R.id.eveningSstart_tue);
        eveningSend_tue = mView.findViewById(R.id.eveningSend_tue);

        morningSstart_wed = mView.findViewById(R.id.morningSstart_wed);
        morningSend_wed = mView.findViewById(R.id.morningSend_wed);
        afternoonSstart_wed = mView.findViewById(R.id.afternoonSstart_wed);
        afternoonSend_wed = mView.findViewById(R.id.afternoonSend_wed);
        eveningSstart_wed = mView.findViewById(R.id.eveningSstart_wed);
        eveningSend_wed = mView.findViewById(R.id.eveningSend_wed);

        morningSstart_thu = mView.findViewById(R.id.morningSstart_thu);
        morningSend_thu = mView.findViewById(R.id.morningSend_thu);
        afternoonSstart_thu = mView.findViewById(R.id.afternoonSstart_thu);
        afternoonSend_thu = mView.findViewById(R.id.afternoonSend_thu);
        eveningSstart_thu = mView.findViewById(R.id.eveningSstart_thu);
        eveningSend_thu = mView.findViewById(R.id.eveningSend_thu);

        morningSstart_fri = mView.findViewById(R.id.morningSstart_fri);
        morningSend_fri = mView.findViewById(R.id.morningSend_fri);
        afternoonSstart_fri = mView.findViewById(R.id.afternoonSstart_fri);
        afternoonSend_fri = mView.findViewById(R.id.afternoonSend_fri);
        eveningSstart_fri = mView.findViewById(R.id.eveningSstart_fri);
        eveningSend_fri = mView.findViewById(R.id.eveningSend_fri);

        morningSstart_sat = mView.findViewById(R.id.morningSstart_sat);
        morningSend_sat = mView.findViewById(R.id.morningSend_sat);
        afternoonSstart_sat = mView.findViewById(R.id.afternoonSstart_sat);
        afternoonSend_sat = mView.findViewById(R.id.afternoonSend_sat);
        eveningSstart_sat = mView.findViewById(R.id.eveningSstart_sat);
        eveningSend_sat = mView.findViewById(R.id.eveningSend_sat);

        morningSstart_sun = mView.findViewById(R.id.morningSstart_sun);
        morningSend_sun = mView.findViewById(R.id.morningSend_sun);
        afternoonSstart_sun = mView.findViewById(R.id.afternoonSstart_sun);
        afternoonSend_sun = mView.findViewById(R.id.afternoonSend_sun);
        eveningSstart_sun = mView.findViewById(R.id.eveningSstart_sun);
        eveningSend_sun = mView.findViewById(R.id.eveningSend_sun);


    }


}