package com.example.yhourstaffproject.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yhourstaffproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInForStaffActivity extends AppCompatActivity {
    ImageButton backSignInS_imgBtn;
    EditText usernameSLogin_edt, pwSLogin_edt;
    Button loginS_btn;
    private FirebaseAuth mAuth;
    ImageView loading_imgv;
    AlertDialog dialog;
    Animation animation;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_for_staff);

        backSignInS_imgBtn = findViewById(R.id.backSignInS_imgBtn);

        backSignInS_imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        backSignInS_imgBtn = findViewById(R.id.backSignInS_imgBtn);
        loginS_btn = findViewById(R.id.loginS_btn);
        pwSLogin_edt = findViewById(R.id.pwSLogin_edt);
        usernameSLogin_edt = findViewById(R.id.usernameSLogin_edt);
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Loading");
//        progressDialog.setMessage("please wait...");
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        loadDialog();








        loginS_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });


    }

    public void loadDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // Tùy chỉnh tùy theo nhu cầu của bạn
        View view = getLayoutInflater().inflate(R.layout.custom_loading_dialog, null);
        loading_imgv = view.findViewById(R.id.loading_imgv);

        builder.setView(view);
        dialog = builder.create();
        //dialog.getWindow().setWindowAnimations(R.style.RotateAnimation);
        dialog.getWindow().setLayout(130, 130);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
        loading_imgv.startAnimation(animation);
    }

    private void login() {
        String username, password;
        username = usernameSLogin_edt.getText().toString();
        password = pwSLogin_edt.getText().toString();

        try {
            if (TextUtils.isEmpty(username)) {
                throw new IllegalArgumentException("Please enter your email!");
            }

            if (TextUtils.isEmpty(password)) {
                throw new IllegalArgumentException("Please enter your password!");
            }

            if (!isValidEmail(username)) {
                throw new IllegalArgumentException("Invalid email format!");
            }

            if (!isValidPassword(password)) {
                throw new IllegalArgumentException("Password must be at least 6 characters long!");
            }

            dialog.show();
            mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    try {
                        if (task.isSuccessful()) {
                            String id = task.getResult().getUser().getUid();
                            firebaseDatabase.getReference().child("User").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        int role = snapshot.child("role").getValue(Integer.class);
                                        int availabilityStatus = snapshot.child("availabilityStatus").getValue(Integer.class);
                                        if (role == 1) {
                                            if (availabilityStatus == 1) {
                                                Intent i = new Intent(SignInForStaffActivity.this, BottomTabActivity.class);
                                                startActivity(i);
                                                dialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Your account is inactive at the shop", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Account not found", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Account not found", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getApplicationContext(), "Login failed!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Login failed!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Login failed!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

}