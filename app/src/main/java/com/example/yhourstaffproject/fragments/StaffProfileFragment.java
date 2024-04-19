package com.example.yhourstaffproject.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.yhourstaffproject.R;
import com.example.yhourstaffproject.activities.ChangePasswordActivity;
import com.example.yhourstaffproject.activities.SignInForStaffActivity;
import com.example.yhourstaffproject.activities.UpdateProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class StaffProfileFragment extends Fragment {

    private View mView;
    ImageView avatar_img;
    TextView staff_name_tv, staff_email_tv, staff_phone_tv, staff_address_tv,
            staff_dob_tv, staff_hourly_salary_tv, staff_position_tv;

    Button logoutS_btn, profile_change_password_btn, edit_profile_btn;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public StaffProfileFragment() {
        // Required empty public constructor
    }


    public static StaffProfileFragment newInstance(String param1, String param2) {
        StaffProfileFragment fragment = new StaffProfileFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_staff_profile, container, false);
        logoutS_btn = mView.findViewById(R.id.logoutS_btn);
        staff_name_tv = mView.findViewById(R.id.staff_name_tv);
        staff_email_tv = mView.findViewById(R.id.staff_email_tv);
        avatar_img = mView.findViewById(R.id.avatar_img);
        staff_dob_tv = mView.findViewById(R.id.staff_dob_tv);
        staff_hourly_salary_tv = mView.findViewById(R.id.staff_hourly_salary_tv);
        staff_position_tv = mView.findViewById(R.id.staff_position_tv);
        staff_address_tv = mView.findViewById(R.id.staff_address_tv);
        staff_phone_tv = mView.findViewById(R.id.staff_phone_tv);
        profile_change_password_btn = mView.findViewById(R.id.profile_change_password_btn);
        edit_profile_btn = mView.findViewById(R.id.profile_edit_btn);

        profile_change_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
                startActivity(intent);
            }
        });

        logoutS_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), SignInForStaffActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        getUsername();
        return mView;
    }


    public void getUsername() {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            if (user != null) {
                DatabaseReference userReference = firebaseDatabase.getReference("User").child(userId);

                userReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.exists()) {
                                String name = snapshot.child("name").getValue(String.class);
                                staff_name_tv.setText(name);
                                String email = snapshot.child("email").getValue(String.class);
                                staff_email_tv.setText(email);
                                String dob = snapshot.child("dateOfBirth").getValue(String.class);
                                staff_dob_tv.setText(dob);
                                Integer hourlySalary = snapshot.child("hourlySalary").getValue(Integer.class);
                                staff_hourly_salary_tv.setText(hourlySalary+" VND");
                                String position = snapshot.child("position").getValue(String.class);
                                staff_position_tv.setText(position);
                                String address = snapshot.child("address").getValue(String.class);
                                staff_address_tv.setText(address);
                                Integer phone = snapshot.child("phoneNumber").getValue(Integer.class);
                                staff_phone_tv.setText("+84 "+phone);
                            } else {
                                Toast.makeText(getContext(), "Data doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showUserInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        if (name == null ){

            staff_name_tv.setText("Anonymous");
        }else {
            staff_name_tv.setVisibility(View.VISIBLE);
            staff_name_tv.setText(name);

        }

        staff_email_tv.setText(email);
        //avatar_img.setImageURI(photoUrl);
        Glide.with(getContext()).load(photoUrl).error(R.drawable.ava_de).into(avatar_img);

    }


}