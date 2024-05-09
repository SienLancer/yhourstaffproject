package com.example.yhourstaffproject.activities;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.yhourstaffproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class UpdateProfileActivity extends AppCompatActivity {
    EditText staff_name_update_edt, staff_phone_update_edt, staff_address_update_edt;
    TextView staff_dob_update_edt;
    Button update_profile_btn;
    ImageButton backSignUpS_imgBtn;
    ImageView loading_imgv;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    AlertDialog loadDialog;
    Animation animation;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_profile);
        staff_name_update_edt = findViewById(R.id.staff_name_update_edt);
        staff_dob_update_edt = findViewById(R.id.staff_dob_update_edt);
        staff_phone_update_edt = findViewById(R.id.staff_phone_update_edt);
        staff_address_update_edt = findViewById(R.id.staff_address_update_edt);
        update_profile_btn = findViewById(R.id.update_profile_btn);
        backSignUpS_imgBtn = findViewById(R.id.backSignUpS_imgBtn);
        loadDialog();
        getUsername();

        backSignUpS_imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        update_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        dateSetListener = (datePicker, year, month, day) -> {
            month = month +1;
            Log.d(TAG, "onDateSet: dd/mm/yyyy " + day + "/" + month + "/" + year);
            String date = day + "/" + month + "/" + year;
            staff_dob_update_edt.setText(date);

        };
        staff_dob_update_edt.setOnClickListener(view -> {
            Calendar kal = Calendar.getInstance();
            int year = kal.get(Calendar.YEAR);
            int month = kal.get(Calendar.MONTH);
            int day = kal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog =new DatePickerDialog(UpdateProfileActivity.this, android.R.style.Theme_DeviceDefault_Dialog,
                    dateSetListener, year, month, day);
            dialog.show();
        });

    }
    public void loadDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileActivity.this);
        builder.setCancelable(false); // Tùy chỉnh tùy theo nhu cầu của bạn
        View view = getLayoutInflater().inflate(R.layout.custom_loading_dialog, null);
        loading_imgv = view.findViewById(R.id.loading_imgv);

        builder.setView(view);
        loadDialog = builder.create();
        //dialog.getWindow().setWindowAnimations(R.style.RotateAnimation);
        loadDialog.getWindow().setLayout(130, 130);
        loadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        animation = AnimationUtils.loadAnimation(UpdateProfileActivity.this, R.anim.rotate_animation);
        loading_imgv.startAnimation(animation);
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
                                staff_name_update_edt.setText(name);
                                String dob = snapshot.child("dateOfBirth").getValue(String.class);
                                staff_dob_update_edt.setText(dob);
                                String address = snapshot.child("address").getValue(String.class);
                                staff_address_update_edt.setText(address);
                                Integer phone = snapshot.child("phoneNumber").getValue(Integer.class);
                                staff_phone_update_edt.setText(phone+"");
                            } else {
                                Toast.makeText(UpdateProfileActivity.this, "Data doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UpdateProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(UpdateProfileActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProfile() {
        try {
            loadDialog.show();
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            if (user != null) {
                DatabaseReference userReference = firebaseDatabase.getReference("User").child(userId);

                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            String name = staff_name_update_edt.getText().toString().trim();
                            String dob = staff_dob_update_edt.getText().toString().trim();
                            String phoneStr = staff_phone_update_edt.getText().toString().trim();
                            String address = staff_address_update_edt.getText().toString().trim();

                            if (name.isEmpty() || dob.isEmpty() || phoneStr.isEmpty() || address.isEmpty()) {
                                // Kiểm tra xem bất kỳ trường nào có trống không
                                Toast.makeText(UpdateProfileActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Kiểm tra độ dài của số điện thoại
                            if (phoneStr.length() > 10) {
                                Toast.makeText(UpdateProfileActivity.this, "Phone number should not exceed 10 digits", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Chuyển đổi số điện thoại sang kiểu Integer
                            Integer phone = Integer.valueOf(phoneStr);

                            // Cập nhật dữ liệu vào cơ sở dữ liệu
                            userReference.child("name").setValue(name);
                            userReference.child("dateOfBirth").setValue(dob);
                            userReference.child("phoneNumber").setValue(phone);
                            userReference.child("address").setValue(address);

                            // Hiển thị Toast khi cập nhật thành công
                            Toast.makeText(UpdateProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            loadDialog.dismiss();
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UpdateProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(UpdateProfileActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}