package com.example.yhourstaffproject.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class ChangePasswordActivity extends AppCompatActivity {
    EditText new_password_edt, re_new_password_edt, old_password_edt;
    Button change_password_btn;
    ImageButton back_imgBtn;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        new_password_edt = findViewById(R.id.new_password_edt);
        re_new_password_edt = findViewById(R.id.re_new_password_edt);
        old_password_edt = findViewById(R.id.old_password_edt);
        change_password_btn = findViewById(R.id.change_password_btn);
        back_imgBtn = findViewById(R.id.back_imgBtn);


        change_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        back_imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    public void changePassword() {
        try {


            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            if (user != null) {
                DatabaseReference userReference = firebaseDatabase.getReference("User").child(userId);
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.exists()) {
                                String oldPassFb = snapshot.child("password").getValue(String.class);
                                Log.d("TAG", "onDataChange: " + oldPassFb);
                                //Toast.makeText(ChangePasswordActivity.this, "Data exist", Toast.LENGTH_SHORT).show();
                                String oldPass = old_password_edt.getText().toString();
                                String newPass = new_password_edt.getText().toString();
                                String reNewPass = re_new_password_edt.getText().toString();
                                //Log.d("TAG", "onDataChange: " + oldPass);
                                if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(reNewPass)) {
                                    Toast.makeText(ChangePasswordActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                                }else {
                                    if(oldPass.length() < 6 || newPass.length() < 6 || reNewPass.length() < 6){
                                        Toast.makeText(ChangePasswordActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                                    }else {
                                        if (newPass.equals(reNewPass)) {
                                            if (newPass.equals(oldPassFb)){
                                                Toast.makeText(ChangePasswordActivity.this, "New password cannot be the same as the old password", Toast.LENGTH_SHORT).show();
                                            }else {
                                                if (oldPass.equals(oldPassFb)) {
                                                    user.updatePassword(newPass)
                                                            .addOnCompleteListener(task -> {
                                                                if (task.isSuccessful()) {
                                                                    // Password updated successfully
                                                                    userReference.child("password").setValue(newPass);
                                                                    Toast.makeText(ChangePasswordActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                                    finish();
                                                                } else {
                                                                    // Failed to update password
                                                                    Toast.makeText(ChangePasswordActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                                                    Log.d("TAG", "onDataChange: " + task.getException());
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(ChangePasswordActivity.this, "The old password is incorrect", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                        } else {
                                            Toast.makeText(ChangePasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, "Data doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChangePasswordActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(ChangePasswordActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}