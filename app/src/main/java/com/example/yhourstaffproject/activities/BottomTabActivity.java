package com.example.yhourstaffproject.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.yhourstaffproject.R;
import com.example.yhourstaffproject.fragments.StaffCalendarFragment;
import com.example.yhourstaffproject.fragments.StaffHomeFragment;
import com.example.yhourstaffproject.fragments.StaffProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomTabActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_tab);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        replaceFragment(new StaffHomeFragment());
        bottomNavigationView.getMenu().findItem(R.id.home_staff).setChecked(true);


        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home_staff) {
                replaceFragment(new StaffHomeFragment());
            } else if (id == R.id.calender_staff) {
                replaceFragment(new StaffCalendarFragment());
            }else if (id == R.id.profile_staff) {
                replaceFragment(new StaffProfileFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}