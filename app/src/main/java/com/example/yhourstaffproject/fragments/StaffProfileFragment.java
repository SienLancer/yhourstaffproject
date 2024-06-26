package com.example.yhourstaffproject.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.yhourstaffproject.R;
import com.example.yhourstaffproject.activities.ChangePasswordActivity;
import com.example.yhourstaffproject.activities.SalaryActivity;
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
    Dialog dialog;
    TextView staff_name_tv, staff_email_tv, staff_phone_tv, staff_address_tv,
            staff_dob_tv, staff_hourly_salary_tv, staff_position_tv,
            owner_shop_phone_tv, owner_shop_email_tv, owner_shop_name_tv, owner_shop_address_tv,
            dialog_title, dialog_message;

    Button profile_change_password_btn, edit_profile_btn, button_yes, button_no;
    ImageButton logoutS_btn;
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
        staff_dob_tv = mView.findViewById(R.id.staff_dob_tv);
        staff_hourly_salary_tv = mView.findViewById(R.id.staff_hourly_salary_tv);
        staff_position_tv = mView.findViewById(R.id.staff_position_tv);
        staff_address_tv = mView.findViewById(R.id.staff_address_tv);
        staff_phone_tv = mView.findViewById(R.id.staff_phone_tv);
        profile_change_password_btn = mView.findViewById(R.id.profile_change_password_btn);
        edit_profile_btn = mView.findViewById(R.id.profile_edit_btn);
        owner_shop_name_tv = mView.findViewById(R.id.owner_shop_name_tv);
        owner_shop_address_tv = mView.findViewById(R.id.owner_shop_address_tv);
        owner_shop_phone_tv = mView.findViewById(R.id.owner_shop_phone_tv);
        owner_shop_email_tv = mView.findViewById(R.id.owner_shop_email_tv);

        dialog=new Dialog(getContext());
        dialog.setContentView(R.layout.custom_yes_no_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_title = dialog.findViewById(R.id.dialog_title);
        dialog_message = dialog.findViewById(R.id.dialog_message);

        dialog_title.setText("Log out");
        dialog_message.setText("Do you want to log out?");
        button_yes =dialog.findViewById(R.id.button_yes);
        button_no =dialog.findViewById(R.id.button_no);
        edit_profile_btn.setBackgroundColor(Color.TRANSPARENT);
        edit_profile_btn.setTextSize(14);
        profile_change_password_btn.setBackgroundColor(Color.TRANSPARENT);

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
        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), SignInForStaffActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        logoutS_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        getUsername();
        getShopInfo();
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

    public void getShopInfo() {
        try {
            //loadDialog.show();
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            if (user != null) {
                firebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                        if (ownerShopId != null) {
                            firebaseDatabase.getReference().child("Shop").child(ownerShopId)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String shopName = snapshot.child("name").getValue(String.class);
                                            String shopEmail = snapshot.child("email").getValue(String.class);
                                            String shopAddress = snapshot.child("address").getValue(String.class);
                                            Integer shopPhone = snapshot.child("phoneNumber").getValue(Integer.class); // Retrieve as Integer

                                            owner_shop_name_tv.setText(shopName);
                                            owner_shop_address_tv.setText(shopAddress);
                                            owner_shop_email_tv.setText(shopEmail);
                                            owner_shop_phone_tv.setText("+84 "+shopPhone); // Convert Integer to String before setting
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                        } else {
                            Toast.makeText(getContext(), "Shop not found", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
        }
    }



}