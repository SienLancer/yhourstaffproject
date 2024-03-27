package com.example.yhourstaffproject.fragments;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.yhourstaffproject.R;
import com.example.yhourstaffproject.activities.SalaryActivity;
import com.example.yhourstaffproject.activities.TimerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class StaffHomeFragment extends Fragment {
    private View mView;
    ImageButton scanQr_imgBtn;
    Button timer_btn;
    ImageView total_salary_imgv;
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
        total_salary_imgv = mView.findViewById(R.id.total_salary_imgv);
        setupTimerButtonVisibilityListener();

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

        total_salary_imgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getActivity(), SalaryActivity.class);
            startActivity(intent);
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
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        if (user != null) {
            firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                    if(ownerShopId != null){
                        firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("QRCode").child("codeScan")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String realtimeqr = snapshot.getValue(String.class);
                                        if (contents.equals(realtimeqr)){
                                            Toast.makeText(getContext(), "Scan successful!", Toast.LENGTH_SHORT).show();
                                            addDataTimeKeeping();
                                            startActivity(new Intent(getActivity(), TimerActivity.class));

                                        }else {
                                            Toast.makeText(getContext(), "Scan failed!", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                    else {
                        Toast.makeText(getContext(), "Shop not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
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
            setDataQrCode();
        }else if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show();
        }else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }


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

    public void setupTimerButtonVisibilityListener() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userReference = firebaseDatabase.getReference("User").child(userId).child("timekeeping");

            Query query = userReference.orderByChild("timestamp").limitToLast(1);

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        boolean checkoutExists = false;
                        // Kiểm tra nếu có ít nhất một mục có trường checkout không rỗng
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            String checkoutTime = childSnapshot.child("checkOut").getValue(String.class);
                            if (checkoutTime != null && !checkoutTime.isEmpty()) {
                                checkoutExists = true;
                                break;
                            }
                        }
                        // Cập nhật giao diện dựa trên tồn tại của checkout
                        if (checkoutExists) {
                            timer_btn.setVisibility(View.GONE);
                            scanQr_imgBtn.setVisibility(View.VISIBLE);
                            scan_txt.setText("Scan QR");
                        } else {
                            timer_btn.setVisibility(View.VISIBLE);
                            scanQr_imgBtn.setVisibility(View.GONE);
                            scan_txt.setText("On shift");
                        }
                    } else {
                        // Không có dữ liệu, hiển thị nút Timer
                        timer_btn.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi nếu có
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

            // Gắn lắng nghe vào truy vấn
            query.addValueEventListener(valueEventListener);

            // Lưu trữ ValueEventListener để có thể loại bỏ lắng nghe sau này nếu cần
            // (ví dụ: trong phương thức onDestroy của Activity hoặc Fragment)
            // Đảm bảo rằng bạn cần loại bỏ lắng nghe khi không cần thiết để tránh rò rỉ bộ nhớ.
            // Đối với Fragment, bạn có thể sử dụng onViewCreated hoặc onCreate để gắn lắng nghe
            // và sử dụng onDestroyView hoặc onDestroy để loại bỏ nó.
            // Đối với Activity, bạn có thể sử dụng onStart và onStop tương tự.
            // Ví dụ:
            // query.removeEventListener(valueEventListener);
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    public void setDataQrCode(){

        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        if (user != null) {
            firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                    if(ownerShopId != null){
                        DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("QRCode").child("codeScan");
                        // Thực hiện thay đổi dữ liệu
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


                        String qrcode = dateString + userId;
                        String encodedString = Base64.encodeToString(qrcode.getBytes(), Base64.DEFAULT);
                        shopRef.setValue(encodedString).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "QR Code updated successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Failed to update QR Code", Toast.LENGTH_SHORT).show();
                                }
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

    }






}