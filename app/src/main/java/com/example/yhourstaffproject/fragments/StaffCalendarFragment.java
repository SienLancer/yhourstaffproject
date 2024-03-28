package com.example.yhourstaffproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.yhourstaffproject.R;
import com.example.yhourstaffproject.activities.CalendarActivity;
import com.example.yhourstaffproject.activities.WeekListActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StaffCalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StaffCalendarFragment extends Fragment {
    private View mView;
    Button view_timetable_btn, list_timetable_btn;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ValueEventListener listener;




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
        view_timetable_btn = mView.findViewById(R.id.view_timetable_btn);
        list_timetable_btn = mView.findViewById(R.id.list_timetable_btn);

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




}