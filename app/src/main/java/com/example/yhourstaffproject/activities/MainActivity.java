package com.example.yhourstaffproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yhourstaffproject.R;

public class MainActivity extends AppCompatActivity {
    Button ownerRoleBtn, staffRoleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ownerRoleBtn = findViewById(R.id.ownerRoleBtn);
        staffRoleBtn = findViewById(R.id.staffRoleBtn);



        staffRoleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent b = new Intent(MainActivity.this, SignInForStaffActivity.class);
                startActivity(b);

            }
        });
    }
}