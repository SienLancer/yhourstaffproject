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

import androidx.fragment.app.Fragment;

import com.example.yhourstaffproject.R;
import com.example.yhourstaffproject.activities.SignInForStaffActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class StaffProfileFragment extends Fragment {

    private View mView;
    private ImageView avatar_img;
    private TextView staff_name_tv, staff_email_tv;

    Button logoutS_btn;
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


        logoutS_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), SignInForStaffActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        showUserInfo();
        return mView;
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
        avatar_img.setImageURI(photoUrl);

    }
}