package com.example.yhourstaffproject.fragments;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.yhourstaffproject.R;
import com.example.yhourstaffproject.activities.TimerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StaffHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StaffHomeFragment extends Fragment {
    private View mView;
    ImageButton scanQr_imgBtn;
    Button timer_btn;
    private Calendar currentTime;
    TextView scan_txt;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView =inflater.inflate(R.layout.fragment_staff_home, container, false);
        scanQr_imgBtn = mView.findViewById(R.id.scanQr_imgBtn);
        scan_txt = mView.findViewById(R.id.scan_txt);
        timer_btn = mView.findViewById(R.id.timer_btn);

        timer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TimerActivity.class);
                startActivity(intent);
                //addDataTimeKeeping();
            }
        });

        scanQr_imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndShowActivity(getContext());

            }
        });

        return mView;
    }
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
               if (isGranted){
                    showCamera();
               }else {

               }
            });

    private ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
       if (result.getContents()==null){
           Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
       }else {
           setResult(result.getContents());
       }
    });

    private void setResult(String contents) {

        Toast.makeText(getContext(), "Timekeeping successful!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity(), TimerActivity.class));
    }

    private void showCamera() {


        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(false);
        options.setPrompt("Scan QR");
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);

        qrCodeLauncher.launch(options);
    }

    public StaffHomeFragment() {
        // Required empty public constructor
    }


    public static StaffHomeFragment newInstance(String param1, String param2) {
        StaffHomeFragment fragment = new StaffHomeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    private void checkPermissionAndShowActivity(Context context) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
        )== PackageManager.PERMISSION_GRANTED){
            showCamera();
            LocalDate today = LocalDate.now();

            int year = today.getYear();
            int month = today.getMonthValue();
            int day = today.getDayOfMonth();
            String dateForTimeKeeping = day + "/" + month + "/" + year;

            String dateString = day + "/" + month + "/" + year;
            // Lấy giờ và phút hiện tại
            Calendar currentTime = Calendar.getInstance();
            int hour = currentTime.get(Calendar.HOUR_OF_DAY);
            int minute = currentTime.get(Calendar.MINUTE);

            // Thêm giờ và phút vào chuỗi dateString
            dateString += " " + hour + ":" + minute;

            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            String qrcode = dateString + userId;
            String encodedString = Base64.encodeToString(qrcode.getBytes(), Base64.DEFAULT);
            firebaseDatabase.getReference().child("QRCode").child("codescan").setValue(encodedString);
            addDataTimeKeeping();
        }else if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show();
        }else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//
//        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (intentResult != null){
//            String a = intentResult.getContents();
//            if (a == null){
//                content.setText("null");
//            }else {
//                content.setText("not null");
//            }
//        }else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }

    public void addDataTimeKeeping() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        long timestamp = System.currentTimeMillis();
        if (user != null) {
            firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    LocalDateTime now = LocalDateTime.now();
                    int year = now.getYear();
                    int month = now.getMonthValue();
                    int day = now.getDayOfMonth();
                    int hour = now.getHour();
                    int minute = now.getMinute();
                    String minuteFormatted = String.format("%02d", minute);

                    String dateForTimeKeeping = day + " " + month + " " + year + " " + hour + ":" + minuteFormatted;
                    String dateForCheckIn = day + "/" + month + "/" + year + " " + hour + ":" + minuteFormatted;

                    DatabaseReference userReference = firebaseDatabase.getReference("User").child(userId).child("timekeeping");

                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String id = timestamp + dateForTimeKeeping; // Tạo id mới
                            DatabaseReference newTimekeepingRef = userReference.child(id);

                            if (!snapshot.child(id).exists()) {
                                String checkIn = dateForCheckIn; // Giờ check in mặc định
                                String checkOut = ""; // Không có giờ check out khi mới thêm

                                Map<String, Object> timekeepingData = new HashMap<>();
                                timekeepingData.put("id", id);
                                timekeepingData.put("checkIn", checkIn);
                                timekeepingData.put("checkOut", checkOut);

                                newTimekeepingRef.setValue(timekeepingData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Data added successfully");
                                        } else {
                                            Log.d(TAG, "Failed to add data");
                                        }
                                    }
                                });
                            } else {
                                Log.d(TAG, "Data already exists for today");
                                Toast.makeText(getContext(), "Data already exists for today", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
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





}